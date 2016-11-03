package frame.core;

import frame.aop.DefaultFactoryBean;

public class WebApplicationContext extends AbstractBeanFactory{

	public final static String SERVLETCONTEXT_BEANFACTORY = "webContext";
	
	public static final String CONFIG_PATH = "configPath";
	
	public WebApplicationContext(String configLocation) {
		super(configLocation);
	}
	
	protected String getConfigPath(String configLocation) {
		return configLocation.replace("/", "\\");
	}
	
	@Override
	protected void initFactoryBean() {
		factoryBean = new DefaultFactoryBean(this);
		factoryBean.initFactoryBean(); // interceptorœ»≥ˆœ÷
	}

}
