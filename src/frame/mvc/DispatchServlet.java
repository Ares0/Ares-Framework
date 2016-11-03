package frame.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.core.BeanDefinition;
import frame.core.WebApplicationContext;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -945745153204232243L;
	
	public static final String CONTROLLER_MAPPING = "controllerMapping";
	
	public static final String METHOD_MAPPING = "methodMapping";
	
	public static final String VIEW_MAPPING = "viewMapping";
	
	public static final String HANDLER_ADAPTER = "HandlerAdapter";
	
	public static final String VIEW_RESOLVER = "ViewResolver";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext sc = config.getServletContext();
		String cfg = sc.getInitParameter(WebApplicationContext.CONFIG_PATH);
		
		if (cfg != null) {
			WebApplicationContext wc;
			String wcn = WebApplicationContext.SERVLETCONTEXT_BEANFACTORY;
			if ((wc = (WebApplicationContext) sc.getAttribute(wcn)) == null) {
				setWebApplicationContext(sc, wcn, cfg);
			} 
			
			if ((wc = (WebApplicationContext) sc.getAttribute(wcn)) != null) {
				Map<String, Method> methodMapping = new HashMap<>();
				Map<String, String> controllerMapping = new HashMap<>();
				Map<Method, String> viewMapping = new HashMap<>();
				
				for (Map.Entry<String, BeanDefinition> e : wc.getBeanNameDefinition().entrySet()) {
					BeanDefinition bd = e.getValue();
					if (bd.isController()) {
						// req
						for (Map.Entry<String, Method> m : bd.getRequestMapping().entrySet()) {
							methodMapping.put(m.getKey(), m.getValue());
							controllerMapping.put(m.getKey(), bd.getName());
						}
						// view
						for (Map.Entry<Method, String> m : bd.getResponseMapping().entrySet()) {
							viewMapping.put(m.getKey(), m.getValue());
						}
					}
				}
				sc.setAttribute(METHOD_MAPPING, methodMapping);
				sc.setAttribute(CONTROLLER_MAPPING, controllerMapping);
				sc.setAttribute(VIEW_MAPPING, viewMapping);
			}
		}
	}

	private synchronized void setWebApplicationContext(ServletContext sc, String wcn, String cfg) {
		if (sc.getAttribute(wcn) == null) {
			String path = null;
			path = sc.getRealPath("/WEB-INF").toString().concat(cfg);
			sc.setAttribute(wcn, new WebApplicationContext(path));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest req,
			HttpServletResponse rep) throws ServletException, IOException {
		String path = getRequestPath(req);
		ServletContext sc = req.getSession().getServletContext();
		
		Map<String, String> controllerMapping = (Map<String, String>) sc.getAttribute(CONTROLLER_MAPPING);
		
		String controllerName;
		if ((controllerName = controllerMapping.get(path)) != null) {
			WebApplicationContext wc = (WebApplicationContext) sc.getAttribute(WebApplicationContext.SERVLETCONTEXT_BEANFACTORY);
			Object controller;
			
			if ((controller = wc.getBean(controllerName)) != null) {
				Map<String, Method> methodMapping = (Map<String, Method>) sc.getAttribute(METHOD_MAPPING);
				Method cm = methodMapping.get(path);
				
				if (cm != null) {
					String view = ((Map<Method, String>) sc.getAttribute(VIEW_MAPPING)).get(cm);
					doService(controller, cm, view, req, rep, wc);
				}
			}
		}
	}

	private String getRequestPath(HttpServletRequest req) {
		String url = req.getServletPath();
		String pathInfo = req.getPathInfo();
		
		if (pathInfo != null && pathInfo.length() > 0) {
		    url = url.concat(pathInfo);
		}
		return url;
	}
	
	private void doService(Object controller, Method cm, 
			String view, HttpServletRequest req, HttpServletResponse rep, WebApplicationContext wc) {
		HandlerAdapter ha = (HandlerAdapter) wc.getBean(HANDLER_ADAPTER);

		try {
			Object result;
			result = ha.service(controller, cm, req, rep, wc);
			
			ViewResolver vr = (ViewResolver) wc.getBean(VIEW_RESOLVER);
			vr.resolve(view, result, req, rep);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
