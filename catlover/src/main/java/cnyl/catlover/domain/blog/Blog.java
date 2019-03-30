package cnyl.catlover.domain.blog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;

import cnyl.catlover.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Blog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 759287539827153042L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	
	@Lob
	private String content;
	private int stars;
	private int views;
	private int mediaType;
	@JSONField(format="yyyy年MM月dd日 HH:mm")
	private Date publishTime;
	private int status;
	private int comments;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="user_id",referencedColumnName = "webId")
	private User publisher;
	
	@JoinColumn(name = "BLOG_ID")
	@OneToMany(fetch=FetchType.EAGER)
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
}
