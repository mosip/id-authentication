package io.mosip.registration.processor.abstractverticle;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MosipVerticleManagerConsumeTest {
	
	private MessageDTO messageDTO;
	private Vertx vertx;

	private ConsumerVerticle consumerVerticle;

	@Before
	public void setup(TestContext testContext) throws Exception {
		this.consumerVerticle = new ConsumerVerticle();

		this.messageDTO = new MessageDTO();
		this.messageDTO.setRid("1001");
		this.messageDTO.setRetryCount(0);
		this.messageDTO.setMessageBusAddress(MessageBusAddress.PACKET_VALIDATOR_BUS_IN);
		this.messageDTO.setIsValid(true);
		this.messageDTO.setInternalError(false);

		vertx = Vertx.vertx();
		vertx.deployVerticle(ConsumerVerticle.class.getName(), testContext.asyncAssertSuccess());

		// vertx=consumerVerticle.getEventBus(ConsumerVerticle.class).getEventbus();
	}

	@After
	public void tearDown(TestContext testContext) {
		vertx.close(testContext.asyncAssertSuccess());
	}

	@Test
	public void testMosipEventBus() {
		vertx.close();
		ConsumerVerticle consumerVerticle = new ConsumerVerticle();
		Vertx vertx= consumerVerticle.deployVerticle().getEventbus();
		assertTrue(vertx.isClustered());
	}
	
	@Test
	public void checkSend(TestContext testContext) {
		final Async async = testContext.async();

		vertx.eventBus().consumer(MessageBusAddress.DEMO_DEDUPE_BUS_IN.getAddress(), msg -> {
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getRid()));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getInternalError().toString()));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getIsValid().toString()));
			testContext.assertTrue(msg.body().toString().contains(Integer.toString(this.messageDTO.getRetryCount())));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getMessageBusAddress().getAddress()));
			if (!async.isCompleted())
				async.complete();
		});
	}

	@Test
	public void checkConsume(TestContext testContext) {
		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(this.messageDTO);

		vertx.eventBus().send(MessageBusAddress.PACKET_VALIDATOR_BUS_IN.getAddress(), jsonObject);
		async.complete();
		async.awaitSuccess();

	}

	@Test
	public void checkConsumeAndSend(TestContext testContext) {
		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(this.messageDTO);
		vertx.eventBus().send(MessageBusAddress.PACKET_VALIDATOR_BUS_OUT.getAddress(), jsonObject);
		vertx.eventBus().consumer(MessageBusAddress.RETRY_BUS.getAddress(), msg -> {
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getRid()));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getInternalError().toString()));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getIsValid().toString()));
			testContext.assertTrue(msg.body().toString().contains(Integer.toString(this.messageDTO.getRetryCount())));
			testContext.assertTrue(msg.body().toString().contains(this.messageDTO.getMessageBusAddress().getAddress()));
			if (!async.isCompleted())
				async.complete();
		});
	}
}
