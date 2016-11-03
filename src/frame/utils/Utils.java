package frame.utils;

public class Utils {

	public static String getBeanNameByClassName(String typeName) {
		return typeName.substring(typeName.lastIndexOf('.') + 1, typeName.length());
	}
	
}
