package cnyl.catlover.domain.user;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class UserToken {
	@Id
	@GenericGenerator(name="uuid",strategy="org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator="uuid")
	private String id;
	
	private String sessionKey;
	
	private String userWebId;
	
	private String token;
	
	private Date expiredTime;

}
