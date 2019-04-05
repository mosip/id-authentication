package io.mosip.registrationprocessor.externalStage.stage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;


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
	};
	@Test
	public void testDeployVerticle() {
		externalStage.deployVerticle();
	}
	
	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
	/** The dto. */
	MessageDTO dto = new MessageDTO();
	
	@Before
	public void setUp() throws Exception {
		dto.setInternalError(false);
		dto.setIsValid(true);
		dto.setRid("2758415120462");
		dto.setReg_type("NEW");
		dto.setRetryCount(5);
		dto.setMessageBusAddress(MessageBusAddress.EXTERNAL_STAGE_BUS_IN);
		
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
