package io.mosip.authentication.common.service.integration;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.mosip.authentication.common.service.integration.dto.MailRequestDto;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.authentication.common.service.helper.RestHelper;
import reactor.core.publisher.Mono;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
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

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
	}

	@Test
	public void testInValidSendNotificationSMS() throws IdAuthenticationBusinessException, RestServiceException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.SMS);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);

		IDDataValidationException idDataValidationException = new IDDataValidationException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);

		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(idDataValidationException);

		notificationManager.sendSmsNotification("9750185759", "test");
			});
	}

	@Test
	public void testInValidSendNotificationEmail() throws IdAuthenticationBusinessException, RestServiceException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		IDDataValidationException e = new IDDataValidationException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		IDDataValidationException idDataValidationException = new IDDataValidationException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(idDataValidationException);
		notificationManager.sendEmailNotification("test@gmail.com", "test", "test");
			});
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
        Mockito.when(restHelper.requestAsync(Mockito.any(RestRequestDTO.class), Mockito.any()))
                .thenReturn(Mono.just("SMS Sent Successfully"));
		notificationManager.sendEmailNotification("abc@test.com", "test", "test");
	}

	@Test
	public void testInValidSendNotificationPhone() throws IdAuthenticationBusinessException, RestServiceException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.SMS);
        Mockito.when(restHelper.requestSync(Mockito.any(), Mockito.any()))
				.thenThrow(new RestServiceException(IdRepoErrorConstants.UNKNOWN_ERROR));
		notificationManager.sendSmsNotification("1234567890", "test");
			});
	}
}
