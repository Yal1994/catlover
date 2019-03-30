package cnyl.catlover.domain.user;

import lombok.Data;

@Data
public class WxLoginResult {
	
	private String openId;
	private String sessionKey;
	private String unionId;
	private String errCode;
	private String errMsg;

}
