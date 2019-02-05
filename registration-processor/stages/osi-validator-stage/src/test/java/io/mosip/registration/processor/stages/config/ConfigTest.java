package io.mosip.registration.processor.stages.config;

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



@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

	@InjectMocks
	OSIConfigBean config;

	private Environment env;

	@Before
	public void setUp() {

		System.setProperty("spring.cloud.config.uri", "http://104.211.212.28:51000");
		System.setProperty("spring.profiles.active", "dev");
		System.setProperty("spring.cloud.config.label", "DEV");
		System.setProperty("spring.application.name", "registration-processor-packet-uploader");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		env = context.getEnvironment();
	}

	@Test
	public void getPropertySourcesPlaceholderConfigurerTest() throws IOException {
		PropertySourcesPlaceholderConfigurer configurer = config.getPropertySourcesPlaceholderConfigurer(env);
		assertNotNull(configurer);
	}

	@Test
	public void getOSIValidatorTest() {
		assertNotNull(config.getOSIValidator());
	}
	
	@Test
	public void getUMCValidatorTest() {
		assertNotNull(config.getUMCValidator());
	}
	
	@Test
	public void getOSIValidatorStageTest() {
		assertNotNull(config.getOSIValidatorStage());
	}

}