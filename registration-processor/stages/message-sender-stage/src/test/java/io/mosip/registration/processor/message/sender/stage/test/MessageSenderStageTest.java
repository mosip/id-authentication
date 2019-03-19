package io.mosip.registration.processor.message.sender.stage.test;

import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.stage.MessageSenderStage;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;
import io.vertx.core.Vertx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class MessageSenderStageTest {

	@Mock
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	
	@Mock
	private TransactionService<TransactionDto> transcationStatusService;
	
	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@InjectMocks
	private MessageSenderStage stage = new MessageSenderStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
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

	@Before
	public void setup() throws Exception {
		System.setProperty("registration.processor.notification.emails", "alokranjan1106@gmail.com");
		System.setProperty("registration.processor.uin.generated.subject", "UIN Generated");
		System.setProperty("registration.processor.duplicate.uin.subject", "duplicate UIN");
		System.setProperty("registration.processor.reregister.subject", "Re-Register");
		ReflectionTestUtils.setField(stage, "notificationTypes", "SMS|EMAIL");

		Mockito.doNothing().when(registrationStatusDto).setStatusCode(any());
		Mockito.doNothing().when(registrationStatusDto).setStatusComment(any());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());
		Mockito.when(transcationStatusService.addRegistrationTransaction(any())).thenReturn(null);
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
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_UIN_GENERATION_SUCCESS.name());

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

		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_UIN_UPDATION_SUCCESS.name());

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

		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.MANUAL_ADJUDICATION_FAILED.name());

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

		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED.name());
		
		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test(expected = TemplateGenerationFailedException.class)
	public void testConfigNotFoundException() throws Exception {
		ReflectionTestUtils.setField(stage, "notificationTypes", "");
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_UIN_GENERATION_SUCCESS.name());

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}

	@Test
	public void testTemplateNotFound() throws ApisResourceAccessException {
		ReflectionTestUtils.setField(stage, "notificationTypes", "OTP");
		
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_UIN_GENERATION_SUCCESS.name());

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}
	
	@Test
	public void testException() throws ApisResourceAccessException {
		
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getStatusCode()).thenReturn(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());

		MessageDTO dto = new MessageDTO();
		dto.setRid("85425022110000120190117110505");
		stage.process(dto);
	}
	
}
