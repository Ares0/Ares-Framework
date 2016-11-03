package frame.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import frame.aop.FactoryBean;
import frame.stereotype.Resource;
import frame.utils.Utils;

public abstract class AbstractBeanFactory implements BeanFactory {

	private Properties config;
	
	private Map<String, BeanDefinition> beanNameDefinition;
	
	// 持有相同的BeanDefinition地址
	private Map<Class<?>, BeanDefinition> beanClassDefinition;
	
	private ResourceLoader loader;
	
	protected FactoryBean factoryBean;
	
	private Map<String, Object> beans;
	
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
		Object bean = null;
		
		if (beans.get(name) != null) {
			return bean;
		}
		
		BeanDefinition bd;
		if ((bd = beanNameDefinition.get(name)) != null) {
			bean = factoryBean.getObject(bd);
			beans.put(name, bean);
			
			List<Class<?>> dbs = bd.getDependences();
			if (dbs != null && dbs.size() > 0) {
				for (Class<?> dc : dbs) {
					beans.put(Utils.getLastNameByPeriod(dc.getName()), getBean(beanClassDefinition.get(dc).getName()));
				}
				doPropertyInject(bean);
			}
		}
		return bean;
	}

	private void doPropertyInject(Object bean) {
		Class<?> beanClass = bean.getClass();
		for (Field f : beanClass.getDeclaredFields()) {
			if ((f.getAnnotation(Resource.class)) != null) {
				BeanDefinition bd;
				Class<?> propertyClass = f.getType();
				
				if ((bd = beanClassDefinition.get(propertyClass)) != null) {
					Object db = beans.get(bd.getName());
					try {
						f.setAccessible(true);
						f.set(bean, db);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
