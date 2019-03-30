package cnyl.catlover.domain.chat;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cnyl.catlover.common.util.JSONUtils;


public class ChatResponse {
	
	//public static String ROOM_LIST = "ROOM_LIST";
	public int SUCCESS_RECIVE = 0;
	public int ROOM_LIST = 1;
	public int MESSAGE = 2;
	public int IN_ROOM = 3;
	public int MSG = 4;
	
	
	public JSONObject getRoomListResp(List<ChatRoom> roomList){
		JSONObject ret = JSONUtils.defaultRet();
		Msg<List<ChatRoom>> msg = new ChatResponse.Msg<List<ChatRoom>>();
		msg.setType(ROOM_LIST+"");
		msg.setContent(roomList);
		ret.put("msg",msg);
		return ret;
	}
	
	public JSONObject getMessageResp(Message msg,ChatRoom room){
		JSONObject ret = JSONUtils.defaultRet();		
		ret.put("msg",msg);
		ret.put("room",room);
		ret.put("msgType",MESSAGE);
		return ret;
	}
	
	public JSONObject getMessageResp(Message msg){
		JSONObject ret = JSONUtils.defaultRet();
		Msg<Message> m = new ChatResponse.Msg<Message>();
		m.setType(MSG+"");
		m.setContent(msg);
		ret.put("msg",m);
		return ret;
	}
	
	public JSONObject getWrongUserResp(){
		JSONObject ret = new JSONObject();
		ret.put("errCode", 40003);
		ret.put("errMsg", "Invalid User");
		return ret;
	}
	
	public JSONObject getReciveResp(){
		JSONObject ret = new JSONObject();
		Msg<String> msg = new ChatResponse.Msg<String>();
		msg.setType(SUCCESS_RECIVE+"");
		msg.setContent("SUCCESS RECIVE");
		
		ret.put("msg",msg);
		ret.put("errCode", 0);
		ret.put("errMsg", "ok");
		return ret;
	}
	
	public JSONObject getInRoomResp(ChatRoom room){
		JSONObject ret = new JSONObject();
		Msg<ChatRoom> msg = new ChatResponse.Msg<ChatRoom>();
		msg.setContent(room);
		msg.setType(IN_ROOM+"");
		ret.put("msg",msg);
		ret.put("errCode", 0);
		ret.put("errMsg", "ok");
		return ret;
	}
	
	class Msg<T>{
		String type;
		T content;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public T getContent() {
			return content;
		}
		public void setContent(T content) {
			this.content = content;
		}
	}
	
}
