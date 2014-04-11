/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.beanbuilder.generator.constructor;

import java.lang.reflect.Constructor;

/**
 * Selects the constructor that should be used for bean construction. 
 *
 * @author Jeroen van Schagen
 * @since Feb 14, 2014
 */
public interface ConstructorStrategy {
    
    /**
     * Find the best suited constructor of the supplied bean.
     * 
     * @param beanClass the bean class
     * @return the found constructor
     */
    Constructor<?> getConstructor(Class<?> beanClass);

}
