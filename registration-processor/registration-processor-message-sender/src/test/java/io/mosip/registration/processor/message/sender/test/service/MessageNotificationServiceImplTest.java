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

import io.mosip.kernel.core.util.JsonUtils;
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
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * The Class MessageNotificationServiceImplTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class, JsonUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class MessageNotificationServiceImplTest {

	/** The message notification service impl. */
	@InjectMocks
	MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> messageNotificationServiceImpl = new MessageNotificationServiceImpl();

	/** The adapter. */
	@Mock
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The template generator. */
	@Mock
	private TemplateGenerator templateGenerator;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The utility. */
	@Mock
	private MessageSenderUtil utility;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The rest api client. */
	@Mock
	private RestApiClient restApiClient;

	/** The env. */
	@Mock
	private Environment env;

	/** The attributes. */
	private Map<String, Object> attributes = new HashMap<>();

	/** The sms response dto. */
	private SmsResponseDto smsResponseDto;

	/** The response dto. */
	private ResponseDto responseDto;

	/** The file. */
	MultipartFile file = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);

	/** The file two. */
	MultipartFile fileTwo = new MockMultipartFile("test.txt", "test.txt", null, new byte[1100]);

	/** The attachment. */
	MultipartFile[] attachment = { file, fileTwo };

	/** The mail to. */
	String[] mailTo = { "mosip.emailnotifier@gmail.com" };

	/** The mail cc. */
	String[] mailCc = { "mosip.emailcc@gmail.com" };

	/** The mail content. */
	String mailContent = "Test Content";

	/** The subject. */
	String subject = "test";

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setup() throws Exception {
		String RegId = "27847657360002520181208094056";

		List<String> RegIdList = new ArrayList<>();
		RegIdList.add(RegId);

		ReflectionTestUtils.setField(messageNotificationServiceImpl, "langCode", "eng");
		Mockito.when(env.getProperty(ApiName.EMAILNOTIFIER.name())).thenReturn("https://mosip.com");

		Mockito.when(packetInfoManager.getRegIdByUIN(Mockito.any())).thenReturn(RegIdList);

		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		String value = "{\r\n" + "	\"identity\": {\r\n" + "		\"name\": {\r\n"
				+ "			\"value\": \"fullName\",\r\n" + "			\"weight\": 20\r\n" + "		},\r\n"
				+ "		\"gender\": {\r\n" + "			\"value\": \"gender\",\r\n" + "			\"weight\": 20\r\n"
				+ "		},\r\n" + "		\"dob\": {\r\n" + "			\"value\": \"dateOfBirth\",\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"pheoniticName\": {\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"poa\": {\r\n"
				+ "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n" + "		\"poi\": {\r\n"
				+ "			\"value\" : \"proofOfIdentity\"\r\n" + "		},\r\n" + "		\"por\": {\r\n"
				+ "			\"value\" : \"proofOfRelationship\"\r\n" + "		},\r\n" + "		\"pob\": {\r\n"
				+ "			\"value\" : \"proofOfDateOfBirth\"\r\n" + "		},\r\n"
				+ "		\"individualBiometrics\": {\r\n" + "			\"value\" : \"individualBiometrics\"\r\n"
				+ "		},\r\n" + "		\"age\": {\r\n" + "			\"value\" : \"age\"\r\n" + "		},\r\n"
				+ "		\"addressLine1\": {\r\n" + "			\"value\" : \"addressLine1\"\r\n" + "		},\r\n"
				+ "		\"addressLine2\": {\r\n" + "			\"value\" : \"addressLine2\"\r\n" + "		},\r\n"
				+ "		\"addressLine3\": {\r\n" + "			\"value\" : \"addressLine3\"\r\n" + "		},\r\n"
				+ "		\"region\": {\r\n" + "			\"value\" : \"region\"\r\n" + "		},\r\n"
				+ "		\"province\": {\r\n" + "			\"value\" : \"province\"\r\n" + "		},\r\n"
				+ "		\"postalCode\": {\r\n" + "			\"value\" : \"postalCode\"\r\n" + "		},\r\n"
				+ "		\"phone\": {\r\n" + "			\"value\" : \"phone\"\r\n" + "		},\r\n"
				+ "		\"email\": {\r\n" + "			\"value\" : \"email\"\r\n" + "		},\r\n"
				+ "		\"localAdministrativeAuthority\": {\r\n"
				+ "			\"value\" : \"localAdministrativeAuthority\"\r\n" + "		},\r\n"
				+ "		\"idschemaversion\": {\r\n" + "			\"value\" : \"IDSchemaVersion\"\r\n" + "		},\r\n"
				+ "		\"cnienumber\": {\r\n" + "			\"value\" : \"CNIENumber\"\r\n" + "		},\r\n"
				+ "		\"city\": {\r\n" + "			\"value\" : \"city\"\r\n" + "		}\r\n" + "	}\r\n" + "} ";

		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		String artifact = "Hi Alok, Your UIN is generated";

		Mockito.when(templateGenerator.getTemplate(any(), any(), any())).thenReturn(artifact);
	}

	/**
	 * Test send sms notification success.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testSendSmsNotificationSuccess() throws ApisResourceAccessException, IOException {
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(smsResponseDto);

		SmsResponseDto resultResponse = messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345",
				IdType.UIN, attributes);
		assertEquals("Test for SMS Notification Success", "Success", resultResponse.getMessage());
	}

	/**
	 * Test send email notification success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSendEmailNotificationSuccess() throws Exception {
		responseDto = new ResponseDto();
		responseDto.setStatus("Success");

		Mockito.when(restApiClient.postApi(any(), any(), any())).thenReturn(responseDto);

		ResponseDto resultResponse = messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345",
				IdType.UIN, attributes, mailCc, subject, null);
		assertEquals("Test for Email Notification Success", "Success", resultResponse.getStatus());
	}

	/**
	 * Test phone number not found exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = PhoneNumberNotFoundException.class)
	public void testPhoneNumberNotFoundException() throws ApisResourceAccessException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("ID2.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345", IdType.UIN, attributes);

	}

	/**
	 * Test email ID not found exception.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = EmailIdNotFoundException.class)
	public void testEmailIDNotFoundException() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File demographicJsonFile = new File(classLoader.getResource("ID2.json").getFile());
		InputStream inputStream = new FileInputStream(demographicJsonFile);
		Mockito.when(adapter.getFile(any(), any())).thenReturn(inputStream);

		messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345", IdType.UIN, attributes,
				mailCc, subject, null);
	}

	/**
	 * Test template generation failed exception.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateGenerationFailedException() throws IOException, ApisResourceAccessException {
		Mockito.when(templateGenerator.getTemplate("RPR_UIN_GEN_SMS", attributes, "eng"))
				.thenThrow(new TemplateNotFoundException());

		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345", IdType.UIN, attributes);
	}

	/**
	 * Test template processing failure exception.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateProcessingFailureException() throws Exception {
		Mockito.when(templateGenerator.getTemplate("RPR_UIN_GEN_EMAIL", attributes, "eng"))
				.thenThrow(new TemplateNotFoundException());

		messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345", IdType.UIN, attributes,
				mailCc, subject, null);
	}

}
