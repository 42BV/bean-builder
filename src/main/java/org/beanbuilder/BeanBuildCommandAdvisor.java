/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.beanbuilder;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.beanbuilder.generator.ValueGenerator;
import org.springframework.aop.Advisor;

/**
 * Appends bean building logic to builders, allowing custom builder interfaces.
 * Whenever a custom method is invoked, such as <code>withId(1)</code> we will
 * automatically decorate the bean with an "id" property value of 1.
 * <p>
 * Providing no argument, such as <code>withId()</code>, we decorate the bean
 * with a generated "id" property value. The property value is generated using
 * the same bean builder.
 *
 * @author Jeroen van Schagen
 * @since Feb 14, 2014
 */
public final class BeanBuildCommandAdvisor implements Advisor {
    
    private static final String WITH_PREFIX = "with";

    private final EditableBeanBuildCommand<?> command;
    
    public BeanBuildCommandAdvisor(EditableBeanBuildCommand<?> command) {
        this.command = command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Advice getAdvice() {
        return new CustomBeanBuilderAdvice();
    }
    
    private class CustomBeanBuilderAdvice implements MethodInterceptor {
        
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            final String methodName = invocation.getMethod().getName();
            if (methodName.startsWith(WITH_PREFIX)) {
                String propertyName = getPropertyName(methodName);
                Object[] arguments = invocation.getArguments();
                if (arguments.length == 0) {
                    return command.generateValue(propertyName);
                } else {
                    return withArgument(propertyName, arguments);
                }
            } else {
                return invocation.proceed();
            }
        }

        private String getPropertyName(final String methodName) {
            String propertyName = methodName.substring(WITH_PREFIX.length());
            return uncapitalize(propertyName);
        }

        private String uncapitalize(String propertyName) {
            return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }

        private Object withArgument(String propertyName, Object[] arguments) {
            Object value = arguments[0];
            if (value instanceof ValueGenerator) {
                return command.generateValue(propertyName, (ValueGenerator) value);
            } else {
                return command.setValue(propertyName, value);
            }
        }

    }

}
