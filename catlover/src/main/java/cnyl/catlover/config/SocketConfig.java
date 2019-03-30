package cnyl.catlover.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import cnyl.catlover.config.interceptor.WxHandShakeInterceptor;
import cnyl.catlover.config.interceptor.WxPrincipalHandShakeHandler;

@Configuration
public class SocketConfig implements WebSocketMessageBrokerConfigurer {
	
//	@Autowired
//	private WxHandShakeInterceptor wxHandShakeInterceptor;
//	
//	@Autowired
//	private WxPrincipalHandShakeHandler principalHandler;
//
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/wx/chat")
//					.addInterceptors(wxHandShakeInterceptor)
//						.setHandshakeHandler(principalHandler).withSockJS();
//	}
	
	@Bean
	public ServerEndpointExporter serverEndpointExporter(){
		return new ServerEndpointExporter();
	}
}
