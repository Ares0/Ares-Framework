package frame.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinition {

	private String name;
	
	private Class<?> clazz;
	
	private BeanKey beanKey;
	
	private boolean isSingleton;
	
	// resource
	private List<Field> resourceFields;
	
	// 依赖class
	private List<Class<?>> dependences;
	
	// 级别
	private int level;
	
	private boolean isAspect;
	
	// 切面表达式
	private List<String> aspectExpressions;
	
	private boolean isController;
	
	// 请求路径-Method缓存
	private Map<String, Method> requestMapping;
	
	// 方法-返回类型
	private Map<Method, String> responseType;
	
	// 方法-返回视图
	private Map<Method, String> responseMapping;
	
	// 方法-参数列表
	private Map<Method, Parameter[]> parameterMapping;
	
	// 方法-参数名列表
	private Map<Method, String[]> parameterNameMapping;
	
	// singleton
	public static final String SCOPE_SINGLETON = "singleton";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getBeanClass() {
		return clazz;
	}

	public void setBeanClass(Class<?> clazz) {
		this.clazz = clazz;
	}

	public BeanKey getBeanKey() {
		return beanKey;
	}

	public void setBeanKey(BeanKey beanKey) {
		this.beanKey = beanKey;
	}

	public boolean isSingleton() {
		return isSingleton;
	}

	public void setSingleton(boolean isSingleton) {
		this.isSingleton = isSingleton;
	}

	public List<Field> getResourceFields() {
		return resourceFields;
	}

	public void setResourceFields(Field field) {
		if (resourceFields == null) {
			resourceFields = new ArrayList<>();
		}
		resourceFields.add(field);
	}

	public List<Class<?>> getDependences() {
		return dependences;
	}

	public void setDependences(Class<?> dependence) {
		if (dependences == null) {
			dependences = new ArrayList<>();
		}
		dependences.add(dependence);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isAspect() {
		return isAspect;
	}

	public void setAspect(boolean isAspect) {
		this.isAspect = isAspect;
	}

	public List<String> getAspectExpression() {
		return aspectExpressions;
	}

	public void setAspectExpression(String aspectExpression) {
		if (aspectExpressions == null) {
			aspectExpressions = new ArrayList<>();
		}
		aspectExpressions.add(aspectExpression);
	}

	public boolean isController() {
		return isController;
	}

	public void setController(boolean isController) {
		this.isController = isController;
	}

	public Map<String, Method> getRequestMapping() {
		return requestMapping;
	}

	public void setRequestMapping(String path, Method m) {
		if (requestMapping == null) {
			requestMapping = new HashMap<>();
		}
		this.requestMapping.put(path, m);
	}

	public Map<Method, String> getResponseType() {
		return responseType;
	}

	public void setResponseType(Method m, String type) {
		if (responseType == null) {
			responseType = new HashMap<>();
		}
		this.responseType.put(m, type);
	}

	public Map<Method, String> getResponseMapping() {
		return responseMapping;
	}
	
	public void setResponseMapping(Method m, String type) {
		if (responseMapping == null) {
			responseMapping = new HashMap<>();
		}
		this.responseMapping.put(m, type);
	}

	public Map<Method, Parameter[]> getParameterMapping() {
		return parameterMapping;
	}

	public void setParameterMapping(Map<Method, Parameter[]> parameterMapping) {
		this.parameterMapping = parameterMapping;
	}
	
	public Map<Method, String[]> getParameterNameMapping() {
		return parameterNameMapping;
	}
	
	public void setParameterNameMapping(Map<Method, String[]> parameterNameMapping) {
		this.parameterNameMapping = parameterNameMapping;
	}
	
}
