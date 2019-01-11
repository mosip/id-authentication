package io.mosip.registration.processor.message.sender.test.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.trigger.notification.uin.TriggerNotificationForUIN;

@RunWith(MockitoJUnitRunner.class)
public class TriggerNotificationForUINTest {

	@Mock
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	@InjectMocks
	private TriggerNotificationForUIN triggerNotificationForUIN;

	@Test
	public void testTriggerNotificationSuccess() throws ApisResourceAccessException, IOException {
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationTypes", "SMS|EMAIL");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmailSubject", "UIN Generated");

		Mockito.when(service.sendSmsNotification(anyString(), any(), any(), any())).thenReturn(null);
		Mockito.when(service.sendEmailNotification(anyString(), any(), any(), any(), any(), any(), any()))
				.thenReturn(null);

		String uin = "123456789";

		triggerNotificationForUIN.triggerNotification(uin);
	}

	@Test(expected = ConfigurationNotFoundException.class)
	public void testConfigNotFoundException() throws ApisResourceAccessException, IOException {
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationTypes", "");
		String uin = "123456789";

		triggerNotificationForUIN.triggerNotification(uin);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateGenerationFailedException() throws ApisResourceAccessException, IOException {
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationTypes", "SMS|EMAIL");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmailSubject", "UIN Generated");

		Mockito.when(service.sendSmsNotification(anyString(), any(), any(), any()))
				.thenThrow(new TemplateGenerationFailedException());
		
		String uin = "123456789";

		triggerNotificationForUIN.triggerNotification(uin);
	}

}
