package io.dynamo.generator.supported;

import io.dynamo.generator.ValueGenerator;

public class SupportableValueGenerators {
    
    private final ValueGenerator generator;
    
    private final Supportable supportable;
    
    public SupportableValueGenerators(ValueGenerator generator, Supportable supportable) {
        this.generator = generator;
        this.supportable = supportable;
    }
    
    public Supportable getSupportable() {
        return supportable;
    }
    
    public ValueGenerator getGenerator() {
        return generator;
    }
    
}