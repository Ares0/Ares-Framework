package frame.test;

import frame.stereotype.Component;
import frame.stereotype.Resource;

@Component("Aop")
public class AopImpl implements Aop {
	
	@Resource
	private IocComponent ic;
	
	public void aopTest() {
		System.out.println("invoke");
	}
	
}
