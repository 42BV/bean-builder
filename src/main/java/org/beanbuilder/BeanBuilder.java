/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.beanbuilder;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.beanbuilder.generate.ConstantValueGenerator;
import org.beanbuilder.generate.SavableValueGenerator;
import org.beanbuilder.generate.TypeValueGenerator;
import org.beanbuilder.generate.ValueGenerator;
import org.beanbuilder.generate.construction.ConstructingBeanGenerator;
import org.beanbuilder.generate.construction.ConstructorStrategy;
import org.beanbuilder.generate.construction.ShortestConstructorStrategy;
import org.beanbuilder.save.BeanSaver;
import org.beanbuilder.save.UnsupportedBeanSaver;
import org.beanbuilder.support.PropertyReference;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.GenericTypeResolver;


/**
 * Builds new bean instances.
 *
 * @author Jeroen van Schagen
 * @since Feb 14, 2014
 */
public class BeanBuilder implements SavableValueGenerator {
    
    private final Set<PropertyReference> skippedProperties = new HashSet<>();

    private final Map<PropertyReference, ValueGenerator> referenceValueGenerators = new HashMap<>();
    
    private final Map<String, ValueGenerator> nameValueGenerators = new HashMap<>();

    private final TypeValueGenerator typeValueGenerator;
    
    private final ValueGenerator beanGenerator;
    
    private final BeanSaver beanSaver;

    public BeanBuilder() {
        this(new UnsupportedBeanSaver());
    }
    
    public BeanBuilder(BeanSaver beanSaver) {
        this(new ShortestConstructorStrategy(), beanSaver);
    }
    
    public BeanBuilder(ConstructorStrategy constructorStrategy, BeanSaver beanSaver) {
        this.typeValueGenerator = new TypeValueGenerator(this);
        this.beanGenerator = new ConstructingBeanGenerator(constructorStrategy, this);
        this.beanSaver = beanSaver;
    }

    /**
     * Start building a new bean.
     * 
     * @param beanClass the type of bean to start building
     * @return the bean build command
     */
    public <T> ConfigurableBeanBuildCommand<T> newBean(Class<T> beanClass) {
        return new DefaultBeanBuildCommand<T>(this, beanClass);
    }
    
