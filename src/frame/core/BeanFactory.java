package frame.core;

import java.util.Map;

public interface BeanFactory {

	final String SCANPACKAGE = "component-scan";
	
	Object getBean(String name);
	
	Object getBean(Class<?> beanClass);
	
	Object getBean(BeanKey beanKey);
	
	Map<BeanKey, BeanDefinition> getBeanDefinition();
	
}
