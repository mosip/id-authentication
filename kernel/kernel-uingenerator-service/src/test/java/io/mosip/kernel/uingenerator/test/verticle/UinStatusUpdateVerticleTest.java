package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinStatusUpdateVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UinStatusUpdateVerticleTest {

	private Vertx vertx;
	private int port;

	@Before
	public void before(TestContext testContext) throws IOException {
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

		ApplicationContext context = new AnnotationConfigApplicationContext(UinGeneratorTestConfiguration.class);
		vertx = Vertx.vertx();
		Verticle[] verticles = { new UinStatusUpdateVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
	}

	@After
	public void after(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}


	
	@Test
	public void checkThatWeCanAdd(TestContext context) {
	  Async async = context.async();
	  final String json = Json.encodePrettily(new UinEntity("7693463571", "ASSIGNED"));
	  final String length = Integer.toString(json.length());
	  vertx.createHttpClient().put(port, "localhost", "/updateuinstatus/7693463571")
	      .putHeader("content-type", "application/json")
	      .putHeader("content-length", length)
	      .handler(response -> {
	        context.assertEquals(response.statusCode(), 200);
	        context.assertTrue(response.headers().get("content-type").contains("application/json"));
	        response.bodyHandler(body -> {
	          final UinEntity uinEntity = Json.decodeValue(body.toString(), UinEntity.class);
	          context.assertEquals(uinEntity.getUin(), "7693463571");
	          context.assertEquals(uinEntity.getStatus(), "ASSIGNED");
	          async.complete();
	        });
	      })
	      .write(json)
	      .end();
	}
}
