package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.vertx.core.Vertx;

@RunWith(MockitoJUnitRunner.class)
public class PacketValidatorStageTest {
	MessageDTO dto = new MessageDTO();
	private String stageName = "PacketValidatorStage";
	@Mock
	private PacketValidateProcessor packetvalidateprocessor;
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
	};

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
