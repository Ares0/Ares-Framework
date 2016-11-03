package frame.aop;

import java.lang.reflect.Method;
import java.util.List;

public interface HandlerInterceptor {

	boolean isMatchClass(String expr);

	void invoke(Object target, Method method, Object[] args);

	boolean isMatchBefore(String string);

	boolean isMatchAfter(String string);

	void setAspectExpression(List<String> aspectExpression);

	void invokeBefore(Object target, Method method, Object[] args);

	void invokeAfter(Object target, Method method, Object[] args);
	
}
