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
import frame.core.BeanKey;
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
	
	private Map<BeanKey, BeanWrapper> beans;
	
	private final static int INJECTBYNAME = 1;
	
	private final static int INJECTBYTYPE = 2;
	
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
		return this.getBean(getBeanKey(name));
	}
	
	@Override
	public Object getBean(Class<?> beanClass) {
		return this.getBean(getBeanKey(beanClass));
	}
	
	protected Object getBean(BeanKey key) {
		String name;
		Class<?> beanClass;
		BeanDefinition bd = null;
		int type = INJECTBYNAME;
		
		if ((name = key.getName()) != null) {
			bd = beanNameDefinition.get(name);
		} else if ((beanClass = key.getBeanClass()) != null) {
			type = INJECTBYTYPE;
			bd = beanClassDefinition.get(beanClass);
		}
		
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
					getBean(beanClassDefinition.get(dc).getName());
				}
				doPropertyInject(bd, bw, type);
			}
		} 
		return bw.getBean();
	}
	
	public BeanKey getBeanKey(String name) {
		return new BeanKey(name);
	}
	
	public BeanKey getBeanKey(Class<?> beanClass) {
		return new BeanKey(beanClass);
	}

	private void doPropertyInject(BeanDefinition bd, BeanWrapper bw, int type) {
		for (Field f : bd.getResourceFields()) {
			if ((bd = beanClassDefinition.get(f.getType())) != null) {
				BeanWrapper db;
				db = getBeanDefinitionByType(bd, type);
				
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

	private BeanWrapper getBeanDefinitionByType(BeanDefinition bd, int type) {
		BeanWrapper db;
		if (type == INJECTBYNAME) 
			db = beans.get(getBeanKey(bd.getName()));
		else
			db = beans.get(getBeanKey(bd.getBeanClass()));
		return db;
	}

}
