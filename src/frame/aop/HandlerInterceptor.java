package frame.aop;

import java.lang.reflect.Method;
import java.util.List;

public interface HandlerInterceptor extends Comparable<HandlerInterceptor>{

	boolean isMatchClass(String expr);

	boolean isMatchBefore(String string);

	boolean isMatchAfter(String string);

	void setAspectExpression(List<String> aspectExpression);

	void invokeBefore(Object target, Method method, Object[] args);

	void invokeAfter(Object target, Method method, Object[] args);
	
	int getInterceptorLevel();
	
	void setInterceptorLevel(int level);
	
}
