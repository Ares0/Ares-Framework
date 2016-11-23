package frame.core.support;

import frame.aop.DefaultFactoryBean;

public class FileSystemBeanFactory extends AbstractBeanFactory {

	public FileSystemBeanFactory(String configLocation) {
		super(configLocation);
	}
	
	protected String getConfigPath(String configLocation) {
		String path = System.getProperty("user.dir").concat("\\src")
				.concat("\\").concat(configLocation);	
		path.replace("/", "\\");
		return path;
	}
	
	@Override
	protected void initFactoryBean() {
		factoryBean = new DefaultFactoryBean(this);
		factoryBean.initFactoryBean(); 
	}

}
