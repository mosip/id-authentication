package io.mosip.registration.test.template;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.TemplateGenerator;

public class TemplateGeneratorTest {
	TemplateManagerBuilderImpl template =  new TemplateManagerBuilderImpl();
	
	@InjectMocks
	TemplateGenerator templateGenerator;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void generateTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = templateGenerator.generateTemplate("sample text", registrationDTO, template);
		assertNotNull(writer);
	}
	
	@Test
	public void generateNotificationTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = templateGenerator.generateNotificationTemplate("sample text", registrationDTO, template);
		assertNotNull(writer);
	}
	
}
