package io.mosip.registration.processor.stages.uigenerator.util.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
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
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.dto.config.GlobalConfig;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
import io.mosip.registration.processor.stages.uingenerator.util.NotificationTemplateType;
import io.mosip.registration.processor.stages.uingenerator.util.TriggerNotification;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class TriggerNotificationForUINTest {

	@InjectMocks
	private TriggerNotification triggerNotification;

	@Mock
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	@Mock
	private ObjectMapper mapIdentityJsonStringToObject;

	@Mock
	private GlobalConfig jsonObject;

	@Mock
	private MessageSenderUtil utility;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The list appender. */
	private ListAppender<ILoggingEvent> listAppender;

	/** The foo logger. */
	private Logger fooLogger;

	private static final String CONFIG_SERVER_URL = "http://104.211.212.28:51000/registration-processor-message-sender/dev/DEV/";

	SmsResponseDto smsResponseDto = new SmsResponseDto();

	ResponseDto responseDto = new ResponseDto();

	@Before
	public void setup() throws Exception {
		listAppender = new ListAppender<>();
		fooLogger = (Logger) LoggerFactory.getLogger(TriggerNotification.class);
		ReflectionTestUtils.setField(triggerNotification, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotification, "notificationEmailSubject", "UIN Generated");

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID2.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		String theString = IOUtils.toString(idJsonStream, StandardCharsets.UTF_8);
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(theString);
		smsResponseDto.setMessage("test");
		responseDto.setStatus("ok");
		Mockito.when(service.sendSmsNotification(anyString(), any(), any(), any())).thenReturn(null);
		Mockito.when(service.sendEmailNotification(anyString(), any(), any(), any(), any(), any(), any()))
				.thenReturn(null);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetGlobalConfigJson()).thenReturn("global-config.json");
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();

		TemplateDto templateDto = new TemplateDto();
		TemplateDto templateDto1 = new TemplateDto();

		templateDto.setTemplateTypeCode("RPR_UIN_GEN_SMS");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateDto1.setTemplateTypeCode("RPR_UIN_GEN_EMAIL");
		list.add(templateDto1);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);

	}

	@Test
	public void testTriggerNotificationSuccess() throws Exception {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.INFO, "SESSIONID - UIN - 123456789 - Sms sent Successfully"));
	}

	@Test
	public void testTriggerNotificationUpdateSuccess() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();

		TemplateDto templateDto = new TemplateDto();
		TemplateDto templateDto1 = new TemplateDto();

		templateDto.setTemplateTypeCode("SMS_TEMP_FOR_UIN_GEN");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateDto1.setTemplateTypeCode("EMAIL_TEMP_FOR_UIN_GEN");
		list.add(templateDto1);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);
		listAppender.start();
		fooLogger.addAppender(listAppender);

		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_UPDATE);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.INFO, "SESSIONID - UIN - 123456789 - Email sent Successfully"));

	}

	@Test
	public void testConfigNotFoundException() throws Exception {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		String value1 = "\r\n" + "{\r\n" + "\r\n" + "		\"notificationtype\":\"\" \r\n" + "\r\n" + "}";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);
		String uin = "123456789";
		Mockito.when(jsonObject.getNotificationtype()).thenReturn(null);

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.ERROR, "SESSIONID - UIN - 123456789 - RPR-TEM-003 --> RPR-TEM-003"));
	}

	@Test
	public void testTemplateGenerationFailedException() throws ApisResourceAccessException {

		listAppender.start();
		fooLogger.addAppender(listAppender);
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();
		TemplateDto templateDto = new TemplateDto();
		templateDto.setTemplateTypeCode("j");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);
		String uin = "123456789";
		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.ERROR,
						"SESSIONID - UIN - 123456789 - RPR-TEM-001 --> sms and email template not found"));
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void emailIdNotFoundExceptionCheck() throws Exception {

		String uin = "123456789";
		EmailIdNotFoundException exp = new EmailIdNotFoundException();
		Mockito.doThrow(exp).when(service).sendSmsNotification(anyString(), any(), any(), any());

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);

	}

}
