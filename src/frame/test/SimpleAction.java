package frame.test;

import frame.stereotype.Controller;
import frame.stereotype.RequestMapping;
import frame.stereotype.ResponseData;

@Controller("/simple")
public class SimpleAction {

	@ResponseData("JSON")
	@RequestMapping("/json.do")
	public String getJson() {
		return "{}";
	}
	
	@RequestMapping("/jsp.do")
	public String getJsp() {
		return "{}";
	}
	
}
