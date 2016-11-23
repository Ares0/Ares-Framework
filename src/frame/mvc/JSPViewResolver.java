package frame.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.stereotype.Component;

@Component("JSPViewResolver")
public class JSPViewResolver implements ViewResolver {

	@Override
	public void resolve(String viewPath, Object result, HttpServletRequest req, HttpServletResponse rep) {
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
				throw new IllegalArgumentException("Ìø×ªÂ·¾¶´íÎó");
			else
				req.getRequestDispatcher(redirectPath).forward(req, rep);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
