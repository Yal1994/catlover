package cnyl.catlover.config.upload;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class UploadFileConfig {
	
	@Value("${catlover.profile}")
	private String filePrefix;
	
	@Bean
	public MultipartConfigElement  multipartConfigElement(){
		
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setLocation(filePrefix);
		factory.setMaxFileSize(DataSize.ofMegabytes(500L));
		return factory.createMultipartConfig();
		
	}

}
