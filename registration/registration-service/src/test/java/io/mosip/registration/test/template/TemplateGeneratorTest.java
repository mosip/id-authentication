package io.mosip.registration.test.template;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.TemplateGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageIO.class })
public class TemplateGeneratorTest {
	TemplateManagerBuilderImpl template = new TemplateManagerBuilderImpl();

	@InjectMocks
	TemplateGenerator templateGenerator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	ApplicationContext applicationContext;
	
	@Mock
	QrCodeGenerator<QrVersion> qrCodeGenerator;

	ResourceBundle dummyResourceBundle = new ResourceBundle() {
		@Override
		protected Object handleGetObject(String key) {
			return "fake_translated_value";
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}
	};

	@Test
	public void generateTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException, QrcodeGenerationException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>();
		segmentedFingerprints.add(new FingerprintDetailsDTO());
		
		registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
				.forEach(fingerPrintDTO -> {
					fingerPrintDTO.setSegmentedFingerprints(segmentedFingerprints);
				});
		PowerMockito.mockStatic(ImageIO.class);
		BufferedImage image = null;
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_HANDS_IMAGE_PATH)))
						.thenReturn(image);
		when(applicationContext.getLocalLanguageProperty()).thenReturn(dummyResourceBundle);
		when(applicationContext.getApplicationLanguageBundle()).thenReturn(dummyResourceBundle);
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template);
		when(qrCodeGenerator.generateQrCode(Mockito.anyString(), Mockito.any())).thenReturn(new byte[1024]);
		assertNotNull(response.getSuccessResponseDTO());
	}

	@Test
	public void generateNotificationTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = templateGenerator.generateNotificationTemplate("sample text", registrationDTO, template);
		assertNotNull(writer);
	}

}
