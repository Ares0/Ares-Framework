package frame.aop;

import frame.core.BeanDefinition;
import frame.core.BeanWrapper;

public interface FactoryBean {

	BeanWrapper getObject(BeanDefinition bd);
	
	void initFactoryBean();
	
}
