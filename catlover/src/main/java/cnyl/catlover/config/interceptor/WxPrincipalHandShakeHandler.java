package cnyl.catlover.config.interceptor;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import cnyl.catlover.common.util.CommonUtils;
import cnyl.catlover.domain.chat.OnlineUser;
import cnyl.catlover.domain.user.UserToken;
import cnyl.catlover.repository.UserTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WxPrincipalHandShakeHandler extends DefaultHandshakeHandler{
	
	@Autowired
	private UserTokenRepository tokenRepository;
	
	public Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
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
						OnlineUser user = new OnlineUser();
						user.setToken(tokenReal);
						user.setWebId(webId);
						return user;
					}
				}
			}
		}
		
		log.info("socket建立失败");
		return null;
	}

}
