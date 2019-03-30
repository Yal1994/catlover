package cnyl.catlover.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cnyl.catlover.common.constants.MsgType;
import cnyl.catlover.common.util.CommonUtils;
import cnyl.catlover.common.util.SpringUtils;
import cnyl.catlover.domain.chat.ChatResponse;
import cnyl.catlover.domain.chat.ChatRoom;
import cnyl.catlover.domain.chat.Message;
import cnyl.catlover.domain.chat.MessageVo;
import cnyl.catlover.domain.chat.OnlineUser;
import cnyl.catlover.domain.user.User;
import cnyl.catlover.domain.user.UserToken;
import cnyl.catlover.repository.ChatRoomRepository;
import cnyl.catlover.repository.MessageRepository;
import cnyl.catlover.repository.UserRepository;
import cnyl.catlover.repository.UserTokenRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室
 * onOpen时:1.进行webId认证 2.记录在线  3.返回聊天室列表
 * onMessage:1.判断接收者是否在线 2.
 * 
 * @author Yal
 *
 */
@Component
@ServerEndpoint("/wx/chat")
@Slf4j
public class ChatSocket {
	
	private ChatResponse response = new ChatResponse();
	
	
	private UserTokenRepository tokenRepository = SpringUtils.getBean("userTokenRepository");
	
	private UserRepository userRepository = SpringUtils.getBean("userRepository");
	
	private ChatRoomRepository roomRepository = SpringUtils.getBean("chatRoomRepository");
	
	private MessageRepository messageRepository = SpringUtils.getBean("messageRepository");
	
	private static int onlineCount = 0;
	/**
	 * key: session创建者的webId<br>
	 * val: onlineUser
	 */
	private static ConcurrentHashMap<String, OnlineUser> onlineUserMap = new ConcurrentHashMap<String, OnlineUser>();
	
	private static CopyOnWriteArraySet<ChatSocket> onlineSocket = new CopyOnWriteArraySet<ChatSocket>();
	
	private Session currentSession;
	
	public static synchronized int getOnlineSession() {
		return onlineCount;
	}
	
	public static synchronized int addOnlineSession(){
		return onlineCount++;
	}
	
