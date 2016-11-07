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
 *  支持普通参数、req、rep的参数注入。
 * */
@Component
public class HandlerAdapter {

	public Object handle(Object controller, Method cm, Parameter[] parameters, 
			String[] parameterNames, HttpServletRequest req, HttpServletResponse rep,
			WebApplicationContext wc) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Object[] param = new Object[parameters.length];
		Map<String, String[]> reqParam = req.getParameterMap();
		
		for (int i = 0; i < Math.min(parameters.length, parameterNames.length); i++) {
			Parameter p = parameters[i];
			String[] values = reqParam.get(parameterNames[i]);
			if (values == null) {
				param[i] = getDefaultValue(p);
			} else if (p.getType() == HttpServletRequest.class) {
				param[i] = req;
			} else if (p.getType() == HttpServletResponse.class) {
				param[i] = rep;
			} else {
				param[i] = getParameterValue(p, values);
			}
		}
		return cm.invoke(controller, param);
	}

	private Object getDefaultValue(Parameter p) {
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
	
	private Object getParameterValue(Parameter p, String[] values) {
		Class<?> type = p.getType();
		
		if (type == String.class) {
			return values[0].toString();
		} 
		else if (type == char.class) {
			return values[0];
		} 
		else if (type == byte.class) {
			return Byte.parseByte(values[0].toString());
		} 
		else if (type == int.class) {
			return Integer.parseInt(values[0].toString());
		} 
		else if (type == long.class) {
			return Long.parseLong(values[0].toString());
		} 
		else if (type == float.class) {
			return Float.parseFloat(values[0].toString());
		} 
		else if (type == double.class) {
			return Double.parseDouble(values[0].toString());
		} 
		else {
			return values;
		}
	}

}
