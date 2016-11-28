package frame.core;

public class BeanKey {

	private String name;
	
	private Class<?> beanClass;
	
	public BeanKey(String name) {
		this.name = name;
	}
	
	public BeanKey(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	public BeanKey(String name, Class<?> beanClass) {
		this.name = name;
		this.beanClass = beanClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public int hashCode() {
		if (name == null) {
			return beanClass.hashCode();
		} else {
			return name.hashCode();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BeanKey) {
			BeanKey bk = (BeanKey)obj;
			String bkName = bk.getName();
			if (bkName != null && bkName.equals(this.name)){
				return true;
			}
			Class<?> bkClass = bk.getBeanClass();
			if (bkClass != null && bkClass.equals(this.beanClass)) {
				return true;
			}
		} 
		return false;
	}
	
}
