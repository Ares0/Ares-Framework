package frame.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class AopProxy implements InvocationHandler{

	private Object target;
	
	private List<HandlerInterceptor> interceptors;
	
	public AopProxy(Object target, List<HandlerInterceptor> interceptors) {
		this.target = target;
		this.interceptors = interceptors;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		for (int i = 0; i < interceptors.size(); i++) {
			HandlerInterceptor hi = interceptors.get(i);
			// 是否匹配当前前置方法
			if (hi.isMatchBefore(method.getName())) {
				hi.invokeBefore(target, method, args);
			}
		}
		
		method.setAccessible(true);
		Object result = method.invoke(target, args);
		
		for (int i = interceptors.size() - 1; i >= 0; i--) {
			HandlerInterceptor hi = interceptors.get(i);
			// 是否匹配当前后置方法
			if (hi.isMatchAfter(method.getName())) {
				hi.invokeAfter(target, method, args);
			}
		}
		return result;
	}

}
