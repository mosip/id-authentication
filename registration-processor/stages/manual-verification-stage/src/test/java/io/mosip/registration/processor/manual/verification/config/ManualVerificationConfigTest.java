package io.mosip.registration.processor.manual.verification.config;

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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ManualVerificationConfigTest {


	@InjectMocks
	ManualVerificationConfigBean config;

	private Environment env;

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
	public void getdemoDedupeStage() {
		assertNotNull(config.getManualVerificationStage());
	}
	
	@Test
	public void getdemoDedupeTest() {
		assertNotNull(config.getManualVerificationService());
	}

}
