package io.mosip.registrationprocessor.externalstage.stage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;


@RunWith(SpringRunner.class)
public class ExternalStageTest {
	
	@InjectMocks
	private ExternalStage externalStage = new ExternalStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			 
			return null;
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
		@Override
		public Router postUrl(Vertx vertx, MessageBusAddress consumeAddress, MessageBusAddress sendAddress) {
			return null;
		}
		@Override
		public void createServer(Router router, int port) {

		}
	};
	@Test
	public void testDeployVerticle() {
		externalStage.deployVerticle();
	}
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private LogDescription description;
	
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
	@Mock
	private RegistrationExceptionMapperUtil registrationStatusMapperUtil;
	
	@Mock
	MosipRouter router;
	
	/** The dto. */
	MessageDTO dto = new MessageDTO();
	InternalRegistrationStatusDto registrationStatusDto=new InternalRegistrationStatusDto();
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(externalStage, "port","8989");
		dto.setInternalError(false);
		dto.setIsValid(true);
		dto.setRid("2758415120462");
		dto.setReg_type(RegistrationType.valueOf("NEW"));
		dto.setRetryCount(5);
		dto.setMessageBusAddress(MessageBusAddress.EXTERNAL_STAGE_BUS_IN);
		registrationStatusDto.setRegistrationId("2758415120462");
		registrationStatusDto.setRetryCount(0);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(router).setRoute(any());
	}
	@Test
	public void testStart()
	{
		externalStage.start();
	}
	
	@Test
	public void testisValidExternalSuccess() throws ApisResourceAccessException  {

		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenReturn(Boolean.TRUE);
		
		
		MessageDTO messageDto = externalStage.process(dto);

		assertTrue(messageDto.getIsValid());

	}
	@Test
	public void testisValidExternalFailure() throws ApisResourceAccessException  {

		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenReturn(Boolean.FALSE);
		
		MessageDTO messageDto = externalStage.process(dto);

		assertFalse(messageDto.getIsValid());

	}
	@SuppressWarnings("unchecked")
	@Test
	public void testisValidExternalDummyRequestFailure() throws ApisResourceAccessException  {

		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenThrow(ApisResourceAccessException.class);
		
		
		MessageDTO messageDto = externalStage.process(dto);

		assertFalse(messageDto.getIsValid());

	}
}
