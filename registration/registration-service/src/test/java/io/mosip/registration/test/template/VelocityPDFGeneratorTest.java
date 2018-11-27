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

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.VelocityPDFGenerator;

public class VelocityPDFGeneratorTest {

	@InjectMocks
	VelocityPDFGenerator velocityGenerator;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void generateTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = velocityGenerator.generateTemplate("sample text", registrationDTO);
		assertNotNull(writer);
	}
	
	@Test
	public void generateNotificationTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = velocityGenerator.generateNotificationTemplate("sample text", registrationDTO);
		assertNotNull(writer);
	}
	
}
