/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.beanie.generator.increment;

import nl._42.beanie.generator.ValueGenerator;

/**
 * Value generator capable of incrementing itself. 
 *
 * @author Jeroen van Schagen
 * @since Apr 11, 2014
 */
public abstract class AbstractValueIncrementor<T> implements ValueGenerator {
    
    private T current;
    
    public AbstractValueIncrementor(T initial) {
        this.current = initial;
    }
    
    @Override
    public Object generate(Class<?> type) {
        T result = current;
        current = increment(current);
        return result;
    }
    
    protected abstract T increment(T current);

}
