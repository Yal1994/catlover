package cnyl.catlover.common.constants;
/**
 * Socket通信请求类型
 * @author Yal
 *
 */
public class MsgType {
	
	
	/**
	 * 0.断开连接指令,此时会将session以及OnlineUser清除
	 */
	public static int DIS_CONNECT = 0;
	/**
	 * 1.连接SOCKET,此时记录连接者的SESSION以及对应的ONLINEUSER
	 */
	public static int CONNECT = 1;
	/**
	 * 2.进入房间指令,此时需记录进入房间的roomid,同时将该ONLINEUSER的ROOMID进行更新,返回最新缓存的消息,并置空缓存表
	 */
	public static int IN_ROOM = 2;
	/**
	 * 3.退出房间指令,此时需记录进入房间的roomid,同时将该ONLINEUSER的ROOMID进行更新为-1
	 */
	public static int OUT_ROOM = 3;
	/**
	 * 4.发送消息指令,此时需要判断接收者是否已经建立房间以及是否在线,来决定是否需要缓存最新消息
	 */
	public static int MSG = 4;
	
	public static int UPLOAD = 5;
	/**
	 * 6.请求获取聊天列表
	 */
	public static final int ROOM_LIST = 6;
	/**
	 * 退出房间-1
	 */
	public static String NOT_IN_ROOM = "-1";
	/**
	 * 没有ROOMID,可能是因为房间是第一次私聊进入的,此时不存在roomID
	 */
	public static String WITHOUT_ROOM = "0";

}
