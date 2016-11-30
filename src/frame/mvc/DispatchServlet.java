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
import frame.core.BeanKey;
import frame.core.support.WebApplicationContext;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -945745153204232243L;
	
	public static final String CONTEXT_INIT = "contextInit";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext sc = config.getServletContext();
		String cfg = sc.getInitParameter(WebApplicationContext.CONFIG_PATH);
		
		if (cfg != null) {
			String wcn = WebApplicationContext.SERVLETCONTEXT_BEANFACTORY;
			WebApplicationContext wc = (WebApplicationContext) sc.getAttribute(wcn);
			
			if (wc == null) {
				setWebApplicationContext(sc, wcn, cfg);
			} 
			
			wc = (WebApplicationContext) sc.getAttribute(wcn);
			if (wc != null) {
				Boolean init = (Boolean) sc.getAttribute(CONTEXT_INIT);
				if (init == null || init == false) {
					Map<String, BeanKey> controllerMapping = new HashMap<>();
					
					Map<String, Method> methodMapping = new HashMap<>();
					Map<Method, Parameter[]> parameterMapping = new HashMap<>();
					Map<Method, String[]> parameterNameMapping = new HashMap<>();
					
					Map<Method, String> viewType = new HashMap<>();
					Map<Method, String> viewMapping = new HashMap<>();
					
					prepareMapping(wc, controllerMapping, methodMapping, parameterMapping, 
							parameterNameMapping, viewType, viewMapping);
					
					registerMapping(sc, controllerMapping, methodMapping, parameterMapping,
							parameterNameMapping, viewType, viewMapping);
				}
			}
		}
	}

	private void prepareMapping(WebApplicationContext wc, Map<String, BeanKey> controllerMapping,
			Map<String, Method> methodMapping, Map<Method, Parameter[]> parameterMapping,
			Map<Method, String[]> parameterNameMapping, Map<Method, String> viewType, Map<Method, String> viewMapping) {
		for (Entry<BeanKey, BeanDefinition> e : wc.getBeanDefinition().entrySet()) {
			BeanDefinition bd = e.getValue();
			if (bd.isController()) {
				// req
				for (Map.Entry<String, Method> m : bd.getRequestMapping().entrySet()) {
					methodMapping.put(m.getKey(), m.getValue());
					controllerMapping.put(m.getKey(), bd.getBeanKey());
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
	}

	private void registerMapping(ServletContext sc, Map<String, BeanKey> controllerMapping,
			Map<String, Method> methodMapping, Map<Method, Parameter[]> parameterMapping,
			Map<Method, String[]> parameterNameMapping, Map<Method, String> viewType, Map<Method, String> viewMapping) {
		sc.setAttribute(HandlerAdapter.METHOD_MAPPING, methodMapping);
		sc.setAttribute(HandlerAdapter.PARAMETER_MAPPING, parameterMapping);
		sc.setAttribute(HandlerAdapter.PARAMETER_NAME_MAPPING, parameterNameMapping);
		
		sc.setAttribute(HandlerAdapter.CONTROLLER_MAPPING, controllerMapping);
		sc.setAttribute(ViewResolver.VIEW_TYPE, viewType);
		sc.setAttribute(ViewResolver.VIEW_MAPPING, viewMapping);
		
		sc.setAttribute(CONTEXT_INIT, true);
	}
	
	private synchronized void setWebApplicationContext(ServletContext sc, String wcn, String cfg) {
		if (sc.getAttribute(wcn) == null) {
			String path = null;
			path = sc.getRealPath("/WEB-INF").toString().concat(cfg);
			sc.setAttribute(wcn, new WebApplicationContext(path));
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
		String path = getRequestPath(req);
		ServletContext sc = req.getSession().getServletContext();
		
		Map<String, BeanKey> controllerMapping = getControllerMapping(sc);
		
		BeanKey controllerKey = controllerMapping.get(path);
		if (controllerKey != null) {
			WebApplicationContext wc = getContext(sc);
			Object controller = wc.getBean(controllerKey);
			
			if (controller != null) {
				Map<String, Method> methodMapping = getMethodMapping(sc);
				Method cm = methodMapping.get(path);
				
				if (cm != null) {
					String viewType = getViewType(sc, cm);
					String viewPath = getViewPath(sc, cm);
					
					Parameter[] parameters = getParameters(sc, cm);
					String[] parameterNames = getParameterNames(sc, cm);
					
					doService(controller, cm, parameters, parameterNames, viewType, viewPath, req, rep, wc);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String[] getParameterNames(ServletContext sc, Method cm) {
		return ((Map<Method, String[]>) sc.getAttribute(HandlerAdapter.PARAMETER_NAME_MAPPING)).get(cm);
	}

	@SuppressWarnings("unchecked")
	private Parameter[] getParameters(ServletContext sc, Method cm) {
		return ((Map<Method, Parameter[]>) sc.getAttribute(HandlerAdapter.PARAMETER_MAPPING)).get(cm);
	}

	@SuppressWarnings("unchecked")
	private String getViewPath(ServletContext sc, Method cm) {
		return ((Map<Method, String>) sc.getAttribute(ViewResolver.VIEW_MAPPING)).get(cm);
	}

	@SuppressWarnings("unchecked")
	private String getViewType(ServletContext sc, Method cm) {
		return ((Map<Method, String>) sc.getAttribute(ViewResolver.VIEW_TYPE)).get(cm);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Method> getMethodMapping(ServletContext sc) {
		return (Map<String, Method>) sc.getAttribute(HandlerAdapter.METHOD_MAPPING);
	}

	private WebApplicationContext getContext(ServletContext sc) {
		return (WebApplicationContext) sc.getAttribute(WebApplicationContext.SERVLETCONTEXT_BEANFACTORY);
	}

	@SuppressWarnings("unchecked")
	private Map<String, BeanKey> getControllerMapping(ServletContext sc) {
		return (Map<String, BeanKey>) sc.getAttribute(HandlerAdapter.CONTROLLER_MAPPING);
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
