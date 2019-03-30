package cnyl.catlover.domain.blog;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

import cnyl.catlover.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class CommentChild implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1344472164614285947L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	
	@Lob
	@Nullable
	private String text;
	@JSONField(format="yyyy年MM月dd日 HH:mm")
	private Date commentTime;
	
	@ManyToOne
	@JoinColumn(name="user_id",referencedColumnName = "webId")
	private User commenter;
	
	@JSONField(serialize = false)
	@ManyToOne()
	@JoinColumn(name="comment_id")
	private Comment comment;

}
