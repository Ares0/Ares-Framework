package frame.test;


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
		t.testJavaReflect(); // 20ms
		t.testAsmReflect();  // 29ms
		
	}
	
	private void testJavaReflect() throws Exception {
//		Test tn = new Test();
//		Class<Test> t = (Class<Test>) Class.forName("frame.test.Test");
//		Method m = t.getMethod("getI", null);
		
		long now = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
//			m.invoke(tn, null);
		}
		System.out.println(System.currentTimeMillis() - now);
	}
	
	private void testAsmReflect() {
//		Test tn = new Test();
//		MethodAccess ma = MethodAccess.get(Test.class);
//		int index = ma.getIndex("getI");
//		
//		long now = System.currentTimeMillis();
//		for(int i = 0; i<1000000; i++){
//		    ma.invoke(tn, index);
//		}
//		System.out.println(System.currentTimeMillis() - now);
	}
	
	private int i;
	
	public int getI() {
		return i;
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
}
