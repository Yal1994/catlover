package cnyl.catlover.domain.blog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cnyl.catlover.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Comment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4260468845996100370L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	
	@Lob
	private String text;
	
	
	@JSONField(format="yyyy年MM月dd日 HH:mm")
	private Date commentTime;
	
	@ManyToOne
	@JoinColumn(name = "user_id",referencedColumnName = "webId")
	private User commenter;
	
	private String blogId;
	
	@OneToMany(targetEntity=CommentChild.class,mappedBy = "comment",fetch=FetchType.EAGER)
	private List<CommentChild> commentChilds = new ArrayList<CommentChild>();

}
