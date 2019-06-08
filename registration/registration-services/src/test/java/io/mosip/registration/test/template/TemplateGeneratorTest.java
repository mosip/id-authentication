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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.junit.Before;
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

import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.TemplateGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageIO.class, ApplicationContext.class, SessionContext.class })
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
	
	@Before
<<<<<<< HEAD
	public void initialize() {
		Map<String,Object> appMap = new HashMap<>();
=======
	public void initialize() throws Exception {
		registrationDTO = DataProvider.getPacketDTO();
		List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>();
		segmentedFingerprints.add(new FingerprintDetailsDTO());
		
		registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
				.forEach(fingerPrintDTO -> {
					fingerPrintDTO.setSegmentedFingerprints(segmentedFingerprints);
				});
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
		appMap.put(RegistrationConstants.DOC_DISABLE_FLAG, "Y");
		appMap.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, "Y");
		appMap.put(RegistrationConstants.IRIS_DISABLE_FLAG, "Y");
		appMap.put(RegistrationConstants.FACE_DISABLE_FLAG, "Y");
		appMap.put(RegistrationConstants.PRIMARY_LANGUAGE, "ara");
		appMap.put(RegistrationConstants.SECONDARY_LANGUAGE, "fra");
		ApplicationContext.getInstance().setApplicationMap(appMap);
<<<<<<< HEAD
=======
		templateGenerator.setGuidelines("My GuideLines");
		ApplicationContext.getInstance().loadResourceBundle();
		when(qrCodeGenerator.generateQrCode(Mockito.anyString(), Mockito.any())).thenReturn(new byte[1024]);
		BufferedImage image = null;
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_EYE_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_LEFT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_RIGHT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_PATH)))
						.thenReturn(image);
		
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		PowerMockito.when(SessionContext.userContext().getRegistrationCenterDetailDTO()).thenReturn(centerDetailDTO);
		
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(RegistrationConstants.IS_Child, false);
		PowerMockito.when(SessionContext.map()).thenReturn(map);
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	}
	
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
		ApplicationContext.getInstance().loadResourceBundle();
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>();
		segmentedFingerprints.add(new FingerprintDetailsDTO());
		
		registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
				.forEach(fingerPrintDTO -> {
					fingerPrintDTO.setSegmentedFingerprints(segmentedFingerprints);
				});
		PowerMockito.mockStatic(ImageIO.class);
		PowerMockito.mockStatic(ApplicationContext.class);
<<<<<<< HEAD
		BufferedImage image = null;
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_EYE_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_LEFT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_RIGHT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_PATH)))
						.thenReturn(image);
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);
		SessionContext.map().put(RegistrationConstants.IS_Child, false);

		when(qrCodeGenerator.generateQrCode(Mockito.anyString(), Mockito.any())).thenReturn(new byte[1024]);
=======
		SessionContext.map().put(RegistrationConstants.IS_Child, false);

		when(ApplicationContext.applicationLanguage()).thenReturn("eng");
		when(ApplicationContext.localLanguage()).thenReturn("ar");
		when(ApplicationContext.localLanguageProperty()).thenReturn(dummyResourceBundle);
		when(ApplicationContext.applicationLanguageBundle()).thenReturn(dummyResourceBundle);
		when(ApplicationContext.map()).thenReturn(appMap);
	
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template, RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE);
		assertNotNull(response.getSuccessResponseDTO());
	}
	
	@Test
	public void generatePreviewTemplateTest() throws IOException, URISyntaxException {
		PowerMockito.mockStatic(ApplicationContext.class);
		SessionContext.map().put(RegistrationConstants.IS_Child, false);
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
		
		when(ApplicationContext.applicationLanguage()).thenReturn("eng");
		when(ApplicationContext.localLanguage()).thenReturn("ar");
		when(ApplicationContext.localLanguageProperty()).thenReturn(dummyResourceBundle);
		when(ApplicationContext.applicationLanguageBundle()).thenReturn(dummyResourceBundle);
<<<<<<< HEAD
=======
		when(ApplicationContext.map()).thenReturn(appMap);
		
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template, RegistrationConstants.TEMPLATE_PREVIEW);
		assertNotNull(response.getSuccessResponseDTO());
	}
	
	@Test
	public void generateAckTemplateChildTest() throws IOException, URISyntaxException {
		PowerMockito.mockStatic(ApplicationContext.class);
		SessionContext.map().put(RegistrationConstants.IS_Child, true);
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082

		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template, RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE);
		assertNotNull(response.getSuccessResponseDTO());
	}
<<<<<<< HEAD
=======
	
	@Test
	public void generatePreviewTemplateChildTest() throws IOException, URISyntaxException {
		PowerMockito.mockStatic(ApplicationContext.class);
		SessionContext.map().put(RegistrationConstants.IS_Child, true);
		
		when(ApplicationContext.applicationLanguage()).thenReturn("eng");
		when(ApplicationContext.localLanguage()).thenReturn("ar");
		when(ApplicationContext.localLanguageProperty()).thenReturn(dummyResourceBundle);
		when(ApplicationContext.applicationLanguageBundle()).thenReturn(dummyResourceBundle);
		when(ApplicationContext.map()).thenReturn(appMap);
		
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template, RegistrationConstants.TEMPLATE_PREVIEW);
		assertNotNull(response.getSuccessResponseDTO());
	}
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082

	@Test
	public void generateNotificationTemplateTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		ApplicationContext.getInstance().loadResourceBundle();
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Writer writer = templateGenerator.generateNotificationTemplate("sample text", registrationDTO, template);
		assertNotNull(writer);
	}

}
