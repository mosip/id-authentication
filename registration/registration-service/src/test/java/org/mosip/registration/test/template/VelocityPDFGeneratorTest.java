package org.mosip.registration.test.template;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.util.acktemplate.VelocityPDFGenerator;
import org.mosip.registration.util.dataprovider.DataProvider;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class VelocityPDFGeneratorTest {

	@InjectMocks
	VelocityPDFGenerator velocityGenerator;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void generateTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		File ackTemplate = new File(RegConstants.TEMPLATE_PATH);
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = velocityGenerator.generateTemplate(ackTemplate, registrationDTO);
		assertNotNull(writer);
	}
	
}
