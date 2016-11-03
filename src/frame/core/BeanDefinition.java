package frame.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinition {

	private String name;
	
	private Class<?> clazz;
	
	// 依赖class
	private List<Class<?>> dependences;
	
	private boolean isAspect;
	
	// 切面表达式
	private List<String> aspectExpressions;
	
	private boolean isController;
	
	// 请求路径-Method缓存
	private Map<String, Method> requestMapping;
	
	// 方法-返回视图
	private Map<Method, String> responseMapping;
	
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

	public List<Class<?>> getDependences() {
		return dependences;
	}

	public void setDependences(Class<?> dependence) {
		if (dependences == null) {
			dependences = new ArrayList<>();
		}
		dependences.add(dependence);
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

	public Map<Method, String> getResponseMapping() {
		return responseMapping;
	}

	public void setResponseMapping(Method m, String type) {
		if (responseMapping == null) {
			responseMapping = new HashMap<>();
		}
		this.responseMapping.put(m, type);
	}
	
}
