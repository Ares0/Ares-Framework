package frame.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import frame.aop.DefaultFactoryBean;
import frame.aop.FactoryBean;
import frame.stereotype.Resource;
import frame.utils.FileResourceLoader;
import frame.utils.ResourceLoader;
import frame.utils.Utils;

public class FileSystemBeanFactory implements BeanFactory {

	Properties config;
	
	Map<String, BeanDefinition> beanDefinition;
	
	Map<Class<?>, BeanDefinition> beanClassDefinition;
	
	ResourceLoader loader;
	
	FactoryBean factoryBean;
	
	Map<String, Object> beans;
	
	public FileSystemBeanFactory(String configLocation) {
		synchronized (this) {
			initConfig(configLocation);
			loadBeanDefinition();
			initBeanDefinitionClass();
			
			beans = new HashMap<>();
			factoryBean = new DefaultFactoryBean();
		}
	}

	private void initConfig(String configLocation) {
		String path = System.getProperty("user.dir").concat("\\src")
				.concat("\\").concat(configLocation);	
		path.replace("/", "\\");
		
		try {
			config = System.getProperties();
			config.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadBeanDefinition() {
		loader = new FileResourceLoader();
		beanDefinition = loader.loadResource(config.getProperty(SCANPACKAGE));
	}

	private void initBeanDefinitionClass() {
		beanClassDefinition = new HashMap<>();
		for (Map.Entry<String, BeanDefinition> e : beanDefinition.entrySet()) {
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
		if ((bd = beanDefinition.get(name)) != null) {
			bean = factoryBean.getObject(bd);
			beans.put(name, bean);
			
			List<Class<?>> dbs = bd.getDependences();
			if (dbs != null && dbs.size() > 0) {
				for (Class<?> dc : dbs) {
					beans.put(Utils.getBeanNameByClassName(dc.getName()), getBean(beanClassDefinition.get(dc).getName()));
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
