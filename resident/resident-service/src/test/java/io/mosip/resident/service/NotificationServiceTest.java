package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.NotificationTemplateCode;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.TemplateDto;
import io.mosip.resident.dto.TemplateResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.NotificationService;
import io.mosip.resident.util.JsonUtil;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import io.mosip.resident.util.Utilitiy;
import io.mosip.resident.validator.RequestValidator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class })
public class NotificationServiceTest {
	@InjectMocks
	private NotificationService notificationService;

	@Mock
	private Utilitiy utility;
	@Mock
	private Environment env;

	@Mock
	private ResidentServiceRestClient restClient;

	@Mock
	private TokenGenerator tokenGenerator;

	@Mock
	private TemplateManager templateManager;

	@Mock
	private RequestValidator requestValidator;
	private Map<String, Object> mailingAttributes;
	private NotificationRequestDto reqDto;
	private ResponseWrapper<NotificationResponseDTO> smsNotificationResponse;

	private static final String SMS_EMAIL_SUCCESS = "Notification has been sent to the provided contact detail(s)";
	private static final String SMS_SUCCESS = "Notification has been sent to the provided contact phone number";
	private static final String EMAIL_SUCCESS = "Notification has been sent to the provided email ";

	@Before
	public void setUp() throws ResidentServiceCheckedException, IOException, ApisResourceAccessException {
		Map<String, Object> additionalAttributes = new HashMap<>();
		additionalAttributes.put("RID", "10008200070004420191203104356");
		mailingAttributes = new HashMap<String, Object>();
		mailingAttributes.put("fullName_eng", "Test");
		mailingAttributes.put("fullName_ara", "Test");
		mailingAttributes.put("phone", "9876543210");
		mailingAttributes.put("email", "test@test.com");
		Mockito.when(utility.getMailingAttributes(Mockito.any(), Mockito.any())).thenReturn(mailingAttributes);
		ReflectionTestUtils.setField(notificationService, "languageType", "BOTH");
		ReflectionTestUtils.setField(notificationService, "primaryLang", "eng");
		ReflectionTestUtils.setField(notificationService, "secondaryLang", "ara");
		ReflectionTestUtils.setField(notificationService, "notificationEmails", "test@test.com|test1@test1.com");
		Mockito.when(tokenGenerator.getToken()).thenReturn("sbfdsafuadfkbdsf");
		Mockito.when(env.getProperty(ApiName.EMAILNOTIFIER.name())).thenReturn("https://int.mosip.io/template/email");
		Mockito.when(requestValidator.emailValidator(Mockito.anyString())).thenReturn(true);
		Mockito.when(requestValidator.phoneValidator(Mockito.anyString())).thenReturn(true);
		reqDto = new NotificationRequestDto();
		reqDto.setId("3527812406");
		reqDto.setIdType(IdType.UIN);
		reqDto.setTemplateTypeCode(NotificationTemplateCode.RS_UIN_RPR_SUCCESS);
		reqDto.setAdditionalAttributes(additionalAttributes);
		ResponseWrapper<TemplateResponseDto> primaryLangResp = new ResponseWrapper<>();
		TemplateResponseDto primaryTemplateResp = new TemplateResponseDto();
		TemplateDto primaryTemplateDto = new TemplateDto();
		primaryTemplateDto.setDescription("re print uin");
		primaryTemplateDto.setFileText(
				"Hi $name_eng,Your request for \"Reprint Of UIN\" has been successfully placed. Your RID (Req Number) is $RID.");
		List<TemplateDto> primaryTemplateList = new ArrayList<>();
		primaryTemplateList.add(primaryTemplateDto);
		primaryTemplateResp.setTemplates(primaryTemplateList);
		primaryLangResp.setResponse(primaryTemplateResp);
		Mockito.when(restClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenReturn(primaryLangResp);

		String primaryTemplatetext = "Hi Test,Your request for \"Reprint Of UIN\" has been successfully placed. Your RID (Req Number) is 10008200070004420191203104356.";
		InputStream primaryIs = new ByteArrayInputStream(primaryTemplatetext.getBytes(StandardCharsets.UTF_8));
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenReturn(primaryIs);
		smsNotificationResponse = new ResponseWrapper<>();
		NotificationResponseDTO notificationResp = new NotificationResponseDTO();
		notificationResp.setMessage("Notification has been sent to provided contact details");
		notificationResp.setStatus("success");
		smsNotificationResponse.setResponse(notificationResp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenReturn(smsNotificationResponse);

	}

	@Test
	public void sendNotificationTest()
			throws ApisResourceAccessException, ResidentServiceCheckedException, IOException {
		NotificationResponseDTO response = notificationService.sendNotification(reqDto);
		assertEquals(SMS_EMAIL_SUCCESS, response.getMessage());

	}

	@Test
	public void smsFailedAndEmailSuccessTest() throws ResidentServiceCheckedException {
		Mockito.when(requestValidator.phoneValidator(Mockito.anyString())).thenReturn(false);
		NotificationResponseDTO response = notificationService.sendNotification(reqDto);
		assertEquals(EMAIL_SUCCESS, response.getMessage());

	}

	@Test
	public void emailFailedAndSMSSuccessTest() throws ResidentServiceCheckedException {
		Mockito.when(requestValidator.emailValidator(Mockito.anyString())).thenReturn(false);
		NotificationResponseDTO response = notificationService.sendNotification(reqDto);
		assertEquals(SMS_SUCCESS, response.getMessage());

	}

	@Test(expected = ResidentServiceException.class)
	public void testNotificationFailure() throws Exception {
		ResponseWrapper<NotificationResponseDTO> smsNotificationResponse = new ResponseWrapper<>();
		NotificationResponseDTO notificationResp = new NotificationResponseDTO();
		notificationResp.setMessage("Notification failure");
		notificationResp.setStatus("failed");
		smsNotificationResponse.setResponse(notificationResp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenReturn(smsNotificationResponse);

		notificationService.sendNotification(reqDto);

	}

	@Test(expected = ResidentServiceException.class)
	public void getTemplateNullResponseTest() throws ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(restClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenReturn(null);
		notificationService.sendNotification(reqDto);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void tokenGenerationFailedException() throws IOException, ResidentServiceCheckedException {
		Mockito.when(tokenGenerator.getToken()).thenThrow(new IOException());
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void testApiResourceClientErrorException()
			throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpClientErrorException clientExp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", clientExp);
		Mockito.when(restClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void testApiResourceServerException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpServerErrorException serverExp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", serverExp);
		Mockito.when(restClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void testApiResourceUnknownException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		RuntimeException runTimeExp = new RuntimeException();
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("runtime exp", runTimeExp);
		Mockito.when(restClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void templateMergeIOException() throws IOException, ResidentServiceCheckedException {
		Mockito.when(templateManager.merge(Mockito.any(), Mockito.any())).thenThrow(new IOException());
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendSMSClientException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpClientErrorException clientExp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", clientExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendSMSServerException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpServerErrorException serverExp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", serverExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendSMSUnknownException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		RuntimeException runTimeExp = new RuntimeException();
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("runtime exp", runTimeExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
//		JsonUtil.objectMapperReadValue(JsonUtil.objectMapperObjectToJson(resp.getResponse()),
//				TemplateResponseDto.class);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendEmailClientException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpClientErrorException clientExp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", clientExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenReturn(smsNotificationResponse).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendEmailServerException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpServerErrorException serverExp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", serverExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenReturn(smsNotificationResponse).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void sendEmailUnknownException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		RuntimeException runTimeExp = new RuntimeException();
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("runtime exp", runTimeExp);
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(Class.class),
				Mockito.any())).thenReturn(smsNotificationResponse).thenThrow(apiResourceAccessExp);
		notificationService.sendNotification(reqDto);
	}

}
