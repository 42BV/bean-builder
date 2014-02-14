package org.beanbuilder;

import org.beanbuilder.BeanBuilder.BeanBuildCommand;
import org.beanbuilder.domain.NestedBean;
import org.beanbuilder.domain.NestedBeanWithConstructor;
import org.beanbuilder.domain.SimpleBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BeanBuilderTest {

    private BeanBuilder beanBuilder;
	
	@Before
	public void setUp() {
        beanBuilder = new BeanBuilder();
	}
	
	@Test
	public void testGenerate() {
		SimpleBean bean = (SimpleBean) beanBuilder.generate(SimpleBean.class);
		Assert.assertNotNull(bean);
		Assert.assertNotNull(bean.getValue());
		
		NestedBean nestedBean = bean.getNestedBean();
		Assert.assertNotNull(nestedBean);
		Assert.assertNotNull(nestedBean.getValue());
		
		NestedBeanWithConstructor nestedBeanWithConstructor = bean.getNestedBeanWithConstructor();
		Assert.assertNotNull(nestedBeanWithConstructor);
		Assert.assertNotNull(nestedBeanWithConstructor.getValue());
	}
    
    @Test
    public void testGenerateWithCustomType() {
        SimpleBean bean = new SimpleBean();
        beanBuilder.registerValue(SimpleBean.class, bean);
        
        Assert.assertEquals(bean, beanBuilder.generate(SimpleBean.class));
    }

	@Test
    public void testGenerateWithCustomProperty() {
		NestedBeanWithConstructor nestedBeanWithConstructor = new NestedBeanWithConstructor("bla");
		beanBuilder.registerValue(SimpleBean.class, "nestedBeanWithConstructor", nestedBeanWithConstructor);

		SimpleBean bean = (SimpleBean) beanBuilder.generate(SimpleBean.class);
		Assert.assertEquals(nestedBeanWithConstructor, bean.getNestedBeanWithConstructor());
	}

    @Test
    public void testBuildWithDefaultBuilder() {
        SimpleBean bean = beanBuilder.newBean(SimpleBean.class)
                                        .withGeneratedValues()
                                        .withValue("value", "success")
                                            .build();
        
        Assert.assertEquals("success", bean.getValue());
        Assert.assertNotNull(bean.getNestedBean());
        Assert.assertNotNull(bean.getNestedBeanWithConstructor());
    }
    
    @Test
    public void testBuildWithCustomBuilder() {
        NestedBean nestedBean = new NestedBean();
        
        SimpleBean bean = beanBuilder.newBeanBy(SimpleBeanBuildCommand.class)
                                        .withValue("success")
                                        .withNestedBean(nestedBean)
                                            .build();
        
        Assert.assertEquals("success", bean.getValue());
        Assert.assertEquals(nestedBean, bean.getNestedBean());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBuildAndSaveUnsupported() {
        beanBuilder.newBean(SimpleBean.class).buildAndSave();
    }

    public interface SimpleBeanBuildCommand extends BeanBuildCommand<SimpleBean> {
        
        SimpleBeanBuildCommand withValue(String value);
        
        SimpleBeanBuildCommand withNestedBean(NestedBean nestedBean);

    }

}
