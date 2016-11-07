package frame.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.stereotype.Component;

/**
 *  支持JSP、JSON解析；
 * 可能使用工厂模式，由不同的resolver解析。
 * */
@Component
public class ViewResolver {

	private final static String VIEW_JSP = "JSP";
	
	private final static String VIEW_JSON = "JSON";
	
	public void resolve(String viewType, String viewPath, Object result, HttpServletRequest req, HttpServletResponse rep) {
		if (viewType == null || viewType.equals("") || viewType.equals(VIEW_JSP)) {
			resolveJsp(viewPath, result, req, rep);
		} else if (viewType.equals(VIEW_JSON)) {
			resolveJson(result, req, rep);
		}
	}

	private void resolveJsp(String viewPath, Object result,HttpServletRequest req, HttpServletResponse rep) {
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
		if (viewPath != null && !viewPath.equals("")) {
			redirectPath = viewPath.concat(".jsp");
		} else if (result instanceof String) {
			redirectPath = result.toString().concat(".jsp");
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

	private void resolveJson(Object result, HttpServletRequest req, HttpServletResponse rep) {
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
