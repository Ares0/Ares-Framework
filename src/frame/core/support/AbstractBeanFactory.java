package frame.core.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import frame.aop.FactoryBean;
import frame.core.BeanDefinition;
import frame.core.BeanFactory;
import frame.core.BeanKey;
import frame.core.BeanWrapper;
import frame.core.FileResourceLoader;
import frame.core.ResourceLoader;


public abstract class AbstractBeanFactory implements BeanFactory {

	private Properties config;
	
	private Map<BeanKey, BeanDefinition> beanDefinition;
	
	// 持有相同的BeanDefinition地址
	private Map<Class<?>, BeanDefinition> beanClassDefinition;
	
	private ResourceLoader loader;
	
	protected FactoryBean factoryBean;
	
	private Map<BeanKey, BeanWrapper> beans;
	
	public AbstractBeanFactory(String configLocation) {
		synchronized (this) {
			initConfig(configLocation);
			loadBeanDefinition();
			initClassBeanDefinition();
			
			beans = new HashMap<>();
			initFactoryBean();
		}
	}

	protected abstract void initFactoryBean();

	public Map<BeanKey, BeanDefinition> getBeanDefinition() {
		return beanDefinition;
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
		beanDefinition = loader.loadResource(config.getProperty(SCANPACKAGE));
	}

	private void initClassBeanDefinition() {
		beanClassDefinition = new HashMap<>();
		for (Entry<BeanKey, BeanDefinition> e : beanDefinition.entrySet()) {
			BeanDefinition bd = e.getValue();
			beanClassDefinition.put(bd.getBeanClass(), bd);
		}
	}
	
	@Override
	public Object getBean(String name) {
		return this.getBean(BeanKey.getBeanKey(name));
	}
	
	@Override
	public Object getBean(Class<?> beanClass) {
		return this.getBean(BeanKey.getBeanKey(beanClass));
	}
	
	public Object getBean(BeanKey key) {
		BeanDefinition bd = null;
		bd = beanDefinition.get(key);
		
		if (bd == null) {
			throw new IllegalArgumentException();
		}
		
		BeanWrapper bw;
		if (bd.isSingleton() || (bw = beans.get(key)) == null) {
			bw = factoryBean.getObject(bd);
			beans.put(key, bw);
			
			List<Class<?>> dbs = bd.getDependences();
			if (dbs != null && dbs.size() > 0) {
				for (Class<?> dc : dbs) {
					BeanDefinition dbd = beanClassDefinition.get(dc);
					getBean(dbd.getBeanKey());
				}
				doPropertyInject(bd, bw);
			}
		} 
		return bw.getBean();
	}

	private void doPropertyInject(BeanDefinition bd, BeanWrapper bw) {
		for (Field f : bd.getResourceFields()) {
			BeanDefinition dbd = beanClassDefinition.get(f.getType());
			
			if (beanClassDefinition.get(f.getType()) != null) {
				BeanWrapper dbw;
				dbw = beans.get(dbd.getBeanKey());
				
				try {
					f.setAccessible(true);
					f.set(bw.getInstance(), dbw.getBean());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
