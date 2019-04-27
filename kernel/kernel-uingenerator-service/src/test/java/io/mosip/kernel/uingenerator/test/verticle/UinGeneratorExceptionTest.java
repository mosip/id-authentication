package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@Ignore
@RunWith(VertxUnitRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UinGeneratorExceptionTest {

	private static Vertx vertx;
	private static int port;
	private static AbstractApplicationContext context;

	@BeforeClass
	public static void setup(TestContext testContext) throws IOException {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		System.setProperty("mosip.kernel.uin.uins-to-generate", "0");
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		context = new AnnotationConfigApplicationContext(UinGeneratorConfiguration.class);
		vertx = Vertx.vertx();
		// Without UinGeneratorVerticle deployed
		Verticle[] verticles = { new UinGeneratorServerVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
	}

	@AfterClass
	public static void cleanup(TestContext testContext) {
		if (vertx != null && testContext != null)
			vertx.close(testContext.asyncAssertSuccess());
		if (context != null)
			context.close();
	}

	@Test
	public void getUinExceptionTest(TestContext context) {
		System.out.println("getUinExceptionTest execution...");
		ObjectMapper objectMapper = new ObjectMapper();
		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.get(port, "localhost", "/v1/uingenerator/uin").send(ar -> {
			ServiceError error = null;
			if (ar.succeeded()) {
				HttpResponse<Buffer> httpResponse = ar.result();
				System.out.println(httpResponse.bodyAsString());
				try {
					httpResponse.bodyAsJsonObject().getValue("errors");
					error = objectMapper.readValue(httpResponse.bodyAsJsonObject().getValue("errors").toString()
							.replace("[", "").replace("]", ""), ServiceError.class);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
				context.assertEquals(200, httpResponse.statusCode());
				context.assertEquals(error.getErrorCode(), UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode());
				client.close();
				async.complete();
			} else {
				System.out.println("Something went wrong " + ar.cause().getMessage());
			}
		});
	}
}