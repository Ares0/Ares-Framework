package frame.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import frame.core.BeanDefinition;
import frame.core.support.WebApplicationContext;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -945745153204232243L;
	
	public static final String CONTEXT_INIT = "contextInit";
	
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
				Boolean init = (Boolean) sc.getAttribute(CONTEXT_INIT);
				if (init == null || init == false) {
					Map<String, String> controllerMapping = new HashMap<>();
					
					Map<String, Method> methodMapping = new HashMap<>();
					Map<Method, Parameter[]> parameterMapping = new HashMap<>();
					Map<Method, String[]> parameterNameMapping = new HashMap<>();
					
					Map<Method, String> viewType = new HashMap<>();
					Map<Method, String> viewMapping = new HashMap<>();
					
					for (Map.Entry<String, BeanDefinition> e : wc.getBeanNameDefinition().entrySet()) {
						BeanDefinition bd = e.getValue();
						if (bd.isController()) {
							// req
							for (Map.Entry<String, Method> m : bd.getRequestMapping().entrySet()) {
								methodMapping.put(m.getKey(), m.getValue());
								controllerMapping.put(m.getKey(), bd.getName());
							}
							// param type
							for (Entry<Method, Parameter[]> m : bd.getParameterMapping().entrySet()) {
								parameterMapping.put(m.getKey(), m.getValue());
							}
							// param name
							for (Entry<Method, String[]> m : bd.getParameterNameMapping().entrySet()) {
								parameterNameMapping.put(m.getKey(), m.getValue());
							}
							// view type
							for (Map.Entry<Method, String> m : bd.getResponseType().entrySet()) {
								viewType.put(m.getKey(), m.getValue());
							}
							// view path
							for (Map.Entry<Method, String> m : bd.getResponseMapping().entrySet()) {
								viewMapping.put(m.getKey(), m.getValue());
							}
						}
					}
					
					sc.setAttribute(HandlerAdapter.METHOD_MAPPING, methodMapping);
					sc.setAttribute(HandlerAdapter.PARAMETER_MAPPING, parameterMapping);
					sc.setAttribute(HandlerAdapter.PARAMETER_NAME_MAPPING, parameterNameMapping);
					
					sc.setAttribute(HandlerAdapter.CONTROLLER_MAPPING, controllerMapping);
					sc.setAttribute(ViewResolver.VIEW_TYPE, viewType);
					sc.setAttribute(ViewResolver.VIEW_MAPPING, viewMapping);
					
					sc.setAttribute(CONTEXT_INIT, true);
				}
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
	protected void service(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
		String path = getRequestPath(req);
		ServletContext sc = req.getSession().getServletContext();
		
		Map<String, String> controllerMapping = (Map<String, String>) sc.getAttribute(HandlerAdapter.CONTROLLER_MAPPING);
		
		String controllerName;
		if ((controllerName = controllerMapping.get(path)) != null) {
			WebApplicationContext wc = (WebApplicationContext) sc.getAttribute(WebApplicationContext.SERVLETCONTEXT_BEANFACTORY);
			Object controller;
			
			if ((controller = wc.getBean(controllerName)) != null) {
				Map<String, Method> methodMapping = (Map<String, Method>) sc.getAttribute(HandlerAdapter.METHOD_MAPPING);
				Method cm = methodMapping.get(path);
				
				if (cm != null) {
					String viewType = ((Map<Method, String>) sc.getAttribute(ViewResolver.VIEW_TYPE)).get(cm);
					String viewPath = ((Map<Method, String>) sc.getAttribute(ViewResolver.VIEW_MAPPING)).get(cm);
					
					Parameter[] parameters = ((Map<Method, Parameter[]>) sc.getAttribute(HandlerAdapter.PARAMETER_MAPPING)).get(cm);
					String[] parameterNames = ((Map<Method, String[]>) sc.getAttribute(HandlerAdapter.PARAMETER_NAME_MAPPING)).get(cm);
					
					doService(controller, cm, parameters, parameterNames, viewType, viewPath, req, rep, wc);
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
	
	private void doService(Object controller, Method cm, Parameter[] parameters, 
			String[] parameterNames, String viewType, String viewPath, HttpServletRequest req,
		    HttpServletResponse rep, WebApplicationContext wc) {
		HandlerAdapter ha = (HandlerAdapter) wc.getBean(HandlerAdapter.HANDLER_ADAPTER);

		try {
			Object result;
			result = ha.handle(controller, cm, parameters, parameterNames, req, rep, wc);
			
			ViewResolver vr = (ViewResolver) wc.getBean(ViewResolverFactory.getViewResolverSign(viewType));
			vr.resolve(viewPath, result, req, rep);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
