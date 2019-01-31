package io.mosip.registration.processor.packet.receiver.stage;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextImpl;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

@RunWith(VertxUnitRunner.class)
public class PacketReceiverStageTest{

	private Vertx vertx;

	@InjectMocks
	PacketReceiverStage packetReceiverStage = new PacketReceiverStage() {

		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			return null;
		}

		@Override
		public void sendMessage(MessageDTO messageDTO) {

		}
	};
	
	@Before
	public void setup(TestContext testContext) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.packet.receiver.config",
				"io.mosip.registration.processor.packet.manager.config",
				"io.mosip.registration.processor.status.config", "io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();
		PacketReceiverStage packetReceiverStage = configApplicationContext.getBean(PacketReceiverStage.class);

		vertx = Vertx.vertx();
		vertx.deployVerticle(packetReceiverStage, testContext.asyncAssertSuccess());
	}

	@Test
	public void healthCheckTest(TestContext testContext) {
		Async async = testContext.async();
		vertx.createHttpClient().getNow(8081, "localhost", "/health", response -> {
			response.handler(responseBody -> {
				testContext.assertTrue(responseBody.toString().contains("Server is up and running"));
				async.complete();
			});
		});
	}

	@Test
	public void packetUploaderTest(TestContext testContext) {
		Async async = testContext.async();
		HttpClient client = vertx.createHttpClient();
		
		HttpClientRequest request = client.post(8081, "localhost",
				"/v0.1/registration-processor/packet-receiver/registrationpackets", response -> {
					async.complete();
					System.out.println("Some callback " + response.statusCode());
				});
		String body = "{'username':'www','password':'www'}";
		request.putHeader("content-length", "1000");
		request.putHeader("content-type", "application/x-www-form-urlencoded");
		request.write(body);
		request.end();
		async.complete();
	}

	@After
	public void tearDown(TestContext testContext) {
		vertx.close(testContext.asyncAssertSuccess());
	}
}
