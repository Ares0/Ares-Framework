package frame.test;

import java.lang.reflect.Method;

import frame.aop.AbstractHandlerInterceptor;
import frame.stereotype.Aspect;
import frame.stereotype.Component;
import frame.stereotype.Pointcut;

@Component
@Aspect
public class AopInterceptor extends AbstractHandlerInterceptor {

	@Override
	@Pointcut("frame.test.Aop.aopTest")
	public void invoke(Object target, Method method, Object[] args) {
		System.out.println("target " + target.toString() + " method " + method.toString() + " args " + args);
	}

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
