package frame.core;

import java.util.Map;

public interface ResourceLoader {

	Map<String, BeanDefinition> loadResource(String packagePath);
	
}
