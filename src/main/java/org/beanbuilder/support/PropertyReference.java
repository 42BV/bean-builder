package org.beanbuilder.support;

import java.beans.PropertyDescriptor;

import org.apache.commons.lang.builder.EqualsBuilder;

public class PropertyReference {

	private final Class<?> declaringClass;
	
	private final String propertyName;
    
    public PropertyReference(PropertyDescriptor description) {
        this.declaringClass = description.getWriteMethod().getDeclaringClass();
        this.propertyName = description.getName();
    }

	public PropertyReference(Class<?> declaringClass, String propertyName) {
		this.declaringClass = declaringClass;
		this.propertyName = propertyName;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	@Override
	public int hashCode() {
        return declaringClass.hashCode() * propertyName.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return declaringClass.getName() + "." + propertyName;
	}
	
}
