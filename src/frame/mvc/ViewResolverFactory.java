package frame.mvc;

import frame.stereotype.Component;

@Component("ViewResolverFactory")
public class ViewResolverFactory {
	
	private final static String VIEW_JSP = "JSP";
	
	private final static String VIEW_JSON = "JSON";
	
	private static String JSP_VIEW_RESOLVER = "JSPViewResolver";
	
	private static String JSON_VIEW_RESOLVER = "JSONViewResolver";
	
	public static String getViewResolverSign(String viewType) {
		if (viewType == null || viewType.equals("") || viewType.equals(VIEW_JSP)) {
			return JSP_VIEW_RESOLVER;
		} else if (viewType.equals(VIEW_JSON)) {
			return JSON_VIEW_RESOLVER;
		}
		return null;
	}
	
}
