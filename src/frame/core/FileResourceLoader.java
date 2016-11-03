package frame.core;

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

import frame.stereotype.Aspect;
import frame.stereotype.Component;
import frame.stereotype.Controller;
import frame.stereotype.Pointcut;
import frame.stereotype.RequestMapping;
import frame.stereotype.Resource;
import frame.stereotype.ResponseData;
import frame.utils.Utils;


public class FileResourceLoader implements ResourceLoader {

	@Override
	public Map<String, BeanDefinition> loadResource(String packagePath) {
		return getBeanDefines(packagePath);
	}
	
	public Map<String, BeanDefinition> getBeanDefines(String packagePath){  
		Enumeration<URL> dirs;  
		String packageDir = packagePath.replace('.', '/');  
		Map<String, BeanDefinition> beanDefinitions = new HashMap<>();;
        
        try {  
        	dirs = Utils.getCurrentClassLoader().getResources(packageDir);  
            while (dirs.hasMoreElements()){  
                URL url = dirs.nextElement();  
                String protocol = url.getProtocol();  
                if ("file".equals(protocol)) {  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    
                    addClass(packagePath, filePath, beanDefinitions);
                    addDependences(beanDefinitions);
                    addAspects(beanDefinitions);
                    addControllerPath(beanDefinitions);
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
                	Class<?> beanClass = Class.forName(packageName.concat(".").concat(className));
                	
                	String beanName = getComponentName(beanClass);
                	if (beanName != null) {
                		if (beanName.equals("")) {
                			beanName = className;
                		}
                		BeanDefinition bean = new BeanDefinition();
                		bean.setName(beanName);
                		bean.setBeanClass(beanClass);
                		beanDefinitions.put(beanName, bean);
                	}
                } catch (ClassNotFoundException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

	private String getComponentName(Class<?> beanClass) {
		Component cop;
		Aspect asp;
		Controller ctl;
		
		if ((cop = beanClass.getAnnotation(Component.class)) != null) {
			return cop.value();
		} else if ((asp = beanClass.getAnnotation(Aspect.class)) != null) {
			return asp.value();
		} else if ((ctl = beanClass.getAnnotation(Controller.class)) != null) {
			return ctl.value();
		} else {
			return null;
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
            		if ((beanName = r.value()).equals("")) {
            			beanName = Utils.getLastNameByPeriod(f.getType().toString());
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
	
	private void addControllerPath(Map<String, BeanDefinition> beanDefinitions) {
		for (Map.Entry<String, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			Controller c;
			if ((c = beanClass.getAnnotation(Controller.class)) != null) {
				bd.setController(true);
				String classMapping = c.value();
				
				for (Method m : beanClass.getDeclaredMethods()) {
					RequestMapping rm;
					if ((rm = m.getAnnotation(RequestMapping.class)) != null) {
						String methodMapping = classMapping.concat(rm.value());
						if (!methodMapping.equals("")) {
							m.setAccessible(true);
							bd.setRequestMapping(methodMapping, m);
						}
					}
					ResponseData rp;
					if ((rp = m.getAnnotation(ResponseData.class)) != null) {
						bd.setResponseMapping(m, rp.value());
					}
				}
			}
		}
	}

}
