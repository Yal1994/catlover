package cnyl.catlover.config.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import cnyl.catlover.config.interceptor.WxAuthInterceptor;

@Configuration
public class MVCConfig extends WebMvcConfigurationSupport{
	
	@Value("${catlover.imgAddr}")
	private String resImgAddr;
	
	@Value("${catlover.viAddr}")
	private String resViAddr;
	
	@Value("${catlover.profile}")
	private String resRealAddr;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry){
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler(resViAddr + "/**").addResourceLocations("file:/"+resRealAddr + "/video/");
		registry.addResourceHandler(resImgAddr + "/**").addResourceLocations("file:/"+resRealAddr + "/image/");
		super.addResourceHandlers(registry);
	}
	
//	@Autowired
//	private WxAuthInterceptor interceptor ;
//	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry){
//		registry.addInterceptor(interceptor)
//				.addPathPatterns("/wx/au/**")
//				.excludePathPatterns(resViAddr + "/**")
//				.excludePathPatterns(resImgAddr + "/**");
//		//super.addInterceptors(registry);
//	}
	
	

}
