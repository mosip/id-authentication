/**
 * 
 */
package io.mosip.registrationprocessor.stages.demodedupe;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeProcessor;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeStage;
import io.vertx.core.Vertx;

/**
 * @author M1022006
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DemoDedupeStageTest {

	MessageDTO dto = new MessageDTO();

	@Mock
	private DemodedupeProcessor demodedupeProcessor;

	@InjectMocks
	private DemodedupeStage demodedupeStage = new DemodedupeStage() {
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

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		demodedupeStage.deployVerticle();
	}

	@Test
	public void testProcess() {
		MessageDTO result = new MessageDTO();
		result.setIsValid(true);
		Mockito.when(demodedupeProcessor.process(dto)).thenReturn(result);
		dto = demodedupeStage.process(dto);
		assertTrue(dto.getIsValid());
	}

}
