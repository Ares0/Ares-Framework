package frame.aop;

import frame.core.BeanDefinition;

public interface FactoryBean {

	Object getObject(BeanDefinition bd);
	
	void initFactoryBean();
	
}
