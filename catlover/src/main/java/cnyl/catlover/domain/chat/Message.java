package cnyl.catlover.domain.chat;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 被缓存的消息,当消息接收者不在线时,将收到的消息存到这里
 * @author Yal
 *
 */
@Entity
@Data
public class Message implements Serializable{
	private static final long serialVersionUID = 1476612916869534507L;
	@Id
	@GenericGenerator(name="system-uuid",strategy="org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date sendTime;
	
	@JSONField(format="HH:mm")
	private Date simpleTime;
	
	private String senderId;
	
	private String reciverId;
	
//	@JSONField(deserialize=true)
//	@ManyToOne
//	@JoinColumn(name="roomId",referencedColumnName = "id")
//	private ChatRoom room;
	
	private String reciverRoomId;
	
	@Lob
	@NotBlank(message="消息不允许为空")
	private String content;

}
