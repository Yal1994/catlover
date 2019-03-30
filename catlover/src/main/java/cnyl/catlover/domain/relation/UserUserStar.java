package cnyl.catlover.domain.relation;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import cnyl.catlover.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户关注用户类
 * @author Yal
 *
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserUserStar {
	
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	
	private String starterId;
	private String targetId;
	
	private Date starTime;

}
