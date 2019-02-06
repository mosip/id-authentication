package io.mosip.registration.processor.message.sender.test.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

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
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
import io.mosip.registration.processor.message.sender.utility.NotificationTemplateType;
import io.mosip.registration.processor.message.sender.utility.TriggerNotification;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class TriggerNotificationTest {

	@InjectMocks
	private TriggerNotification triggerNotification;

	@Mock
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private GlobalConfig jsonObject;

	@Mock
	private MessageSenderUtil utility;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(triggerNotification, "notificationEmails", "alokranjan1106@gmail.com");
		ReflectionTestUtils.setField(triggerNotification, "uinGeneratedSubject", "UIN Generated");
		ReflectionTestUtils.setField(triggerNotification, "duplicateUinSubject", "duplicate UIN");
		ReflectionTestUtils.setField(triggerNotification, "reregisterSubject", "Re-Register");

		String identityString = "{\r\n" + "	\"notificationtype\":\"SMS|EMAIL\"\r\n" + "}";

		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(identityString);

		Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(jsonObject);

	}

	@Test
	public void testTriggerNotificationSuccess() throws Exception {
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

		String uin = "123456789";
		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
	}

	@Test
	public void testTriggerNotificationUpdateSuccess() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();

		TemplateDto templateDto = new TemplateDto();
		TemplateDto templateDto1 = new TemplateDto();

		templateDto.setTemplateTypeCode("RPR_UIN_UPD_SMS");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateDto1.setTemplateTypeCode("RPR_UIN_UPD_EMAIL");
		list.add(templateDto1);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);

		String uin = "123456789";
		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_UPDATE);

	}

	@Test
	public void testTriggerNotificationDuplicateUIN() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();

		TemplateDto templateDto = new TemplateDto();
		TemplateDto templateDto1 = new TemplateDto();

		templateDto.setTemplateTypeCode("RPR_DUP_UIN_SMS");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateDto1.setTemplateTypeCode("RPR_DUP_UIN_EMAIL");
		list.add(templateDto1);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);

		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.DUPLICATE_UIN);

	}

	@Test
	public void testTriggerNotificationTechnicalIssue() throws Exception {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();

		TemplateDto templateDto = new TemplateDto();
		TemplateDto templateDto1 = new TemplateDto();

		templateDto.setTemplateTypeCode("RPR_TEC_ISSUE_SMS");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);
		templateDto1.setTemplateTypeCode("RPR_TEC_ISSUE_EMAIL");
		list.add(templateDto1);
		templateResponseDto.setTemplates(list);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);

		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.TECHNICAL_ISSUE);

	}

	@Test
	public void testConfigNotFoundException() throws Exception {

		String value1 = "\r\n" + "{\r\n" + "\r\n" + "		\"notificationtype\":\"\" \r\n" + "\r\n" + "}";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);
		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
	}

	@Test
	public void testTemplateNotFound() throws ApisResourceAccessException {
		TemplateResponseDto templateResponseDto = new TemplateResponseDto();
		TemplateDto templateDto = new TemplateDto();
		templateDto.setTemplateTypeCode("RPR_TEC_ISSUE_SMS");
		List<TemplateDto> list = new ArrayList<TemplateDto>();
		list.add(templateDto);

		templateResponseDto.setTemplates(list);

		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(templateResponseDto);

		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
	}

	@Test
	public void testJsonParseException() throws Exception {

		String value1 = "value";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);
		String uin = "123456789";

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void emailIdNotFoundExceptionCheck() throws Exception {
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
		String uin = "123456789";
		EmailIdNotFoundException exp = new EmailIdNotFoundException();
		Mockito.doThrow(exp).when(service).sendEmailNotification(anyString(), any(), any(), any(), any(), any(), any());

		triggerNotification.triggerNotification(uin, NotificationTemplateType.UIN_CREATED);
	}

}
