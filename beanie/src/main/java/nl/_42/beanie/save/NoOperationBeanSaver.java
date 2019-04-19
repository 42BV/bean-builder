/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.beanie.save;

/**
 * Does not save the bean.
 *
 * @author Jeroen van Schagen
 * @since Feb 14, 2014
 */
public class NoOperationBeanSaver implements BeanSaver {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T save(T bean) {
        return bean;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object bean) {
    }

}
