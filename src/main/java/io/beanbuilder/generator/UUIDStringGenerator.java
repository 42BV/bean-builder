/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanbuilder.generator;

import java.util.UUID;

/**
 * Generates a random UUID string value.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public class UUIDStringGenerator implements ValueGenerator {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String generate(Class<?> type) {
        return UUID.randomUUID().toString();
    }
    
}
