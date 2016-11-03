package frame.core;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {

	private String name;
	
	private Class<?> clazz;
	
	private List<Class<?>> dependences;
	
	private boolean isAspect;
	
	private List<String> aspectExpressions;
	
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
	
}
