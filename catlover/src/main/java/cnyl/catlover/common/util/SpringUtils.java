package cnyl.catlover.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
@Component
public class SpringUtils implements BeanFactoryPostProcessor{
	
	private static ConfigurableListableBeanFactory beanFactory;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SpringUtils.beanFactory = beanFactory;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name){
		return (T)beanFactory.getBean(name);
	}
	
	
	public static <T> T getBean(Class<T> clazz){
		T result = (T) beanFactory.getBean(clazz);
        return result;
	}

}
