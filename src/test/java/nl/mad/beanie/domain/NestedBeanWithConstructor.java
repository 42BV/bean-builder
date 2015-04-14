package nl.mad.beanie.domain;

public class NestedBeanWithConstructor {
	
	private final Object value;

	public NestedBeanWithConstructor(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
}
