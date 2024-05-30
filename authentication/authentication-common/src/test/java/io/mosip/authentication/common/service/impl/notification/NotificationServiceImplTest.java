package io.mosip.authentication.common.service.impl.notification;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.indauth.dto.SenderType;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
@Import(EnvUtil.class)
public class NotificationServiceImplTest {

	@InjectMocks
	AuditRequestFactory auditFactory;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@Autowired
	EnvUtil environment;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private NotificationServiceImpl notificationService;

	@Mock
	private IdTemplateManager idTemplateManager;

	@Mock
	private IdService<AutnTxn> idInfoService;

	@Mock
	private IdInfoHelper demoHelper;

	@Mock
	private NotificationManager notificationManager;
	@Mock
	private IdInfoFetcher idInfoFetcher;

	List<String> templateLanguages = new ArrayList<String>();

	@Before
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationService, "idInfoFetcher", idInfoFetcher);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(notificationService, "notificationManager", notificationManager);
		templateLanguages.add("eng");
		templateLanguages.add("ara");
	}

	@Test
	public void TestValidAuthSmsNotification()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		AuthResponseDTO authResponseDTO = new AuthResponseDTO();

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toString());

		// authRequestDTO.setReqTime(Instant.now().atOffset(offset)
		// .format(DateTimeFormatter.ofPattern(environment.getDateTimePattern())).toString());
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		CompletableFuture<Object> Supplier = CompletableFuture.completedFuture("Success");
		Mockito.when(restHelper.requestAsync(Mockito.any())).thenReturn(Supplier);
		String uin = "274390482564";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("mosip.auth.sms.template", "test");
		mockenv.setProperty("uin.masking.required", "true");
		mockenv.setProperty("uin.masking.charcount", "8");
		mockenv.setProperty("notification.date.format", "dd-MM-yyyy");
		mockenv.setProperty("notification.time.format", "HH:mm:ss");
		mockenv.setProperty("mosip.otp.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.subject.template", "test");
		mockenv.setProperty("mosip.otp.mail.content.template", "test");
		mockenv.setProperty("mosip.auth.mail.content.template", "test");
		mockenv.setProperty("mosip.primary-language", "fra");
		mockenv.setProperty("mosip.secondary-language", "ara");
		mockenv.setProperty("mosip.otp.sms.template", "test");
		mockenv.setProperty("mosip.notification.timezone", "GMT+05:30");
		ReflectionTestUtils.setField(environment, "env", mockenv);
		notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidAuthSmsNotification()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toString());
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		CompletableFuture<Object> Supplier = CompletableFuture.completedFuture("Success");
		Mockito.when(restHelper.requestAsync(Mockito.any())).thenReturn(Supplier);
		String uin = "4667732";
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn(" mosip ");
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		IdAuthenticationBusinessException idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(idAuthenticationBusinessException.getCause());
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.notificationtype", "email|sms");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("mosip.auth.sms.template", "test");
		mockenv.setProperty("notification.date.format", "dd-MM-yyyy");
		mockenv.setProperty("notification.time.format", "HH:mm:ss");
		mockenv.setProperty("mosip.otp.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.sms.template", "test");
		mockenv.setProperty("mosip.primary-language", "fra");
		mockenv.setProperty("mosip.secondary-language", "ara");
		mockenv.setProperty("mosip.notification.timezone", "GMT+05:30");
		notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, true);
	}

	@Test
	public void testSendOtpNotification()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IOException {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		String otp = "987654";
		String idvid = "123";
		String idvidType = "test";
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("key1", "value1");
		List<String> templateLanguages = Arrays.asList("eng", "ara", "fra");
		String notificationProperty = "test";
		LocalDateTime otpGenerationTime = LocalDateTime.now();

		otpRequestDto.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).toString());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(demoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");

		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		IdAuthenticationBusinessException idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(idAuthenticationBusinessException.getCause());
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.notificationtype", "email|sms");
		mockenv.setProperty("uin.masking.charcount", "8");
		mockenv.setProperty("mosip.auth.sms.template", "test");
		mockenv.setProperty("mosip.kernel.otp.expiry-time", "120");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		mockenv.setProperty("mosip.otp.mail.content.template", "test");
		mockenv.setProperty("mosip.otp.mail.subject.template", "test");
		mockenv.setProperty("mosip.otp.sms.template", "test");
		ReflectionTestUtils.setField(environment, "env", mockenv);
		notificationService.sendOTPNotification(idvid, idvidType, valueMap, templateLanguages, otp,
				notificationProperty, otpGenerationTime);
	}

	@Test
	public void testInvokeSmsTemplate() {
		Map<String, Object> values = new HashMap<>();
		String notificationMobileNo = "1234567890";
		ReflectionTestUtils.invokeMethod(notificationService, "invokeSmsNotification", values, SenderType.OTP,
				notificationMobileNo, templateLanguages);
	}

	@Test
	public void testInvokeSmsTemplateInvalid() {
		Map<String, Object> values = new HashMap<>();
		String notificationMobileNo = "1234567890";
		SenderType senderType = null;
		ReflectionTestUtils.invokeMethod(notificationService, "invokeSmsNotification", values, senderType,
				notificationMobileNo, templateLanguages);
	}

	@Test
	public void testInvokeEmailTemplateInvalid() {
		Map<String, Object> values = new HashMap<>();
		SenderType senderType = null;
		ReflectionTestUtils.invokeMethod(notificationService, "invokeEmailNotification", values, "abc@test.com",
				senderType, templateLanguages);
	}

	@Test
	public void testprocessNotification() {
		Set<NotificationType> notificationtype = new HashSet<>();
		ReflectionTestUtils.invokeMethod(notificationService, "processNotification", null, "12345657890",
				notificationtype, "email");
	}

	@Test
	public void testsendNotification() {
		Map<String, Object> values = new HashMap<>();
		values.put("uin", "123456677890");
		ReflectionTestUtils.invokeMethod(notificationService, "sendNotification", values, "abc@test.com", "1234567890",
				SenderType.OTP, "email", templateLanguages);
	}

	@Test
	public void testInvalidTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> values = new HashMap<>();
		values.put("uin", "123456677890");
		String contentTemplate = "test";
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(IOException.class);
		try {
			ReflectionTestUtils.invokeMethod(notificationService, "applyTemplate", values, contentTemplate,
					templateLanguages);
		} catch (UndeclaredThrowableException ex) {
			assertTrue(ex.getUndeclaredThrowable().getClass().equals(IdAuthenticationBusinessException.class));
		}
	}

	@Test
	public void sendOTPNotificationTest() throws IdAuthenticationBusinessException {
		String idvid = "123";
		String idvidType = "test";
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("key1", "value1");
		valueMap.put("key2", "value2");
		valueMap.put("key3", "value3");
		List<String> templateLanguages = Arrays.asList("eng", "ara", "fra");
		String otp = "1234";
		String notificationProperty = "test";
		LocalDateTime otpGenerationTime = LocalDateTime.now();
		notificationService.sendOTPNotification(idvid, idvidType, valueMap, templateLanguages, otp,
				notificationProperty, otpGenerationTime);
	}

}
