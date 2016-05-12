package io.dynamo.generator;

import org.mockito.Mockito;

public class MockitoBeanGenerator implements ValueGenerator {
	
	@Override
	public Object generate(Class<?> valueType) {
		Object mock = Mockito.mock(valueType);
		Mockito.verifyZeroInteractions(mock);
		return mock;
	}
	
}
