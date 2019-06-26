package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

@RunWith(MockitoJUnitRunner.class)
public class PacketValidatorStageTest {
	MessageDTO dto = new MessageDTO();
	private String stageName = "PacketValidatorStage";
	@Mock
	private PacketValidateProcessor packetvalidateprocessor;
	
	@Mock
	private MosipRouter router;
	@Mock
	MosipEventBus mosipEventBus;
	@InjectMocks
	private PacketValidatorStage packetValidatorStage = new PacketValidatorStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}
		
		@Override
		public void consumeAndSend(MosipEventBus eventbus, MessageBusAddress addressbus1,
				MessageBusAddress addressbus2) {
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
	public void testStart()
	{
		ReflectionTestUtils.setField(packetValidatorStage, "port", "2321");
		Mockito.doNothing().when(router).setRoute(any());
		packetValidatorStage.start();
	}

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		packetValidatorStage.deployVerticle();
	}

	@Test
	public void testProcess() {
		MessageDTO result = new MessageDTO();
		result.setIsValid(true);
		Mockito.when(packetvalidateprocessor.process(any(), any())).thenReturn(result);
		dto = packetValidatorStage.process(dto);
		assertTrue(dto.getIsValid());

	}
}
