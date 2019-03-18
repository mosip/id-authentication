package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

//@RunWith(VertxUnitRunner.class)
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
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
	}

	@After
	public void after(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	//@Test
	public void updateVerticle(TestContext context) {
		Async async = context.async();

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		UinResponseDto uinResp = restTemplate.getForObject("http://localhost:" + port + "/uingenerator/v1.0/uin",
				UinResponseDto.class);

		System.out.println(uinResp.getUin());

		final String json = Json.encodePrettily(new UinEntity(uinResp.getUin(), "ASSIGNED"));
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().put(port, "localhost", "/uingenerator/v1.0/uin")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					response.bodyHandler(body -> {
						final UinEntity UinEntity = Json.decodeValue(body.toString(), UinEntity.class);
						context.assertEquals(UinEntity.getUin(), uinResp.getUin());
						context.assertEquals(UinEntity.getStatus(), "ASSIGNED");
						async.complete();
					});
				}).write(json).end();

	}

}
