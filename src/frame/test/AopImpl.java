package frame.test;

import frame.stereotype.Component;

@Component("Aop")
public class AopImpl implements Aop {
	
	public void aopTest() {
		System.out.println("invoke");
	}
	
}
