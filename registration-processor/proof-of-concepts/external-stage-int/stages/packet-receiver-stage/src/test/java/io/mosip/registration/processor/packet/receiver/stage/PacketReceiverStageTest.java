package io.mosip.registration.processor.packet.receiver.stage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;

@RunWith(SpringRunner.class)
public class PacketReceiverStageTest {

	@InjectMocks
	private PacketReceiverStage packetReceiverStage = new PacketReceiverStage() {

		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			return null;
		}

		@Override
		public void sendMessage(MessageDTO messageDTO) {

		}
	};

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		packetReceiverStage.deployStage();
	}
}
