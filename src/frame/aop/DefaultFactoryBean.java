package frame.aop;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import frame.core.BeanDefinition;
import frame.core.BeanFactory;
import frame.core.BeanKey;
import frame.core.BeanWrapper;
import frame.utils.Utils;

public class DefaultFactoryBean implements FactoryBean {

	private BeanFactory bf;
	
	private Map<BeanKey, HandlerInterceptor> interceptors;
	
	public DefaultFactoryBean(BeanFactory b) {
		this.bf = b;
		interceptors = new HashMap<>();
	}
	
	/*
	 * load interceptors
	 */
	public void initFactoryBean() {
		for (Entry<BeanKey, BeanDefinition> e : bf.getBeanDefinition().entrySet()) {
			BeanDefinition bd = e.getValue();
			if (bd.isAspect()) {
				BeanKey hk = bd.getBeanKey();
				List<String> exprs = bd.getAspectExpression();
				HandlerInterceptor hi = (HandlerInterceptor) bf.getBean(hk);
				
				if (exprs != null && exprs.size() > 0) {
					hi.setAspectExpression(bd.getAspectExpression());
				} else {
					throw new IllegalArgumentException();
				}
				interceptors.put(hk, hi);
			}
		}
	}
	
	@Override
	public BeanWrapper getObject(BeanDefinition bd) {
		BeanWrapper bw = null;
		try {
			Object instance = bd.getBeanClass().newInstance();
			List<HandlerInterceptor> his = getObjectInterceptors(bd);
			
			if (his == null) {
				bw = new BeanWrapper();
				bw.setBean(instance);
				bw.setInstance(instance);
			} 
			else {
				AopProxy proxy = new AopProxy(instance, his);  
				Object object =  Proxy.newProxyInstance(Utils.getCurrentClassLoader(), bd.getBeanClass().getInterfaces(), proxy);
				bw = new BeanWrapper();
				bw.setBean(object);
				bw.setInstance(instance);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		return bw;
	}

	private List<HandlerInterceptor> getObjectInterceptors(BeanDefinition bd) {
		boolean order = false;
		List<HandlerInterceptor> his = null;
		
		for (Entry<BeanKey, HandlerInterceptor> e : interceptors.entrySet()) {
			HandlerInterceptor hi = e.getValue();
			
			if (hi.isMatchClass(bd.getName())) {
				BeanKey bk = BeanKey.getBeanKey(hi.getClass());
				Map<BeanKey, BeanDefinition> bds = bf.getBeanDefinition();
				
				if (bds.get(bk).getLevel() != 0) {
					order = true;
				}
				if (his == null) {
					his = new ArrayList<>();
				}
				his.add(hi);
			}
		}
		
		if (order) {
			Collections.sort(his);
		}
		return his;
	}

}
