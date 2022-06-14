/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.beanie.generator;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Default value generator, registers a generator for all common types.
 *
 * @author Jeroen van Schagen
 * @since Apr 11, 2014
 */
public class DefaultValueGenerator extends TypeBasedValueGenerator {
    
    public DefaultValueGenerator() {
        this(new NoArgBeanGenerator());
    }
    
    public DefaultValueGenerator(ValueGenerator fallback) {
        super(fallback);
        registerDefaultGenerators();
    }

    private void registerDefaultGenerators() {
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
        registerValue(BigDecimal.class, new BigDecimal("0.0"));
        registerValue(String.class, "value");
        registerValue(Class.class, Object.class);

        registerValue(java.time.LocalDate.class, java.time.LocalDate.now());
        registerValue(java.time.LocalDateTime.class, java.time.LocalDateTime.now());
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

        register(Collection.class, () -> List.of());
        register(List.class, () -> List.of());
        register(Set.class, () -> Set.of());
        register(Map.class, Map::of);
        register(Object[].class, new EmptyArrayValueGenerator());
        register(Enum.class, new FirstEnumValueGenerator());
        register(UUID.class, UUID::randomUUID);
    }

}
