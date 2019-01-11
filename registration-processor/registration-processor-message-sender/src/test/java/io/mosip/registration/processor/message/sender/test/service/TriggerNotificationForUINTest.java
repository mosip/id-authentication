package io.mosip.registration.processor.message.sender.test.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.dto.config.GlobalConfig;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.trigger.notification.uin.TriggerNotificationForUIN;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class TriggerNotificationForUINTest {

	@InjectMocks
	private TriggerNotificationForUIN triggerNotificationForUIN;
	
	@Mock
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;
	
	@Mock
	private ObjectMapper mapIdentityJsonStringToObject;
	
	@Mock
	private GlobalConfig jsonObject;
	
	@Mock
	private MessageSenderUtil utility;
	
	private static final String CONFIG_SERVER_URL = "http://104.211.212.28:51000/registration-processor-message-sender/dev/DEV/";
	
	@Before
	public void setup() throws Exception{

		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmailSubject", "UIN Generated");
		String value = "\r\n" + 
				"{\r\n" + 
				"\r\n" + 
				"		\"archivalPolicy\":\"arc_policy_2\",\r\n" + 
				"\r\n" + 
				"		\"otpTimeOutInMinutes\":2,\r\n" + 
				"\r\n" + 
				"		\"numberOfWrongAttemptsForOtp\":5,\r\n" + 
				"\r\n" + 
				"		\"accountFreezeTimeoutInHours\":10, \r\n" + 
				"\r\n" + 
				"		\"uinLength\":24,\r\n" + 
				"\r\n" + 
				"		\"vidLength\":32,\r\n" + 
				"\r\n" + 
				"		\"pridLength\":32,\r\n" + 
				"\r\n" + 
				"		\"tokenIdLength\":23,\r\n" + 
				"\r\n" + 
				"		\"tspIdLength\":24,\r\n" + 
				"\r\n" + 
				"		\"registrationCenterId\":\"KDUE83CJ3\",\r\n" + 
				"\r\n" + 
				"		\"machineId\":\"MCBD3UI3\",\r\n" + 
				"\r\n" + 
				"		\"mobilenumberlength\":10,\r\n" + 
				"\r\n" + 
				"		\"restrictedNumbers\":[\r\n" + 
				"\r\n" + 
				"			\"8732\",\"321\",\"65\"\r\n" + 
				"\r\n" + 
				"		],\r\n" + 
				"\r\n" + 
				"		\"supportedLanguages\":[\r\n" + 
				"\r\n" + 
				"			\"eng\",\"ara\",\"deu\"\r\n" + 
				"\r\n" + 
				"		],\r\n" + 
				"\r\n" + 
				"		\"notificationtype\":\"SMS|EMAIL\" \r\n" + 
				"\r\n" + 
				"}";

		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value);
		Mockito.when(service.sendSmsNotification(anyString(), any(), any(), any())).thenReturn(null);
		Mockito.when(service.sendEmailNotification(anyString(), any(), any(), any(), any(), any(), any()))
				.thenReturn(null);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetGlobalConfigJson()).thenReturn("global-config.json");
	}
	
	@Test
	public void testTriggerNotificationSuccess() throws Exception {
		
		String uin = "123456789";		

		triggerNotificationForUIN.triggerNotification(uin);
	}

	@Test(expected = ConfigurationNotFoundException.class)
	public void testConfigNotFoundException() throws Exception {
		String value1="\r\n" + 
				"{\r\n" + 
				"\r\n" +
				"		\"notificationtype\":\"\" \r\n" + 
				"\r\n" + 
				"}";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);
		String uin = "123456789";

		triggerNotificationForUIN.triggerNotification(uin);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testTemplateGenerationFailedException() throws ApisResourceAccessException, IOException {
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotificationForUIN, "notificationEmailSubject", "UIN Generated");

		Mockito.when(service.sendSmsNotification(anyString(), any(), any(), any()))
				.thenThrow(new TemplateGenerationFailedException());
		
		String uin = "123456789";

		triggerNotificationForUIN.triggerNotification(uin);
	}

}
