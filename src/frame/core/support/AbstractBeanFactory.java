package frame.core.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import frame.aop.FactoryBean;
import frame.core.BeanDefinition;
import frame.core.BeanFactory;
import frame.core.BeanWrapper;
import frame.core.FileResourceLoader;
import frame.core.ResourceLoader;

public abstract class AbstractBeanFactory implements BeanFactory {

	private Properties config;
	
	private Map<String, BeanDefinition> beanNameDefinition;
	
	// 持有相同的BeanDefinition地址
	private Map<Class<?>, BeanDefinition> beanClassDefinition;
	
	private ResourceLoader loader;
	
	protected FactoryBean factoryBean;
	
	private Map<String, BeanWrapper> beans;
	
	public AbstractBeanFactory(String configLocation) {
		synchronized (this) {
			initConfig(configLocation);
			loadBeanDefinition();
			initBeanDefinitionClass();
			
			beans = new HashMap<>();
			initFactoryBean();
		}
	}

	protected abstract void initFactoryBean();

	public Map<String, BeanDefinition> getBeanNameDefinition() {
		return beanNameDefinition;
	}

	public Map<Class<?>, BeanDefinition> getBeanClassDefinition() {
		return beanClassDefinition;
	}

	private void initConfig(String configLocation) {
		String path = getConfigPath(configLocation);
		
		try {
			config = System.getProperties();
			config.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract String getConfigPath(String configLocation);
	
	private void loadBeanDefinition() {
		loader = new FileResourceLoader();
		beanNameDefinition = loader.loadResource(config.getProperty(SCANPACKAGE));
	}

	private void initBeanDefinitionClass() {
		beanClassDefinition = new HashMap<>();
		for (Map.Entry<String, BeanDefinition> e : beanNameDefinition.entrySet()) {
			BeanDefinition bd = e.getValue();
			beanClassDefinition.put(bd.getBeanClass(), bd);
		}
	}
	
	@Override
	public Object getBean(String name) {
		BeanDefinition bd = beanNameDefinition.get(name);
		
		if (bd == null) {
			throw new IllegalArgumentException();
		}
		
		BeanWrapper bw;
		if (bd.isSingleton() || (bw = beans.get(name)) == null) {
			bw = factoryBean.getObject(bd);
			beans.put(name, bw);
			
			List<Class<?>> dbs = bd.getDependences();
			if (dbs != null && dbs.size() > 0) {
				for (Class<?> dc : dbs) {
					getBean(beanClassDefinition.get(dc).getName());
				}
				doPropertyInject(bd, bw);
			}
		} 
		return bw.getBean();
	}

	private void doPropertyInject(BeanDefinition bd, BeanWrapper bw) {
		for (Field f : bd.getResourceFields()) {
			if ((bd = beanClassDefinition.get(f.getType())) != null) {
				BeanWrapper db = beans.get(bd.getName());
				
				try {
					f.setAccessible(true);
					f.set(bw.getInstance(), db.getBean());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
