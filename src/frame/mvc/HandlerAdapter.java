package frame.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.core.WebApplicationContext;
import frame.stereotype.Component;

/**
 *  支持普通参数、req、rep的参数注入；
 * 不支持modelAndView，只支持注解方式。
 * */
@Component
public class HandlerAdapter {

	public Object service(Object controller, Method cm, HttpServletRequest req, HttpServletResponse rep,
			WebApplicationContext wc) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Parameter[] parameters = cm.getParameters();
		
		Object[] param = new Object[parameters.length];
		Map<?, ?> reqParam = req.getParameterMap();
		
		int i = 0;
		for (Parameter p : parameters) {
			Object po = reqParam.get(p.getName());
			if (po == null) {
				param[i++] = getDefaultParameter(p);
			} else if (p.getType() == HttpServletRequest.class) {
				param[i++] = req;
			} else if (p.getType() == HttpServletResponse.class) {
				param[i++] = rep;
			}
		}
		return cm.invoke(controller, param);
	}

	private Object getDefaultParameter(Parameter p) {
		Class<?> type = p.getType();
		if (type == char.class) {
			return "";
		} else if (type == float.class) {
			return 0f;
		} else if (type == int.class) {
			return 0;
		} else if (type == byte.class) {
			return 0;
		} else if (type == long.class) {
			return 0;
		} else if (type == double.class) {
			return 0;
		} else {
			return null;
		}
	}

}
