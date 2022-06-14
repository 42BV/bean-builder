package nl._42.beanie.generator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Generates values of any type, using the behavior registered to that value type.
 * 
 * @author Jeroen van Schagen
 */
public class TypeBasedValueGenerator implements ValueGenerator {

    private final Map<Class<?>, ValueGenerator> generators;

    private final ValueGenerator fallback;

    public TypeBasedValueGenerator(ValueGenerator fallback) {
    	generators = new LinkedHashMap<>();
        this.fallback = fallback;
    }
    
    /**
     * Creates a direct clone of this value generator.
     * 
     * @return the cloned instance
     */
    public TypeBasedValueGenerator clone() {
        TypeBasedValueGenerator result = new TypeBasedValueGenerator(fallback);
        result.generators.putAll(generators);
        return result;
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public Object generate(Class<?> type) {
		ValueGenerator generator = getSupportedGenerator(type);
        if (generator == null) {
            if (fallback == null) {
                throw new IllegalArgumentException("Could not generate value for '" + type.getName() + "'.");
            }
            generator = fallback;
        }
        return generator.generate(type);
    }

    private ValueGenerator getSupportedGenerator(Class<?> type) {
    	ValueGenerator generator = generators.get(type);
    	if (generator == null) {
        	generator = findFirstAssignableGenerator(type);
    	}
        return generator;
    }

	private ValueGenerator findFirstAssignableGenerator(Class<?> type) {
        ValueGenerator generator = null;
		for (Entry<Class<?>, ValueGenerator> entry : generators.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
            	generator = entry.getValue();
            	break;
            }
        }
		return generator;
	}

    /**
     * Register a value generation strategy for a specific type.
     *
     * @param type the type of value
     * @param generator the generation strategy
     * @return this instance
     */
    public TypeBasedValueGenerator register(Class<?> type, ValueGenerator generator) {
        generators.put(type, generator);
        return this;
    }

    /**
     * Register a value generation strategy for a specific type.
     *
     * @param type the type of value
     * @param generator the generation strategy
     * @return this instance
     */
    public <T> TypeBasedValueGenerator register(Class<T> type, Supplier<T> generator) {
        return register(type, arg -> generator.get());
    }

    /**
     * Register a constant value for a specific type.
     * 
     * @param type the type of value
     * @param value the value to return
     * @return this instance
     */
    public TypeBasedValueGenerator registerValue(Class<?> type, Object value) {
        return register(type, new ConstantValueGenerator(value));
    }
    
    /**
     * Determine if the value is known in our mapping.
     * 
     * @param type the type of value
     * @return if it exists
     */
    public boolean contains(Class<?> type) {
        return getSupportedGenerator(type) != null;
    }

}
