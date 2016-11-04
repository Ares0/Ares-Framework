package frame.test;

import frame.stereotype.Controller;
import frame.stereotype.RequestMapping;
import frame.stereotype.ResponseData;
import frame.stereotype.ResponseMapping;

@Controller("/simple")
public class SimpleAction {

	@ResponseData("JSON")
	@RequestMapping("/json.do")
	public String getJson(int id, String name) {
		return "{" + id + ", " +name +"}";
	}
	
	@RequestMapping("/jsp.do")
	@ResponseData("JSP")
	@ResponseMapping("/WEB-INF/jsp/hello")
	public String getJsp(int id, String name) {
		return "{" + id + ", " +name +"}";
	}
	
}
