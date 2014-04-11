/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.beanbuilder.generator.random;

import org.beanbuilder.generator.ValueGenerator;


/**
 * Retrieves a random value from an array of possibilities.
 *
 * @author Sander Benschip
 * @since Feb 14, 2014
 */
public class AnyOfArrayValueGenerator extends RandomSupport implements ValueGenerator {
    
    private final Object[] values;
    
    public AnyOfArrayValueGenerator(Object... values) {
        this.values = values;
    }
    
    @Override
    public Object generate(Class<?> valueType) {
        Object result = null;
        if (values.length > 0) {
            result = values[randomInt(values.length)];
        }
        return result;
    }

}
