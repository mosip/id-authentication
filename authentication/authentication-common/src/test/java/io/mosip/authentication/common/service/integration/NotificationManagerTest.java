package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.MailRequestDto;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OTPManagerTest.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class NotificationManagerTest {

	@Mock
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	AuditRequestFactory auditFactory;

	@Autowired
	EnvUtil environment;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private NotificationManager notificationManager;

	@Mock
	private IdTemplateManager idTemplateManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testInValidSendNotificationSMS() throws IdAuthenticationBusinessException, RestServiceException {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.SMS);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);

		IDDataValidationException idDataValidationException = new IDDataValidationException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);

		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(idDataValidationException);

		notificationManager.sendSmsNotification("9750185759", "test");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testInValidSendNotificationEmail() throws IdAuthenticationBusinessException, RestServiceException {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		IDDataValidationException idDataValidationException = new IDDataValidationException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(idDataValidationException);
		notificationManager.sendEmailNotification("test@gmail.com", "test", "test");
	}

	@Test
	public void TestInvalidTemplate() throws IdAuthenticationBusinessException, IOException {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		IDDataValidationException idDataValidationException = new IDDataValidationException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenThrow(idDataValidationException);
		notificationManager.sendSmsNotification("1234567890", "test");
	}

	@Test
	public void TestInvalidNotificationConfig() throws IdAuthenticationBusinessException {
		MailRequestDto mailRequestDto = new MailRequestDto();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.MAIL_NOTIFICATION_SERVICE, mailRequestDto,
				String.class))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.notification.type", "");
		CompletableFuture<Object> Supplier = CompletableFuture.completedFuture("Success");
		Mockito.when(restHelper.requestAsync(Mockito.any())).thenReturn(Supplier);
		notificationManager.sendEmailNotification("abc@test.com", "test", "test");
	}
}
