package frame.test;

import java.lang.reflect.Method;

import com.esotericsoftware.reflectasm.MethodAccess;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class Test {

	public static void main(String[] args) throws Exception {
//		for (Class<?> c : getClasses("frame")) {
//			System.out.println(c);
//		}
//		
//		for (Field f : Test.class.getDeclaredFields()) {
//			String typeName = f.getType().toString();
//			Object o = f.getType();
//			System.out.println(f.getClass().toString());
//		}
		
//		System.out.println("frame.test.Test.main".contains("Test"));
		
//		System.out.println(Test.class.getDeclaredMethods()[0].getName());
		
		Test t = new Test();
		
		t.testJavaReflect(); // 39ms
		t.testAsmReflect();  // 35ms
		
		t.testNoCheckReflect(); // 32ms
		t.testNoCacheReflect(); // 665ms
		
//		getParamtername();  
	}

	@SuppressWarnings("unused")
	private static void getParamtername() {
		Class<?> clazz = Test.class;  
	    try {  
	        ClassPool pool = ClassPool.getDefault();  
	        CtClass cc = pool.get(clazz.getName());  
	        CtMethod cm = cc.getDeclaredMethod("main");  
	  
	        MethodInfo methodInfo = cm.getMethodInfo();  
	        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  

	        String[] paramNames = new String[cm.getParameterTypes().length];  
	        
	        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
	        
	        for (int i = 0; i < paramNames.length; i++)  
	            paramNames[i] = attr.variableName(i + pos);  

	        for (int i = 0; i < paramNames.length; i++)  
	            System.out.println(paramNames[i]);  
	    } catch (NotFoundException e) {  
	        e.printStackTrace();  
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void testJavaReflect() throws Exception {
		Test tn = new Test();
		Class<Test> t = (Class<Test>) Class.forName("frame.test.Test");
//		Method m = t.getMethod("getI", null);
		Method m2 = t.getMethod("setI", int.class);
		
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
//			m.invoke(tn, null);
			m2.invoke(tn, 1);
		}
		System.out.println(System.currentTimeMillis() - now);
	}
	
	@SuppressWarnings("unchecked")
	private void testNoCheckReflect() throws Exception {
		Test tn = new Test();
		Class<Test> t = (Class<Test>) Class.forName("frame.test.Test");
//		Method m = t.getMethod("getI", null);
		Method m2 = t.getMethod("setI", int.class);
		
//		m.setAccessible(true);
		m2.setAccessible(true);
		
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
//			m.invoke(tn, null);
			m2.invoke(tn, 1);
		}
		System.out.println(System.currentTimeMillis() - now);
	}
	
	@SuppressWarnings("unchecked")
	private void testNoCacheReflect() throws Exception {
		Test tn = new Test();
		Class<Test> t = (Class<Test>) Class.forName("frame.test.Test");
		
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
//			Method m = t.getMethod("getI", null);
			Method m2 = t.getMethod("setI", int.class);
			
//			m.invoke(tn, null);
			m2.invoke(tn, 1);
		}
		System.out.println(System.currentTimeMillis() - now);
	}
	
	private void testAsmReflect() {
		Test tn = new Test();
		MethodAccess ma = MethodAccess.get(Test.class);
		int index = ma.getIndex("getI");
		int index2 = ma.getIndex("setI");
		
		long now = System.currentTimeMillis();
		for(int i = 0; i<1000000; i++){
		    ma.invoke(tn, index);
		    ma.invoke(tn, index2, 1);
		}
		System.out.println(System.currentTimeMillis() - now);
	}
	
	private int i;
	
	public int getI() {
		return i;
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
}
