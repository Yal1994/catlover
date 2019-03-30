package cnyl.catlover.domain.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cnyl.catlover.common.constants.YesOrNo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable{
	private static final long serialVersionUID = -6996810500481849872L;
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "system-uuid")
	private String id;
	@JSONField(serialize = false)
	private String openId;
	@JSONField(serialize = false)
	private String unionId;
	private String avatarUrl;
	private String webId;
	private String gender;
	private String nickName;
	private String webNickName;
	private String webAvatarUrl;
	private int stars;
	private int follows;
	private int blogs;
	@JSONField(format="yyyy年MM月dd日 HH:mm")
	private Date registDate;
	@JSONField(format="yyyy年MM月dd日 HH:mm")
	private Date lastVisit;
	
	private String bgImg;
	
	
	private String openChat = YesOrNo.YES;//是否开启私聊,默认开启
	

}
