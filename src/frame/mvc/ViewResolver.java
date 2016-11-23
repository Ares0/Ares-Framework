package frame.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ViewResolver {

	public static final String VIEW_TYPE = "viewType";
	
	public static final String VIEW_MAPPING = "viewMapping";
	
	public void resolve(String viewPath, Object result, HttpServletRequest req, HttpServletResponse rep);
	
}
