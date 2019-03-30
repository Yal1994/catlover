package cnyl.catlover.domain.relation;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
/**
 * 用户关注博客类
 * @author Yal
 *
 */
@Data
@Entity
public class UserBlogStar {
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	private String webId;
	private String blogId;
	private Date starTime;

}
