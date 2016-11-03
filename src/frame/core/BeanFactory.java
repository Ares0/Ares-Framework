package frame.core;

public interface BeanFactory {

	final String SCANPACKAGE = "component-scan";
	
	Object getBean(String name);
	
}
