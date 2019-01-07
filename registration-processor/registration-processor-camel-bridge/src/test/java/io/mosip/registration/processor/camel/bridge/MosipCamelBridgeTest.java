package io.mosip.registration.processor.camel.bridge;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.processor.camel.bridge.MosipBridgeFactory;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
@RunWith(VertxUnitRunner.class)
public class MosipCamelBridgeTest {
	private Vertx vertx;
	
	private MessageDTO dto=new MessageDTO();
	private MosipBridgeFactory mosipBridgeFactory=mock(MosipBridgeFactory.class);
	
	@Before
	public void setUp(TestContext testContext) throws Exception {
		vertx = Vertx.vertx();
		
		
		dto.setRid("1001");
		dto.setRetryCount(0);
		dto.setIsValid(false);
		dto.setInternalError(true);
		dto.setMessageBusAddress( MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
		
		vertx.deployVerticle(MosipBridgeFactory.class.getName(), testContext.asyncAssertSuccess());
		
	}
	
	@After
	public void tearDown(TestContext testContext) {
		vertx.close(testContext.asyncAssertSuccess());
	}
	@SuppressWarnings("static-access")
	@Test
	public void checkMosipBridgeFactory() throws InterruptedException, ExecutionException {
		verify(mosipBridgeFactory).getEventBus();
	}

	@Test
	public void checkBridge(TestContext testContext) {
		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.PACKET_VALIDATOR_BUS_IN.getAddress(), jsonObject);
		vertx.eventBus().send(MessageBusAddress.PACKET_VALIDATOR_BUS_OUT.getAddress(), jsonObject);
		vertx.eventBus().send(MessageBusAddress.BATCH_BUS.getAddress(), jsonObject);
		
		vertx.eventBus().send(MessageBusAddress.QUALITY_CHECK_BUS.getAddress(), jsonObject);
		
		vertx.eventBus().send(MessageBusAddress.ERROR.getAddress(), jsonObject);
		vertx.eventBus().send(MessageBusAddress.RETRY_BUS.getAddress(), jsonObject);
		
		
		async.complete();
	}
	
}
