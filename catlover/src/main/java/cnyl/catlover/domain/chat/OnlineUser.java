package cnyl.catlover.domain.chat;

import javax.websocket.Session;
import java.security.Principal;

import lombok.Data;

@Data
public class OnlineUser implements Principal{
	
	private String webId;
	private String token;
	private Session session;
	private String curRoom = "-1";
	
	@Override
	public String getName() {
		return token;
	}

}
