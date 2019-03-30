package cnyl.catlover.domain.blog;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Entity
@Data
public class Attachment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1778174607746010914L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	
	private String url;
	
	@ManyToOne(optional=false,targetEntity=Blog.class)
	@JoinColumn(name="blog_id",referencedColumnName = "id")
	@JSONField(serialize=false)
	private Blog blog;
	
	private String type;
	

}
