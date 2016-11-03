package frame.aop;

import frame.core.BeanDefinition;

public class DefaultFactoryBean implements FactoryBean {

	public DefaultFactoryBean() {
		
	}
	
	@Override
	public Object getObject(BeanDefinition bd) {
		if (!bd.isAspect()) {
			try {
				return bd.getBeanClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
		} else {
			// TODO aopproxy
		}
		return null;
	}

}
