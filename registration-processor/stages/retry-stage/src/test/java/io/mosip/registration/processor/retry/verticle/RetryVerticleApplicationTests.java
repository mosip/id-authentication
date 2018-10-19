package io.mosip.registration.processor.retry.verticle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.retry.verticle.stages.RetryStage;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
@RunWith(VertxUnitRunner.class)
public class RetryVerticleApplicationTests {
	
	
	private Vertx vertx;
	
	private Integer limit=6;
	
	RetryStage retryStage =new RetryStage();
	
	private MessageDTO dto=new MessageDTO();
	
	@Before
	public void setup(TestContext context) {
		retryStage.deployVerticle();
		vertx=retryStage.getEventBus(RetryStage.class).getEventbus();
		dto.setRid("1001");
		dto.setRetryCount(null);
		dto.setIsValid(false);
		dto.setInternalError(true);
		dto.setMessageBusAddress( MessageBusAddress.STRUCTURE_BUS_IN);
		
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}
	@Test
	public void checkProcessRetry(TestContext testContext) {
		
		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.RETRY_BUS.getAddress(), jsonObject);
		
		async.complete();
	}
	@Test
	public void checkProcessError(TestContext testContext) {
		
		final Async async = testContext.async();
		dto.setRetryCount(limit);
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.RETRY_BUS.getAddress(), jsonObject);
		
		async.complete();
	}
	
}
