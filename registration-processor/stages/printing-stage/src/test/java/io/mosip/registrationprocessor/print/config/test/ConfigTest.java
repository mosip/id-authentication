package io.mosip.registrationprocessor.print.config.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import io.mosip.registration.processor.print.config.PrintStageConfig;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

	@InjectMocks
	private PrintStageConfig config;

	private Environment env;

	@SuppressWarnings("resource")
	@Before
	public void setUp() {

		System.setProperty("spring.cloud.config.uri", "http://104.211.212.28:51000");
		System.setProperty("spring.profiles.active", "int");
		System.setProperty("spring.cloud.config.label", "0.8.0");
		System.setProperty("spring.application.name", "registration-processor");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		env = context.getEnvironment();
	}

	@Test
	public void getPropertySourcesPlaceholderConfigurerTest() throws IOException {
		PropertySourcesPlaceholderConfigurer configurer = config.getPropertySourcesPlaceholderConfigurer(env);
		assertNotNull(configurer);
	}

	@Test
	public void testgetPrintStage() {
		assertNotNull(config.getPrintStage());
	}

	@Test
	public void testgetPrintGlobalExceptionHandler() {
		assertNotNull(config.getPrintGlobalExceptionHandler());
	}

}