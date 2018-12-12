package io.mosip.registration.test.template;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.TemplateGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageIO.class })
public class TemplateGeneratorTest {
	TemplateManagerBuilderImpl template =  new TemplateManagerBuilderImpl();
	
	@InjectMocks
	TemplateGenerator templateGenerator;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void generateTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		PowerMockito.mockStatic(ImageIO.class);
		BufferedImage image = null;
		when(ImageIO.read(templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_HANDS_IMAGE_PATH))).thenReturn(image);
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
