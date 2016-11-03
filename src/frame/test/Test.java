package frame.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import frame.stereotype.Component;
import frame.stereotype.Resource;
import frame.utils.Utils;

@Component
public class Test {

	@Resource
	private IOCResource r;
	
	public static void main(String[] args) {
//		for (Class<?> c : getClasses("frame")) {
//			System.out.println(c);
//		}
		for (Field f : Test.class.getDeclaredFields()) {
			String typeName = f.getType().toString();
//			Object o = f.getType();
			System.out.println(Utils.getBeanNameByClassName(typeName));
		}
		
	}
	
	public static List<Class<?>> getClasses(String packageName){  
        List<Class<?>> classes = new ArrayList<Class<?>>();  
        
        String packageDirName = packageName.replace('.', '/');  
        Enumeration<URL> dirs;  
        
        try {  
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);  
            while (dirs.hasMoreElements()){  
                URL url = dirs.nextElement();  
                String protocol = url.getProtocol();  
                //如果是以文件的形式保存在服务器上  
                if ("file".equals(protocol)) {  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    //以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(packageName, filePath, classes);  
                } 
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
         
        return classes;  
    }  
	
	 public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, List<Class<?>> classes){  
        File dir = new File(packagePath);  
        if (!dir.exists() || !dir.isDirectory()) {  
            return;  
        }  
        
        File[] dirfiles = dir.listFiles(new FileFilter() {  
              public boolean accept(File file) {  
                return (file.isDirectory()) || (file.getName().endsWith(".class"));  
              }  
            });  
        
        for (File file : dirfiles) {  
            //如果是目录 则继续扫描  
            if (file.isDirectory()) {  
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),  
                                      file.getAbsolutePath(), classes);  
            }  
            else {  
                String className = file.getName().substring(0, file.getName().length() - 6);  
                try {  
                    classes.add(Class.forName(packageName + '.' + className));  
                    
                    System.out.println(className);
                    
                } catch (ClassNotFoundException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  

}
