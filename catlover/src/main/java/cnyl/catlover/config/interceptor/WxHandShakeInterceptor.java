package cnyl.catlover.config.interceptor;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cnyl.catlover.common.util.CommonUtils;
import cnyl.catlover.domain.user.UserToken;
import cnyl.catlover.repository.UserTokenRepository;
@Component
@Configuration
public class WxHandShakeInterceptor implements HandshakeInterceptor{
	
	@Autowired
	private UserTokenRepository tokenRepository;
	
	@Value("${catlover.token.expired}")
	private int expiredTime;

	@Override
	public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2, Exception arg3) {
		
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler,Map<String, Object> map) throws Exception {
		if(request instanceof ServletServerHttpRequest){
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpServletRequest req = servletRequest.getServletRequest();
			if(CommonUtils.paramsAuth(req, "webId","token")){
				String webId = req.getAttribute("webId").toString();
				String token = req.getAttribute("token").toString();
				Optional<UserToken> optUserToken = tokenRepository.findByUserWebId(webId);
				if(optUserToken.isPresent()){
					UserToken userToken = optUserToken.get();
					String tokenReal = userToken.getToken();
					if(tokenReal.equals(token)){
						//userToken.setExpiredTime(DateUtil.offsetDay(new Date(), expiredTime));
						//tokenRepository.save(userToken);
						return true;
					}
				}
			}
		}
		return false;
	}

}
