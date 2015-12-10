/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package io.beanbuilder.save;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.core.annotation.AnnotationUtils;

/**
 * Persists entities in the entity manager. Non entity
 * values are ignored and just returned without actions.
 *
 * @author Jeroen van Schagen
 * @since Mar 10, 2014
 */
public class JpaBeanSaver implements BeanSaver {
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T save(final T bean) {
        if (isSaveable(bean)) {
            entityManager.persist(bean);
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object bean) {
        if (isSaveable(bean)) {
            entityManager.remove(bean);
        }
    }
    
    private <T> boolean isSaveable(T value) {
        return value != null && isEntity(value);
    }

    private boolean isEntity(Object value) {
        return AnnotationUtils.findAnnotation(value.getClass(), Entity.class) != null;
    }
    
}