    /**
     * Start building a new bean, using a custom builder interface.
     * 
     * @param beanClass the type of bean to start building
     * @param commandType the build command interface
     * @return the builder instance, capable of building beans
     */
    @SuppressWarnings("unchecked")
    public <T extends BeanBuildCommand<?>> T newBeanBy(Class<T> commandType) {
        final Class<?> beanClass = GenericTypeResolver.resolveTypeArguments(commandType, BeanBuildCommand.class)[0];
        final ConfigurableBeanBuildCommand<?> command = newBean(beanClass);

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(new SingletonTargetSource(command));
        proxyFactory.addInterface(commandType);
        proxyFactory.addAdvisor(new CustomBeanBuilderAdvisor(command));
        return (T) proxyFactory.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object generate(Class<?> beanClass) {
        return newBean(beanClass).complete().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object generateAndSave(Class<?> beanClass) {
        return newBean(beanClass).complete().buildAndSave();
    }

    private Object generatePropertyValue(Class<?> beanClass, PropertyDescriptor propertyDescriptor, boolean autoSave) {
        ValueGenerator propertyGenerator = getPropertyGenerator(beanClass, propertyDescriptor);
        if (autoSave && propertyGenerator instanceof SavableValueGenerator) {
            return ((SavableValueGenerator) propertyGenerator).generateAndSave(propertyDescriptor.getPropertyType());
        } else {
            return propertyGenerator.generate(propertyDescriptor.getPropertyType());
        }
    }

    private ValueGenerator getPropertyGenerator(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
        ValueGenerator propertyGenerator = this;
        PropertyReference propertyReference = new PropertyReference(beanClass, propertyDescriptor.getName());
        if (referenceValueGenerators.containsKey(propertyReference)) {
            propertyGenerator = referenceValueGenerators.get(propertyReference);
        } else if (nameValueGenerators.containsKey(propertyDescriptor.getName())) {
            propertyGenerator = nameValueGenerators.get(propertyDescriptor.getName());
        } else if (typeValueGenerator.contains(beanClass)) {
            propertyGenerator = typeValueGenerator;
        }
        return propertyGenerator;
    }
    
    /**
     * Skip a property from being generated.
     * 
     * @param declaringClass the bean that declares this property
     * @param propertyName the property name
     * @return this instance
     */
    public BeanBuilder skip(Class<?> declaringClass, String propertyName) {
        skippedProperties.add(new PropertyReference(declaringClass, propertyName));
        return this;
    }

    /**
     * Register a value generation strategy for a specific property reference.
     * 
     * @param declaringClass the bean class that declares our property
     * @param propertyName the name of the property
     * @param generator the generation strategy
     * @return this instance
     */
    public BeanBuilder register(Class<?> declaringClass, String propertyName, ValueGenerator generator) {
        referenceValueGenerators.put(new PropertyReference(declaringClass, propertyName), generator);
        return this;
    }
    
    /**
     * Register a constant value for a specific property reference.
     * 
     * @param declaringClass the bean class that declares our property
     * @param propertyName the name of the property
     * @param value the value to return
     * @return this instance
     */
    public BeanBuilder registerValue(Class<?> declaringClass, String propertyName, Object value) {
        return register(declaringClass, propertyName, new ConstantValueGenerator(value));
    }
    
    /**
     * Register a value generation strategy for a specific property name.
     * 
     * @param propertyName the name of the property
     * @param generator the generation strategy
     * @return this instance
     */
    public BeanBuilder register(String propertyName, ValueGenerator generator) {
        nameValueGenerators.put(propertyName, generator);
        return this;
    }
    
    /**
     * Register a constant value for a specific property name.
     * 
     * @param propertyName the name of the property
     * @param value the value to return
     * @return this instance
     */
    public BeanBuilder registerValue(String propertyName, Object value) {
        return register(propertyName, new ConstantValueGenerator(value));
    }
    
    /**
     * Register a value generation strategy for a specific type.
     * 
     * @param valueType the type of value
     * @param generator the generation strategy
     * @return this instance
     */
    public BeanBuilder register(Class<?> valueType, ValueGenerator generator) {
        typeValueGenerator.register(valueType, generator);
        return this;
    }
    
    /**
     * Register a constant value for a specific type.
     * @param valueType the type of value
     * @param value the value to return
     * @return this instance
     */
    public BeanBuilder registerValue(Class<?> valueType, Object value) {
        return register(valueType, new ConstantValueGenerator(value));
    }

    /**
     * Command for building beans.
     *
     * @author Jeroen van Schagen
     * @since Feb 14, 2014
     */
    public interface BeanBuildCommand<T> {
        
        /**
         * Generate all untouched values in our bean.
         * 
         * @return this instance, for chaining
         */
        BeanBuildCommand<T> complete();
        
        /**
         * Build the new bean.
         * 
         * @return the created bean
         */
        T build();
        
        /**
         * Build and save new bean.
         * 
         * @return the saved bean
         */
        T buildAndSave();

    }
    
    /**
     * Bean build command that allows users to declare custom property values.
     *
     * @author Jeroen van Schagen
     * @since Feb 14, 2014
     */
    public interface ConfigurableBeanBuildCommand<T> extends BeanBuildCommand<T> {
        
        /**
         * Generate a value in our to be generated bean.
         * 
         * @param propertyName the property name
         * @return this instance, for chaining
         */
        ConfigurableBeanBuildCommand<T> generateValue(String propertyName);
        
        /**
         * Declare a value in our to be generated bean.
         * 
         * @param propertyName the property name
         * @param value the property value
         * @return this instance, for chaining
         */
        ConfigurableBeanBuildCommand<T> withValue(String propertyName, Object value);

    }

    /**
     * Default implementation of the bean build command.
     *
     * @author Jeroen van Schagen
     * @since Feb 14, 2014
     */
    private static class DefaultBeanBuildCommand<T> implements ConfigurableBeanBuildCommand<T> {
        
        private final Set<String> touchedProperties = new HashSet<>();
        
        private final Set<String> generatedProperties = new HashSet<>();

        private final BeanBuilder builder;
        
        private final Class<T> beanClass;

        private final BeanWrapper beanWrapper;

        public DefaultBeanBuildCommand(BeanBuilder builder, Class<T> beanClass) {
            this.builder = builder;
            this.beanClass = beanClass;

            Object bean = builder.beanGenerator.generate(beanClass);
            beanWrapper = new BeanWrapperImpl(bean);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigurableBeanBuildCommand<T> withValue(String propertyName, Object value) {
            beanWrapper.setPropertyValue(propertyName, value);
            touchedProperties.add(propertyName);
            generatedProperties.remove(propertyName);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigurableBeanBuildCommand<T> generateValue(String propertyName) {
            touchedProperties.add(propertyName);
            generatedProperties.add(propertyName);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigurableBeanBuildCommand<T> complete() {
            for (PropertyDescriptor propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
                String propertyName = propertyDescriptor.getName();
                if (propertyDescriptor.getWriteMethod() != null && ! touchedProperties.contains(propertyName)) {
                    if (! builder.skippedProperties.contains(new PropertyReference(propertyDescriptor))) {
                        generateValue(propertyName);
                    }
                }
            }
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T build() {
            return build(false);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public T buildAndSave() {
            T bean = build(true);
            return builder.beanSaver.save(bean);
        }
        
        @SuppressWarnings("unchecked")
        private T build(boolean autoSave) {
            for (String generatedProperty : new HashSet<>(generatedProperties)) {
                PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(generatedProperty);
                Object generatedValue = builder.generatePropertyValue(beanClass, propertyDescriptor, autoSave);
                withValue(generatedProperty, generatedValue);
            }
            return (T) beanWrapper.getWrappedInstance();
        }

    }

}
