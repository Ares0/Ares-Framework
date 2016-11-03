package frame.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.stereotype.Component;

/**
 *  ResponseData获取返回类型，支持jsp、json解析；
 * 可能使用工厂模式，由不同的resolver解析。
 * */
@Component
public class ViewResolver {

	private final static String VIEW_JSP = "JSP";
	
	private final static String VIEW_JSON = "JSON";
	
	public void resolve(String view, Object result, HttpServletRequest req, HttpServletResponse rep) {
		if (view == null || view.equals("") || view.equals(VIEW_JSP)) {
			resolveJsp(view, result, req, rep);
		} else if (view.equals(VIEW_JSON)) {
			resolveJson(view, result, req, rep);
		}
	}

	private void resolveJsp(String view, Object result,HttpServletRequest req, HttpServletResponse rep) {
		if (result instanceof Model) {
			Map<?, ?> rm = (Model)result;
			for (Map.Entry<?, ?> e : rm.entrySet()) {
				req.setAttribute((String) e.getKey(), e.getValue());
			}
		} else if (result instanceof Map<?, ?>) {
			Map<?, ?> rm = (Map<?, ?>)result;
			for (Map.Entry<?, ?> e : rm.entrySet()) {
				req.setAttribute((String) e.getKey(), e.getValue());
			}
		}
		
		String redirectPath = "";
		if (!view.equals("")) {
			redirectPath = view.concat(".jsp");
		} else if (result instanceof String) {
			redirectPath = result.toString();
		}
		
		try {
			if (redirectPath.equals("")) 
				throw new IllegalArgumentException("跳转路径错误");
			else
				req.getRequestDispatcher(redirectPath).forward(req, rep);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void resolveJson(String view, Object result, HttpServletRequest req, HttpServletResponse rep) {
		if (result instanceof String) {
			try {
				String rs = (String)result;
				
				rep.setContentType("text/javascript;charset=utf-8");
				rep.getWriter().write(rs);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
