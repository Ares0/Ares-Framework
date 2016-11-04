package frame.utils;

public class Utils {

	public static String getLastNameByPeriod(String typeName) {
		return typeName.substring(typeName.lastIndexOf('.') + 1, typeName.length());
	}
	
	public static ClassLoader getCurrentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static boolean isMatch(String sp, String sc) {
		return sp.contains(sc);
	}
	
	public static String getFileSystemPath(String path) {
		return path.replace("/", "\\");
	}
}
