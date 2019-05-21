/**
 * 
 */
package io.mosip.registration.processor.biodedupe.stage;

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

/**
 * @author M1022006
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BioDedupeStageTest {

	MessageDTO dto = new MessageDTO();

	@Mock
	private BioDedupeProcessor bioDedupeProcessor;

	private String stageName = "BioDedupeStage";

	@InjectMocks
	private BioDedupeStage bioDedupeStage = new BioDedupeStage() {
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

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		bioDedupeStage.deployVerticle();
	}

	@Test
	public void testProcess() {
		MessageDTO result = new MessageDTO();
		result.setIsValid(true);
		Mockito.when(bioDedupeProcessor.process(any(), any())).thenReturn(result);
		dto = bioDedupeStage.process(dto);
		assertTrue(dto.getIsValid());
	}

}
