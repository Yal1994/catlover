package cnyl.catlover.config.interceptor;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.hutool.core.date.DateUtil;
import cnyl.catlover.common.util.CommonUtils;
import cnyl.catlover.domain.user.UserToken;
import cnyl.catlover.repository.UserTokenRepository;

public class WxAuthInterceptor implements HandlerInterceptor{
	@Value("${catlover.token.expired}")
	private int expiredTime;
	
	@Autowired
	private UserTokenRepository tokenRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(CommonUtils.paramsAuth(request, "webId","token")){
			String webId = request.getAttribute("webId").toString();
			String token = request.getAttribute("token").toString();
			Optional<UserToken> optUserToken = tokenRepository.findByUserWebId(webId);
			if(optUserToken.isPresent()){
				UserToken userToken = optUserToken.get();
				String tokenReal = userToken.getToken();
				if(tokenReal.equals(token)){
					userToken.setExpiredTime(DateUtil.offsetDay(new Date(), expiredTime));
					tokenRepository.save(userToken);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable ModelAndView modelAndView) throws Exception {

	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

	}
	

}
