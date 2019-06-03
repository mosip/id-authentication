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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.ResponseDTO;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.IDRepoResponseNull;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.service.impl.MessageNotificationServiceImpl;
import io.mosip.registration.processor.message.sender.template.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;
import io.mosip.registration.processor.status.code.RegistrationType;

/**
 * The Class MessageNotificationServiceImplTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, JsonUtils.class, IOUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class MessageNotificationServiceImplTest {

	/** The message notification service impl. */
	@InjectMocks
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> messageNotificationServiceImpl = new MessageNotificationServiceImpl();

	/** The adapter. */
	@Mock
	private FileSystemAdapter adapter;

	/** The template generator. */
	@Mock
	private TemplateGenerator templateGenerator;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The utility. */
	@Mock
	private Utilities utility;

	@Mock
	private ABISHandlerUtil abisHandlerUtil;

	/** The rest client service. */
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The rest api client. */
	@Mock
	private RestApiClient restApiClient;

	/** The env. */
	@Mock
	private Environment env;

	@Mock
	private ObjectMapper mapper;

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

	/** The id response. */
	private IdResponseDTO idResponse = new IdResponseDTO();

	/** The response. */
	private ResponseDTO response = new ResponseDTO();

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(messageNotificationServiceImpl, "langCode", "eng");
		Mockito.when(env.getProperty(ApiName.EMAILNOTIFIER.name())).thenReturn("https://mosip.com");

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

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);
		Map<String, Long> map1 = new HashMap<>();
		Long value1 = (long) 423072;
		map1.put("UIN", value1);
		JSONObject jsonObject1 = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject1);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		InputStream in = IOUtils.toInputStream("Hi Alok, Your UIN is generated", "UTF-8");
		Mockito.when(templateGenerator.getTemplate(any(), any(), any())).thenReturn(in);

		LinkedHashMap<String, Object> identityMap = new LinkedHashMap<>();
		Map<String, String> map = new HashMap<>();
		map.put("language", "eng");
		map.put("value", "Alok");
		JSONObject j1 = new JSONObject(map);

		Map<String, String> map2 = new HashMap<>();
		map2.put("language", "ara");
		map2.put("value", "Alok");
		JSONObject j2 = new JSONObject(map2);
		JSONArray array = new JSONArray();
		array.add(j1);
		array.add(j2);
		identityMap.put("fullName", array);
		identityMap.put("gender", array);
		identityMap.put("addressLine1", array);
		identityMap.put("addressLine2", array);
		identityMap.put("addressLine3", array);
		identityMap.put("city", array);
		identityMap.put("province", array);
		identityMap.put("region", array);
		identityMap.put("dateOfBirth", "1980/11/14");
		identityMap.put("phone", "9967878787");
		identityMap.put("email", "raghavdce@gmail.com");
		identityMap.put("postalCode", "900900");
		identityMap.put("proofOfAddress", j2);

		Object identity = identityMap;
		response.setIdentity(identity);

		idResponse.setResponse(response);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponse);

		Mockito.when(env.getProperty("mosip.registration.processor.sms.id")).thenReturn("id");
		Mockito.when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("v1.0");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

	/**
	 * Test send sms notification success.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSendSmsNotificationSuccess() throws ApisResourceAccessException, IOException {
		ResponseWrapper<SmsResponseDto> wrapper = new ResponseWrapper<>();
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");
		wrapper.setResponse(smsResponseDto);
		wrapper.setErrors(null);

		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(wrapper);
		Mockito.when(mapper.writeValueAsString(any())).thenReturn(smsResponseDto.toString());
		Mockito.when(mapper.readValue(anyString(), any(Class.class))).thenReturn(smsResponseDto);

		SmsResponseDto resultResponse = messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345",
				IdType.RID, attributes, RegistrationType.NEW.name());
		assertEquals("Test for SMS Notification Success", "Success", resultResponse.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUINTypeMessage() throws ApisResourceAccessException, IOException {
		ResponseWrapper<SmsResponseDto> wrapper = new ResponseWrapper<>();
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");

		String uin = "1234567";
		List<String> uinList = new ArrayList<>();
		uinList.add(uin);
		wrapper.setResponse(smsResponseDto);
		wrapper.setErrors(null);

		// Mockito.when(abisHandlerUtil.getUinFromIDRepo(any())).thenReturn(1234567);
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(wrapper);
		Mockito.when(mapper.writeValueAsString(any())).thenReturn(smsResponseDto.toString());
		Mockito.when(mapper.readValue(anyString(), any(Class.class))).thenReturn(smsResponseDto);
		SmsResponseDto resultResponse = messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS",
				"27847657360002520181208094056", IdType.UIN, attributes, RegistrationType.ACTIVATED.name());

		assertEquals("Test for SMS Notification Success", "Success", resultResponse.getMessage());
	}

	/**
	 * Test send email notification success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSendEmailNotificationSuccess() throws Exception {
		ResponseWrapper<ResponseDto> wrapper = new ResponseWrapper<>();
		responseDto = new ResponseDto();
		responseDto.setStatus("Success");
		wrapper.setErrors(null);
		wrapper.setResponse(responseDto);

		Mockito.when(restApiClient.postApi(any(), any(), any(), any())).thenReturn(wrapper);
		Mockito.when(mapper.writeValueAsString(any())).thenReturn(responseDto.toString());
		Mockito.when(mapper.readValue(anyString(), any(Class.class))).thenReturn(responseDto);

		ResponseDto resultResponse = messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345",
				IdType.RID, attributes, mailCc, subject, null, RegistrationType.NEW.name());
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

		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345", IdType.RID, attributes,
				RegistrationType.NEW.name());

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

		messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345", IdType.RID, attributes,
				mailCc, subject, null, RegistrationType.NEW.name());
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

		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "12345", IdType.RID, attributes,
				RegistrationType.NEW.name());
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

		messageNotificationServiceImpl.sendEmailNotification("RPR_UIN_GEN_EMAIL", "12345", IdType.RID, attributes,
				mailCc, subject, null, RegistrationType.NEW.name());
	}

	@Test(expected = IDRepoResponseNull.class)
	@Ignore
	public void testIDResponseNull() throws Exception {
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");

		String uin = "1234567";
		List<String> uinList = new ArrayList<>();
		uinList.add(uin);

		// Mockito.when(abisHandlerUtil.getUinFromIDRepo(any())).thenReturn(1234567);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(null);

		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "27847657360002520181208094056",
				IdType.UIN, attributes, RegistrationType.DEACTIVATED.name());
	}

	@Test(expected = IDRepoResponseNull.class)
	@Ignore
	public void testApisResourceAccessException() throws Exception {
		smsResponseDto = new SmsResponseDto();
		smsResponseDto.setMessage("Success");

		String uin = "1234567";
		List<String> uinList = new ArrayList<>();
		uinList.add(uin);
		// Mockito.when(abisHandlerUtil.getUinFromIDRepo(any())).thenReturn(1234567);
		ApisResourceAccessException exp = new ApisResourceAccessException("Error Message");
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any(Class.class))).thenThrow(exp);
		messageNotificationServiceImpl.sendSmsNotification("RPR_UIN_GEN_SMS", "27847657360002520181208094056",
				IdType.UIN, attributes, RegistrationType.DEACTIVATED.name());
	}

}
