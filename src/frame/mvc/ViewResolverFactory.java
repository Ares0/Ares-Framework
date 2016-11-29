package frame.mvc;

import frame.core.BeanKey;
import frame.stereotype.Component;

@Component("ViewResolverFactory")
public class ViewResolverFactory {
	
	private final static String VIEW_JSP = "JSP";
	
	private final static String VIEW_JSON = "JSON";
	
	private static BeanKey JSP_VIEW_RESOLVER = BeanKey.getBeanKey("JSPViewResolver");
	
	private static BeanKey JSON_VIEW_RESOLVER = BeanKey.getBeanKey("JSONViewResolver");
	
	public static BeanKey getViewResolverSign(String viewType) {
		if (viewType == null || viewType.equals("") || viewType.equals(VIEW_JSP)) {
			return JSP_VIEW_RESOLVER;
		} else if (viewType.equals(VIEW_JSON)) {
			return JSON_VIEW_RESOLVER;
		}
		return null;
	}
	
}
