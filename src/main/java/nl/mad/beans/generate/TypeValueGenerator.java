package nl.mad.beans.generate;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Generates values of any type, using the behavior registered to that value type.
 * @author Jeroen van Schagen
 */
public final class TypeValueGenerator implements ValueGenerator {

    private final Map<Class<?>, ValueGenerator> typeGenerators;

    private final ValueGenerator defaultGenerator;

    public TypeValueGenerator() {
        this(new NoArgEmptyBeanGenerator());
    }

    public TypeValueGenerator(ValueGenerator defaultGenerator) {
    	typeGenerators = new LinkedHashMap<Class<?>, ValueGenerator>();
        this.defaultGenerator = defaultGenerator;

        registerValue(short.class, 0);
        registerValue(byte.class, 0);
        registerValue(float.class, 0);
        registerValue(int.class, 0);
        registerValue(Integer.class, Integer.valueOf(0));
        registerValue(int.class, 0);
        registerValue(Double.class, Double.valueOf(0.0));
        registerValue(double.class, 0.0);
        registerValue(Long.class, Long.valueOf(0));
        registerValue(long.class, 0L);
        registerValue(Boolean.class, Boolean.FALSE);
        registerValue(boolean.class, false);
        registerValue(String.class, "value");
        registerValue(BigDecimal.class, new BigDecimal("0.0"));
        registerValue(java.util.Date.class, new java.util.Date());
        registerValue(java.sql.Date.class, new java.sql.Date(System.currentTimeMillis()));
        registerValue(Calendar.class, Calendar.getInstance());
        registerValue(byte[].class, new byte[0]);
        registerValue(short[].class, new short[0]);
        registerValue(int[].class, new int[0]);
        registerValue(long[].class, new long[0]);
        registerValue(double[].class, new double[0]);
        registerValue(float[].class, new float[0]);
        registerValue(boolean[].class, new boolean[0]);
        registerValue(char[].class, new char[0]);
        registerValue(List.class, Collections.emptyList());
        registerValue(Set.class, Collections.emptySet());
        registerValue(Collection.class, Collections.emptyList());
        registerValue(Map.class, Collections.emptyMap());
        registerValue(Class.class, Object.class);
        register(Object[].class, new ArrayValueGenerator());
        register(Enum.class, new EnumValueGenerator());
    }

	@Override
    public Object generate(Class<?> valueType) {
		ValueGenerator generator = getSupportedGenerator(valueType);
        return generator.generate(valueType);
    }

    private ValueGenerator getSupportedGenerator(Class<?> valueType) {
    	ValueGenerator generator = typeGenerators.get(valueType);
    	if (generator == null) {
        	generator = getFirstAssignableGenerator(valueType);
    	}
        return generator;
    }

	private ValueGenerator getFirstAssignableGenerator(Class<?> valueType) {
    	ValueGenerator generator = defaultGenerator;
		for (Entry<Class<?>, ValueGenerator> entry : typeGenerators.entrySet()) {
            if (entry.getKey().isAssignableFrom(valueType)) {
            	generator = entry.getValue();
            	break;
            }
        }
		return generator;
	}

    /**
     * Register a value generation strategy for a specific type.
     * @param valueType the type of value
     * @param generator the generation strategy
     * @return this instance
     */
    public TypeValueGenerator register(Class<?> valueType, ValueGenerator generator) {
        typeGenerators.put(valueType, generator);
        return this;
    }

    /**
     * Register a constant value for a specific type.
     * @param valueType the type of value
     * @param value the value to return
     * @return this instance
     */
    public TypeValueGenerator registerValue(Class<?> valueType, Object value) {
        return register(valueType, new ConstantValueGenerator(value));
    }
    
    /**
     * Determine if the value is known in our mapping.
     * @param valueType the type of value
     * @return if it exists
     */
    public boolean contains(Class<?> valueType) {
    	boolean found = false;
        for (Entry<Class<?>, ValueGenerator> entry : typeGenerators.entrySet()) {
            if (entry.getKey().isAssignableFrom(valueType)) {
            	found = true;
            	break;
            }
        }
    	return found;
    }

}
