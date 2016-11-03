package frame.utils;

public class Utils {

	public static String getLastNameByPeriod(String typeName) {
		return typeName.substring(typeName.lastIndexOf('.') + 1, typeName.length());
	}
	
	public static ClassLoader getCurrentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
}
