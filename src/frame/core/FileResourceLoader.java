package frame.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import frame.stereotype.Aspect;
import frame.stereotype.Component;
import frame.stereotype.Controller;
import frame.stereotype.Order;
import frame.stereotype.Pointcut;
import frame.stereotype.RequestMapping;
import frame.stereotype.Resource;
import frame.stereotype.ResponseData;
import frame.stereotype.ResponseMapping;
import frame.stereotype.Scope;
import frame.utils.Utils;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class FileResourceLoader implements ResourceLoader {

	private ClassPool pool;
	
	private Map<Class<?>, String> classPathMapping;
	
	public FileResourceLoader() {
		pool = ClassPool.getDefault();
		classPathMapping = new HashMap<>();
	}
	
	@Override
	public Map<BeanKey, BeanDefinition> loadResource(String packagePath) {
		return getBeanDefines(packagePath);
	}
	
	/*
	 * bean define
	 */
	public Map<BeanKey, BeanDefinition> getBeanDefines(String packagePath){  
		Enumeration<URL> dirs;  
		String packageDir = packagePath.replace('.', '/');  
		Map<BeanKey, BeanDefinition> beanDefinitions = new HashMap<>();;
        
        try {  
        	dirs = Utils.getCurrentClassLoader().getResources(packageDir);  
            while (dirs.hasMoreElements()){  
                URL url = dirs.nextElement();  
                String protocol = url.getProtocol();  

                if ("file".equals(protocol)) {  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    resolveClass(packagePath, filePath, beanDefinitions);
                } 
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        resolveBeanDefinition(beanDefinitions);  
        release();
        return beanDefinitions;  
    }

    /*
     * class loading
     */
	private void resolveClass(String packageName, String packagePath, Map<BeanKey, BeanDefinition> beanDefinitions){  
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
                resolveClass(packageName.concat(".").concat(file.getName()), 
                		file.getAbsolutePath(), beanDefinitions);  
            } 
            else {  
            	try {  
            		String className = file.getName().substring(0, 
            				file.getName().length() - 6);  
            		String classPath = packageName.concat(".").concat(className);
            		
                	Class<?> beanClass = Class.forName(classPath);
                	classPathMapping.put(beanClass, classPath);
                	
                	BeanKey bk;
                	String beanName = getComponentName(beanClass);
            		bk = getBeanKey(beanClass, beanName);
            		
            		BeanDefinition bean = new BeanDefinition();
            		bean.setName(beanName);
            		bean.setBeanKey(bk);
            		bean.setBeanClass(beanClass);
            		
            		beanDefinitions.put(bk, bean);
                } catch (ClassNotFoundException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

	/*
     * resolve bean definition
     */
	private void resolveBeanDefinition(Map<BeanKey, BeanDefinition> beanDefinitions) {
		resolveBeanFields(beanDefinitions);
		resolveScope(beanDefinitions);
        resolveDependences(beanDefinitions);
        
        resolveAspects(beanDefinitions);
        resolveControllerPath(beanDefinitions);
        resolveControllerParameters(beanDefinitions);
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

	private BeanKey getBeanKey(Class<?> beanClass, String beanName) {
		BeanKey bk;
		if (beanName == null || beanName.equals("")) {
			bk = BeanKey.getBeanKey(beanClass);
		} else {
			bk = BeanKey.getBeanKey(beanName);
		}
		return bk;
	}

	private void release() {
		pool = null;
		classPathMapping.clear();
		classPathMapping = null;
	}  
	
	// bean fields
	private void resolveBeanFields(Map<BeanKey, BeanDefinition> beanDefinitions) {
		for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			for (Field f : beanClass.getDeclaredFields()) {
				if ((f.getAnnotation(Resource.class)) != null) {
					bd.setResourceFields(f);
				}
			}
		}
	}
	
	// scope
	private void resolveScope(Map<BeanKey, BeanDefinition> beanDefinitions) {
		for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			Scope s = beanClass.getAnnotation(Scope.class);
			if (s != null && s.value().equals(BeanDefinition.SCOPE_SINGLETON)) {
				bd.setSingleton(true);
			}
		}
	}
	
	// dependences
	private void resolveDependences(Map<BeanKey, BeanDefinition> beanDefinitions) {
		for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			for (Field f : beanClass.getDeclaredFields()) {
				Resource r;
				if ((r = f.getAnnotation(Resource.class)) != null) {
					String beanName = r.value();
					Class<?> dpClass = f.getType();
					
					BeanDefinition dp;   
					BeanKey bk = getBeanKey(dpClass, beanName);
					if ((dp = beanDefinitions.get(bk)) != null) {
						bd.setDependences(dp.getBeanClass());
					}
				}
			}
		}
	}

	// aspects
	private void resolveAspects(Map<BeanKey, BeanDefinition> beanDefinitions) {
		for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
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
				
				Order od = beanClass.getAnnotation(Order.class);
				bd.setLevel(od == null ? 0 : od.value());
			}
		}
	}
	
	// controller path
	private void resolveControllerPath(Map<BeanKey, BeanDefinition> beanDefinitions) {
		for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
			BeanDefinition bd = e.getValue();
			Class<?> beanClass = bd.getBeanClass();
			
			Controller c;
			if ((c = beanClass.getAnnotation(Controller.class)) != null) {
				bd.setController(true);
				String classMapping = c.value();
				
				for (Method m : beanClass.getDeclaredMethods()) {
					RequestMapping reqMapping;
					if ((reqMapping = m.getAnnotation(RequestMapping.class)) != null) {
						String methodMapping = classMapping.concat(reqMapping.value());
						if (!methodMapping.equals("")) {
							m.setAccessible(true);
							bd.setRequestMapping(methodMapping, m);
						}
					}
					// view type
					ResponseData rp;
					if ((rp = m.getAnnotation(ResponseData.class)) != null) {
						bd.setResponseType(m, rp.value());
					}
					// view path
					ResponseMapping repMapping;
					if ((repMapping = m.getAnnotation(ResponseMapping.class)) != null) {
						String viewPath = repMapping.value();
						if (!viewPath.equals("")) {
							bd.setResponseMapping(m, viewPath);
						}
					}
				}
			}
		}
	}

	// controller method path
	private void resolveControllerParameters(Map<BeanKey, BeanDefinition> beanDefinitions) {
		try {  
			for (Entry<BeanKey, BeanDefinition> e : beanDefinitions.entrySet()) {
				BeanDefinition bd = e.getValue();
				Class<?> beanClass = bd.getBeanClass();
				
				Map<Method, Parameter[]> parameterMapping = new HashMap<>();
				Map<Method, String[]> parameterNameMapping = new HashMap<>();
				
				if (beanClass.getAnnotation(Controller.class) != null) {
					for (Method m : beanClass.getDeclaredMethods()) {
						pool.insertClassPath(new ClassClassPath(this.getClass())); 
						
				        CtClass cc = pool.get(classPathMapping.get(beanClass));  
				        CtMethod cm = cc.getDeclaredMethod(m.getName());  
				  
				        MethodInfo methodInfo = cm.getMethodInfo();  
				        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
				        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  

				        String[] parameterNames = new String[cm.getParameterTypes().length];  
				        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
				        
				        for (int i = 0; i < parameterNames.length; i++) {
				        	parameterNames[i] = attr.variableName(i + pos);
				        }
				        
				        parameterMapping.put(m, m.getParameters());
				        parameterNameMapping.put(m, parameterNames);
					}
					
					bd.setParameterMapping(parameterMapping);
					bd.setParameterNameMapping(parameterNameMapping);
				}
			}
		} catch (NotFoundException ex) {  
			ex.printStackTrace();  
		}
	}
	
}
