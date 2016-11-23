package frame.test;

import frame.core.support.FileSystemBeanFactory;

public class AopTest {

	public static void main(String[] args) {
		FileSystemBeanFactory fb = new FileSystemBeanFactory("frame.propriety");
		Aop obj = (Aop) fb.getBean("Aop");
		obj.aopTest();
	}
	
}
