package io.mosip.registration.processor.retry.verticle;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

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
	private Vertx revertx;

	private Integer limit = 6;

	RetryStage retryStage = new RetryStage();

	private MessageDTO dto = new MessageDTO();

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Before
	public void setup(TestContext context) {
		retryStage.deployVerticle();
		dto.setRid("1001");
		dto.setRetryCount(null);
		dto.setIsValid(false);
		dto.setInternalError(true);
		dto.setMessageBusAddress(MessageBusAddress.STRUCTURE_BUS_IN);

	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
		revertx.close(context.asyncAssertSuccess());

	}

	@Test
	@Ignore
	public void checkProcessRetry(TestContext testContext) {

		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.RETRY_BUS.getAddress(), jsonObject);
		revertx.eventBus().consumer(dto.getMessageBusAddress().getAddress(), msg -> {
			testContext.assertTrue(msg.body().toString().contains(dto.getRid()));
			if (!async.isCompleted())
				async.complete();
		});
		async.awaitSuccess();
	}

	@Test
	@Ignore
	public void checkProcessError(TestContext testContext) {

		final Async async = testContext.async();
		dto.setRetryCount(limit);
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.RETRY_BUS.getAddress(), jsonObject);
		revertx.eventBus().consumer(MessageBusAddress.ERROR.getAddress(), msg -> {
			testContext.assertTrue(msg.body().toString().contains(dto.getRid()));
			if (!async.isCompleted())
				async.complete();
		});
		async.awaitSuccess();
	}

}