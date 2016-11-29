package frame.core;

import java.util.Map;

public interface ResourceLoader {

	Map<BeanKey, BeanDefinition> loadResource(String packagePath);
	
}
