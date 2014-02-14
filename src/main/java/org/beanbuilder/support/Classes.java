package org.beanbuilder.support;

import java.util.Arrays;

import org.apache.log4j.Logger;

public class Classes {

	private static final Logger LOGGER = Logger.getLogger(Classes.class);

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Could not load class: " + className, e);
		}
	}

	public static boolean hasNullaryConstructor(Class<?> clazz) {
		return hasConstructor(clazz);
	}

	private static boolean hasConstructor(Class<?> clazz, Class<?>... types) {
		try {
			clazz.getDeclaredConstructor(types);
			return true;
		} catch (NoSuchMethodException e) {
			LOGGER.trace("Class '" + clazz.getName() + "' has no constructor for " + Arrays.toString(types) + ".", e);
			return false;
		}
	}

}
