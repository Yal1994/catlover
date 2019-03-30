package cnyl.catlover.domain.chat;

import java.io.Serializable;

import lombok.Data;

@Data
public class MessageVo implements Serializable{
	
	private static final long serialVersionUID = -5105694573854637527L;
	private int msgType ;
	private String content;
	private String reciverId;
	private String senderId;
	private String roomId;
	private int contentType;
	

}
