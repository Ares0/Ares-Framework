package frame.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.stereotype.Component;

@Component("JSONViewResolver")
public class JSONViewResolver implements ViewResolver {

	@Override
	public void resolve(String viewPath, Object result, HttpServletRequest req, HttpServletResponse rep) {
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
