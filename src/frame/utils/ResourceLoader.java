package frame.utils;

import java.util.Map;

import frame.core.BeanDefinition;

public interface ResourceLoader {

	Map<String, BeanDefinition> loadResource(String packagePath);
	
}
