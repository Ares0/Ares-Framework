package frame.core;

import java.util.Map;

public interface BeanFactory {

	final String SCANPACKAGE = "component-scan";
	
	Object getBean(String name);
	
	Map<String, BeanDefinition> getBeanNameDefinition();
	
	Map<Class<?>, BeanDefinition> getBeanClassDefinition();
	
}
