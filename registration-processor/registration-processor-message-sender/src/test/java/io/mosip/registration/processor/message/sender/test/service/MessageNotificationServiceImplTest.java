package io.mosip.registration.processor.message.sender.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.service.impl.MessageNotificationServiceImpl;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class MessageNotificationServiceImplTest {

	@InjectMocks
	MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> messageNotificationServiceImpl = new MessageNotificationServiceImpl();

	@Mock
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Mock
	private TemplateGenerator templateGenerator;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	private MessageSenderUtil utility;

	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	private RestApiClient restApiClient;

	@Mock
	private Environment env;

	private Map<String, Object> attributes = new HashMap<>();
	private SmsResponseDto smsResponseDto;
	private ResponseDto responseDto;

	MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
	MultipartFile fileTwo = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);
	MultipartFile[] attachment = { file, fileTwo };

	String[] mailTo = { "mosip.emailnotifier@gmail.com" };
	String[] mailCc = { "mosip.emailcc@gmail.com" };
	String mailContent = "Test Content";
	String subject = "test";

	@Before
	public void setup() throws Exception {
		String RegId = "27847657360002520181208094056";

		List<String> RegIdList = new ArrayList<>();
		RegIdList.add(RegId);

		ReflectionTestUtils.setField(messageNotificationServiceImpl, "langCode", "eng");
		Mockito.when(env.getProperty(ApiName.EMAILNOTIFIER.name())).thenReturn("https://mosip.com");

		Mockito.when(packetInfoManager.getRegIdByUIN(Mockito.any())).thenReturn(RegIdList);

		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("DemographicInfo.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		String value = "{\r\n" + "\"firstName\": \"firstName\",\r\n" + "\"phoneNumber\" : \"mobileNumber\",\r\n"
				+ "\"emailID\" : \"emailId\"\r\n" + "}";

		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		String artifact = "Hi Alok, Your UIN is generated";

		Mockito.when(templateGenerator.getTemplate(any(), any(), any())).thenReturn(artifact);
	}

	@Test
	public void testSendSmsNotificationSuccess() throws ApisResourceAccessException, IOException {
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(smsResponseDto);

		SmsResponseDto resultResponse = messageNotificationServiceImpl.sendSmsNotification("SMS_TEMP_FOR_UIN_GEN",
				"12345", IdType.UIN, attributes);
		assertEquals("Success", resultResponse.getMessage());
	}

	@Test
	public void testSendEmailNotificationSuccess() throws Exception {
		responseDto = new ResponseDto();
		responseDto.setStatus("Success");

		Mockito.when(restApiClient.postApi(any(), any(), any())).thenReturn(responseDto);

		ResponseDto resultResponse = messageNotificationServiceImpl.sendEmailNotification("EMAIL_TEMP_FOR_UIN_GEN",
				"12345", IdType.UIN, attributes, mailCc, subject, null);
		assertEquals("Success", resultResponse.getStatus());
	}

	@Test(expected = PhoneNumberNotFoundException.class)
	public void testPhoneNumberNotFoundException() throws ApisResourceAccessException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("DemographicInfo2.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		messageNotificationServiceImpl.sendSmsNotification("SMS_TEMP_FOR_UIN_GEN", "12345", IdType.UIN, attributes);

	}

	@Test(expected = EmailIdNotFoundException.class)
	public void testEmailIDNotFoundException() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("DemographicInfo2.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		messageNotificationServiceImpl.sendEmailNotification("EMAIL_TEMP_FOR_UIN_GEN", "12345", IdType.UIN, attributes,
				mailCc, subject, null);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateGenerationFailedException() throws IOException, ApisResourceAccessException {
		Mockito.when(templateGenerator.getTemplate("SMS_TEMP_FOR_UIN_GEN", attributes, "eng"))
				.thenThrow(new TemplateNotFoundException());

		messageNotificationServiceImpl.sendSmsNotification("SMS_TEMP_FOR_UIN_GEN", "12345", IdType.UIN, attributes);
	}

	@Test(expected = IdentityNotFoundException.class)
	public void identityNotFoundExceptionTest() throws ApisResourceAccessException, IOException {
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("test");

		messageNotificationServiceImpl.sendSmsNotification("SMS_TEMP_FOR_UIN_GEN", "12345", IdType.UIN, attributes);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateProcessingFailureException() throws Exception {
		Mockito.when(templateGenerator.getTemplate("EMAIL_TEMP_FOR_UIN_GEN", attributes, "eng"))
				.thenThrow(new TemplateNotFoundException());

		messageNotificationServiceImpl.sendEmailNotification("EMAIL_TEMP_FOR_UIN_GEN", "12345", IdType.UIN, attributes,
				mailCc, subject, null);
	}

}