	public static synchronized int subOnlineSession(){
		return onlineCount--;
	}
	
	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}
	
	public void sendMessage(Object msg){
		log.info("sendMesg:{}",JSON.toJSONString(msg));
		try {
			this.currentSession.getBasicRemote().sendText(JSON.toJSONString(msg));
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sendMessageTo(Session reciver,Object msg){
		log.info("sendMesg:{}",JSON.toJSONString(msg));
		try {
			reciver.getBasicRemote().sendText(JSON.toJSONString(msg));
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(Session session, String msg){
		log.info("收到消息:{}",msg);
		log.info(session.toString());
		try {
			//接受到的消息应该有两种,一:单纯的接受到消息即转发消息,二更新聊天室状态即将未读状态撤销
			MessageVo msgVo = JSON.parseObject(msg, MessageVo.class);
			
			if(MsgType.IN_ROOM == msgVo.getMsgType()){
				//进入房间
				inRoom(session,msgVo);
			}
			
			if(MsgType.OUT_ROOM == msgVo.getMsgType()){
				//退出房间
				outRoom(session,msgVo);
			}
			
			if(MsgType.MSG == msgVo.getMsgType()){
				//发送消息
				invokeMsg(session,msgVo);
			}
			
			if(MsgType.ROOM_LIST == msgVo.getMsgType()){
				getRoomList(session,msgVo);
			}
						
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void getRoomList(Session session, MessageVo msgVo) {
		// TODO Auto-generated method stub
		String senderId = msgVo.getSenderId();
		Optional<User> ownerOpt = userRepository.findByWebId(senderId);
		if(ownerOpt.isPresent()){
			List<ChatRoom> roomList = roomRepository.findAllByOwnerOrderByHasNewDesc(ownerOpt.get());
			sendMessage(response.getRoomListResp(roomList));
		}
		
	}

	/**
	 * 处理消息类的指令
	 * @param session
	 * @param msgVo
	 */
	private void invokeMsg(Session session, MessageVo msgVo) {
		// TODO Auto-generated method stub
		String senderId = msgVo.getSenderId();
		String reciverId = msgVo.getReciverId();
		//String roomId = msgVo.getRoomId();
		String content = msgVo.getContent();
		//msgVo.getContentType();
		OnlineUser targetUser = onlineUserMap.get(reciverId);
		ChatRoom roomOpt = roomRepository.findByOwnerIdAndReciverId(reciverId,senderId);
		if(targetUser != null){
			//此时接收者在线,判断是否在房间
			String curRoom = targetUser.getCurRoom();
			if(!MsgType.NOT_IN_ROOM.equals(curRoom)){
				//此时在房间,判断是否在消息发送者的房间
				if(roomOpt != null){
					ChatRoom chatRoom = roomOpt;
					if(curRoom.equals(chatRoom.getId())){
						//此时两者在相同的房间,直接发送给接收者,不缓存消息
						Message msg = new Message();
						msg.setContent(content);
						msg.setReciverId(reciverId);
						msg.setReciverRoomId(chatRoom.getId());
						msg.setSenderId(senderId);
						msg.setSendTime(new Date());
						msg.setSimpleTime(msg.getSendTime());
						sendMessageTo(targetUser.getSession(), response.getMessageResp(msg));
					}else{
						//此时虽然在房间,但是不在同一房间,所以消息会被缓存
						Message msg = new Message();
						msg.setContent(content);
						msg.setReciverId(reciverId);
						msg.setReciverRoomId(chatRoom.getId());
						msg.setSenderId(senderId);
						msg.setSendTime(new Date());
						msg.setSimpleTime(msg.getSendTime());
						Message msgRet = messageRepository.save(msg);
						List<Message> msgList = chatRoom.getUnreadMessage();
						msgList.add(msgRet);
						chatRoom.setHasNew(true);
						chatRoom.setUnreadMessage(msgList);
						roomRepository.save(chatRoom);
						sendMessage(response.getMessageResp(msg));
					}
				}
			}else{
				//接收者在线,但不在房间
				if(roomOpt != null ){
					ChatRoom chatRoom = roomOpt;
					Message msg = new Message();
					msg.setContent(content);
					msg.setReciverId(reciverId);
					msg.setReciverRoomId(chatRoom.getId());
					msg.setSenderId(senderId);
					msg.setSendTime(new Date());
					msg.setSimpleTime(msg.getSendTime());
					Message msgRet = messageRepository.save(msg);
					List<Message> msgList = chatRoom.getUnreadMessage();
					msgList.add(msgRet);
					chatRoom.setHasNew(true);
					chatRoom.setUnreadMessage(msgList);
					roomRepository.save(chatRoom);
					sendMessage(response.getMessageResp(msg));
				}
			}
		}else{
			//此时接收者并没有开启socket,视为不在线
			if(roomOpt != null){
				ChatRoom chatRoom = roomOpt;
				Message msg = new Message();
				msg.setContent(content);
				msg.setReciverId(reciverId);
				msg.setReciverRoomId(chatRoom.getId());
				msg.setSenderId(senderId);
				msg.setSendTime(new Date());
				msg.setSimpleTime(msg.getSendTime());
				Message msgRet = messageRepository.save(msg);
				List<Message> msgList = chatRoom.getUnreadMessage();
				msgList.add(msgRet);
				chatRoom.setHasNew(true);
				chatRoom.setUnreadMessage(msgList);
				roomRepository.save(chatRoom);
				sendMessage(response.getMessageResp(msg));
			}
		}
	}
	/**
	 * 处理退出房间的指令
	 * @param session
	 * @param msgVo
	 */
	private void outRoom(Session session, MessageVo msgVo) {
		String senderId = msgVo.getSenderId();
		OnlineUser onlineUser = onlineUserMap.get(senderId);
		if(onlineUser != null){
			onlineUser.setCurRoom(MsgType.NOT_IN_ROOM);
			onlineUserMap.put(senderId, onlineUser);
		}
	}
	/**
	 * 处理进入房间的指令逻辑
	 * @param session
	 * @param msgVo
	 */
	private void inRoom(Session session, MessageVo msgVo) {
		String senderId = msgVo.getSenderId();
		String reciverId = msgVo.getReciverId();
		String roomId = msgVo.getRoomId();
		OnlineUser onlineUser = onlineUserMap.get(senderId);
		
		if(onlineUser!= null){
			if(StrUtil.isNotBlank(roomId) && !MsgType.WITHOUT_ROOM.equals(roomId)){
				
				ChatRoom roomRet = roomRepository.findByOwnerIdAndReciverId(senderId, reciverId);
				onlineUser.setCurRoom(roomId);
				onlineUserMap.put(senderId, onlineUser);
				sendMessage(response.getInRoomResp(roomRet));
				messageRepository.deleteByReciverRoomId(roomRet.getId());
				roomRet.setHasNew(false);
				roomRet.setUnreadMessage(new ArrayList());
				roomRepository.save(roomRet);
			}else{
				Optional<User> senderOpt = userRepository.findByWebId(senderId);
				Optional<User> reciverOpt = userRepository.findByWebId(reciverId);
				if(senderOpt.isPresent() && reciverOpt.isPresent()){
					if(MsgType.WITHOUT_ROOM.equals(roomId)){
						//房间不存在需要创建房间
						ChatRoom owner = new ChatRoom();
						ChatRoom reciver = new ChatRoom();
						owner.setHasNew(false);
						owner.setOwner(senderOpt.get());
						owner.setReciver(reciverOpt.get());
						owner.setUnreadMessage(null);
						
						reciver.setHasNew(false);
						reciver.setOwner(reciverOpt.get());
						reciver.setReciver(senderOpt.get());
						reciver.setUnreadMessage(null);
						
						ChatRoom ownerRet = roomRepository.save(owner);
						roomRepository.save(reciver);
						onlineUser.setCurRoom(ownerRet.getId());
						
						onlineUserMap.put(senderId, onlineUser);
						sendMessage(response.getInRoomResp(ownerRet));
					}
				}
			}
		}
	}

	@OnOpen
	public void onOpen(Session session){
		addOnlineSession();
		this.currentSession = session;
		onlineSocket.add(this);
		Map<String,List<String>> paramsMap = session.getRequestParameterMap();;
		String token = paramsMap.get("token").get(0);
		String webId = paramsMap.get("webId").get(0);
		if(CommonUtils.paramsAuth(token,webId)){
			Optional<UserToken> tokenOpt = tokenRepository.findByUserWebId(webId);
			Optional<User> userOpt = userRepository.findByWebId(webId);
			if(tokenOpt.isPresent() && userOpt.isPresent()){
				if(token.equals(tokenOpt.get().getToken())){
					OnlineUser onlineUser = new OnlineUser();
					onlineUser.setSession(session);
					onlineUser.setToken(token);
					onlineUser.setWebId(webId);
					onlineUserMap.put(webId, onlineUser);
					List<ChatRoom> roomList = roomRepository.findAllByOwnerOrderByHasNewDesc(userOpt.get());
					sendMessage(response.getRoomListResp(roomList));
				}
			}
		}else{
			sendMessage(response.getWrongUserResp());
			log.error("错误的连接被建立:params is blank");
		}
		log.info("新连接建立,当前{}个连接",onlineCount);
	}
	
	
	@OnClose
	public void onClose(Session session){
		subOnlineSession();
		onlineSocket.remove(session);
		String key = "";
		for(Entry<String,OnlineUser> entry : onlineUserMap.entrySet()){
			if(session.equals(entry.getValue().getSession())){
				key = entry.getKey();
				break;
			}
		}
		if(!key.isEmpty()){
			onlineUserMap.remove(key);
		}
		log.info("连接退出,当前{}个连接",onlineCount);
	}
	
	@OnError
	public void onError(Session session,Throwable error){
		log.error("socket服务中断! :{}",error.getLocalizedMessage());
		error.printStackTrace();
	}
	
}
