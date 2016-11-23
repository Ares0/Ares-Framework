package frame.test;

import java.lang.reflect.Method;

import frame.aop.AbstractHandlerInterceptor;
import frame.stereotype.Aspect;
import frame.stereotype.Order;
import frame.stereotype.Pointcut;

/**
 * 切面表达式，包名-类名是IOC的名称，方法名则是实例的名称
 */
@Aspect
@Order(1)
public class AopInterceptor extends AbstractHandlerInterceptor {

	@Override
	@Pointcut("frame.test.Aop.aopTest")
	public void invokeBefore(Object target, Method method, Object[] args) {
		System.out.println("before " + "target " + target.toString() + " method " + method.toString() + " args " + args);
	}

	@Override
	@Pointcut("frame.test.Aop.aopTest")
	public void invokeAfter(Object target, Method method, Object[] args) {
		System.out.println("after " + "target " + target.toString() + " method " + method.toString() + " args " + args);
	}
	
}
