package io.mosip.registration.processor.reprocessor.stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ReprocessingStageSchedularTest {
	Vertx vertx;
	@InjectMocks
	private ReprocessorStage reprocessorStage = new ReprocessorStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	@Before
	public void setup(TestContext context) throws Exception {
		ReflectionTestUtils.setField(reprocessorStage, "fetchSize", 2);
		ReflectionTestUtils.setField(reprocessorStage, "elapseTime", 21600);
		ReflectionTestUtils.setField(reprocessorStage, "reprocessCount", 3);

		vertx = Vertx.vertx();
	}

	@After
	public void after(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testSchedular(TestContext context) {
		final Async async = context.async();
		/*
		 * vertx.deployVerticle(ReprocessorStage.class.getName(),
		 * context.asyncAssertSuccess(deploymentID -> {
		 * 
		 * @Override public void handle(AsyncResult<String> asyncResult) { // Deployment
		 * is asynchronous and this this handler will be called when it's complete (or
		 * failed) assertTrue(asyncResult.succeeded()); } vertx.undeploy(deploymentID,
		 * context.asyncAssertSuccess()); }));
		 */

		vertx.createHttpClient().getNow(8080, "localhost", "/assets/index.html", response -> {
			context.assertEquals(response.statusCode(), 200);
			context.assertEquals(response.headers().get("content-type"), "text/html");
			response.bodyHandler(body -> {
				context.assertTrue(body.toString().contains("<title>My Whisky Collection</title>"));
				async.complete();
			});
		});

		vertx.deployVerticle("ceylon:herd.schedule.chime/0.2.0", ar -> {
			if (ar.succeeded()) { // context.fail();

			} else { // async.complete(); }
			}
		});

		reprocessorStage.deployVerticle();
		async.complete();
	}

}
