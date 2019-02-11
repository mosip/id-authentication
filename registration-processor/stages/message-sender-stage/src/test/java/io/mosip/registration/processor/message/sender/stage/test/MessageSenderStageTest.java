package io.mosip.registration.processor.message.sender.stage.test;

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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
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
import io.mosip.registration.processor.message.sender.stage.MessageSenderStage;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MessageSenderUtil.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class MessageSenderStageTest {

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

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@InjectMocks
	private MessageSenderStage stage = new MessageSenderStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	@Test
	public void testDeployVerticle() {
		stage.deployVerticle();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		System.setProperty("registration.processor.notification.emails", "alokranjan1106@gmail.com");
		System.setProperty("registration.processor.uin.generated.subject", "UIN Generated");
		System.setProperty("registration.processor.duplicate.uin.subject", "duplicate UIN");
		System.setProperty("registration.processor.reregister.subject", "Re-Register");

		String identityString = "{\r\n" + "	\"notificationtype\":\"SMS|EMAIL\"\r\n" + "}";

		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(identityString);

		Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(jsonObject);

	}

	@Test
	public void testMessageSentUINGenerated() throws Exception {
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
		registrationStatusDto.setStatusCode(RegistrationStatusCode.UIN_GENERATED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test
	public void testMessageSentUINUpdate() throws Exception {

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

		registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_UIN_UPDATION_SUCCESS.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test
	public void testMessageSentDuplicateUIN() throws Exception {
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

		registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test
	public void testMessageSentTechnicalIssue() throws Exception {
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

		registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testConfigNotFoundException() throws Exception {

		String value1 = "\r\n" + "{\r\n" + "\r\n" + "		\"notificationtype\":\"\" \r\n" + "\r\n" + "}";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);

		registrationStatusDto.setStatusCode(RegistrationStatusCode.UIN_GENERATED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
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

		registrationStatusDto.setStatusCode(RegistrationStatusCode.UIN_GENERATED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test
	public void testJsonParseException() throws Exception {

		String value1 = "value";
		PowerMockito.mockStatic(MessageSenderUtil.class);
		PowerMockito.when(MessageSenderUtil.class, "getJson", anyString(), anyString()).thenReturn(value1);

		registrationStatusDto.setStatusCode(RegistrationStatusCode.UIN_GENERATED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
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

		registrationStatusDto.setStatusCode(RegistrationStatusCode.UIN_GENERATED.name());
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);

		EmailIdNotFoundException exp = new EmailIdNotFoundException();
		Mockito.doThrow(exp).when(service).sendEmailNotification(anyString(), any(), any(), any(), any(), any(), any());

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

}
