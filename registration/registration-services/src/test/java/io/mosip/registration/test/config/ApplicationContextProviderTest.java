package io.mosip.registration.test.config;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.config.ApplicationContextProvider;

public class ApplicationContextProviderTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	ApplicationContextProvider applicationContextProvider;
	
	@Mock
	AnnotationConfigApplicationContext applicationContext;
	
	@Test
	public void refreshContextTest() {
		Mockito.doNothing().when(applicationContext).refresh();
		applicationContextProvider.refreshContext();
	}
}
