package cnyl.catlover.domain.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import cnyl.catlover.domain.user.User;
import lombok.Data;

@Entity
@Data
public class ChatRoom implements Serializable{
	private static final long serialVersionUID = 8583239654745201646L;
	
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator="system-uuid")
	private String id;
	/**
	 * sender_id
	 */
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="sender_id",referencedColumnName = "webId", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private User owner;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="reciver_id",referencedColumnName = "webId", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private User reciver;
	
	private boolean hasNew;
	
	@OneToMany(cascade=CascadeType.REMOVE,targetEntity=Message.class,mappedBy = "reciverRoomId",fetch=FetchType.EAGER)
	private List<Message> unreadMessage = new ArrayList<Message>();
	
}
