package frame.aop;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frame.core.BeanDefinition;
import frame.core.BeanFactory;
import frame.utils.Utils;

public class DefaultFactoryBean implements FactoryBean {

	private BeanFactory bf;
	
	private Map<String, HandlerInterceptor> interceptors;
	
	public DefaultFactoryBean(BeanFactory b) {
		this.bf = b;
		interceptors = new HashMap<>();
	}
	
	public void initFactoryBean() {
		for (Map.Entry<String, BeanDefinition> e : bf.getBeanNameDefinition().entrySet()) {
			BeanDefinition bd = e.getValue();
			if (bd.isAspect()) {
				String hiName = bd.getName();
				List<String> exprs = bd.getAspectExpression();
				HandlerInterceptor hi = (HandlerInterceptor) bf.getBean(hiName);
				
				if (exprs != null && exprs.size() > 0) {
					hi.setAspectExpression(bd.getAspectExpression());
				} else {
					throw new IllegalArgumentException("表达式错误");
				}
				interceptors.put(hiName, hi);
			}
		}
	}
	
	@Override
	public Object getObject(BeanDefinition bd) {
		List<HandlerInterceptor> his = getObjectInterceptors(bd);
		try {
			if (his == null) {
				return bd.getBeanClass().newInstance();
			} else {
				// 只装载匹配的类 hi
				AopProxy proxy = new AopProxy(bd.getBeanClass().newInstance(), his);  
				return Proxy.newProxyInstance(Utils.getCurrentClassLoader(), bd.getBeanClass().getInterfaces(), proxy);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		return null;
	}

	private List<HandlerInterceptor> getObjectInterceptors(BeanDefinition bd) {
		List<HandlerInterceptor> his = null;
		for (Map.Entry<String, HandlerInterceptor> e : interceptors.entrySet()) {
			// 是否匹配当前类
			if (e.getValue().isMatchClass(bd.getName())) {
				if (his == null) 
					his = new ArrayList<>();
				his.add(e.getValue());
			}
		}
		return his;
	}

}
