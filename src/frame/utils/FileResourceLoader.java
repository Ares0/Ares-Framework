package frame.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import frame.core.BeanDefinition;
import frame.stereotype.Aspect;
import frame.stereotype.Component;
import frame.stereotype.Pointcut;
import frame.stereotype.Resource;

public class FileResourceLoader implements ResourceLoader {

    ClassLoader classLoader;
	
	public FileResourceLoader() {
		classLoader = Thread.currentThread().getContextClassLoader();
	}
	
	@Override
	public Map<String, BeanDefinition> loadResource(String packagePath) {
		return getBeanDefines(packagePath);
	}
	
	public Map<String, BeanDefinition> getBeanDefines(String packagePath){  
		Map<String, BeanDefinition> beanDefinitions = new HashMap<>();;
        Enumeration<URL> dirs;  
        String packageDir = packagePath.replace('.', '/');  
        
        try {  
        	dirs = classLoader.getResources(packageDir);  
        	
            while (dirs.hasMoreElements()){  
                URL url = dirs.nextElement();  
                String protocol = url.getProtocol();  
                
                if ("file".equals(protocol)) {  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    
                    addClass(packagePath, filePath, beanDefinitions);
                    addDependences(beanDefinitions);
                    addAspects(beanDefinitions);
                } 
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return beanDefinitions;  
    }  
	
	private void addClass(String packageName, String packagePath, Map<String, BeanDefinition> beanDefinitions){  
        File dir = new File(packagePath);  
        if (!dir.exists() || !dir.isDirectory()) {  
            return;  
        }  
        
        File[] dirfiles = dir.listFiles(new FileFilter() {  
              public boolean accept(File file) {  
                return (file.isDirectory()) || (file.getName().endsWith(".class"));  
              }  
            });  
        
        for (File file : dirfiles) {  
            if (file.isDirectory()) {  
                addClass(packageName.concat(".").concat(file.getName()), 
                		file.getAbsolutePath(), beanDefinitions);  
            } else {  
            	try {  
            		String className = file.getName().substring(0, 
            				file.getName().length() - 6);  
            		
                	Class<?> clazz = Class.forName(packageName.concat(".").concat(className));
                	Component cop = clazz.getAnnotation(Component.class);
                	
                	if (cop != null) {
                		String beanName;
                		if ((beanName = cop.value())== null || beanName.equals("")) {
                			beanName = className;
                		}
                		
                		BeanDefinition bean = new BeanDefinition();
                		bean.setName(beanName);
                		bean.setBeanClass(clazz);
                		beanDefinitions.put(beanName, bean);
                	}
                } catch (ClassNotFoundException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

	private void addDependences(Map<String, BeanDefinition> beanDefinitions) {
		for (Map.Entry<String, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			for (Field f : beanClass.getDeclaredFields()) {
				Resource r;
				if ((r = f.getAnnotation(Resource.class)) != null) {
					String beanName;
            		if ((beanName = r.value())== null || beanName.equals("")) {
            			beanName = Utils.getBeanNameByClassName(f.getType().toString());
            		}
            		
            		BeanDefinition dp;   
            		if ((dp = beanDefinitions.get(beanName)) != null) {
            			bd.setDependences(dp.getBeanClass());
            		}
				}
			}
		}
	}

	private void addAspects(Map<String, BeanDefinition> beanDefinitions) {
		for (Map.Entry<String, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			if (beanClass.getAnnotation(Aspect.class) != null) {
				bd.setAspect(true);
				for (Method m : beanClass.getDeclaredMethods()) {
					Pointcut pc;
					if ((pc = m.getAnnotation(Pointcut.class)) != null) {
						bd.setAspectExpression(pc.value());
					}
				}
			}
		}
	}

}
