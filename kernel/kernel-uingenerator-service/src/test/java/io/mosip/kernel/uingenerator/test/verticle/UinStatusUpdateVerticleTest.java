package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.impl.UinGeneratorServiceImpl;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.mosip.kernel.uingenerator.verticle.UinStatusUpdateVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UinStatusUpdateVerticleTest {
	UinEntity givenUin = new UinEntity();

	private Vertx vertx;
	private int port;

	// @MockBean private UinRepository uinRepository;

	// @MockBean private UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@Autowired
	UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@Autowired
	UinRepository uinRepository;

	@Before
	public void before(TestContext testContext) throws IOException {
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

		ApplicationContext context = new AnnotationConfigApplicationContext(UinGeneratorTestConfiguration.class);
		vertx = Vertx.vertx();
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context),
				new UinStatusUpdateVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
		
	}

	/*
	 * @After public void after(TestContext context) {
	 * vertx.close(context.asyncAssertSuccess()); }
	 */

	/*
	 * @Test public void getUinTest(TestContext context) { Async async =
	 * context.async(); WebClient client = WebClient.create(vertx); client.get(port,
	 * "localhost", "/uin").send(ar -> { if (ar.succeeded()) { HttpResponse<Buffer>
	 * response = ar.result(); String uin = response.bodyAsString(); givenUin =
	 * response.bodyAsJson(UinEntity.class); givenUin.setStatus("ASSIGNED");
	 * 
	 * System.out.println("------" + uin); context.assertEquals(200,
	 * response.statusCode()); client.close(); async.complete(); } else {
	 * System.out.println("Something went wrong " + ar.cause().getMessage()); } });
	 * }
	 */

	@Test
	public void checkThatWeCanAdd() {
		// Async async = context.async();

		/*
		   WebClient client = WebClient.create(vertx); client.get(port, "localhost",
		   "/uin").send(ar -> { if (ar.succeeded()) { HttpResponse<Buffer> response =
		   ar.result(); UinEntity entity = response.bodyAsJson(UinEntity.class);
		   entity.setStatus("ASSIGNED");
		  */

		// UinEntity entity1 = new UinEntity();
		// UinEntity givEntity = new UinEntity("7638913247", "ASSIGNED");

		// WebClient client = WebClient.create(vertx);
		
		MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON,MediaType.APPLICATION_OCTET_STREAM})); 

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		UinResponseDto uinResp = restTemplate.getForObject("http://localhost:" + port + "/uin",
				UinResponseDto.class);

		System.out.println(uinResp.getUin());
		/*
		   client.get(port, "localhost", "/uin").send(ar -> { if (ar.succeeded()) {
		   HttpResponse<Buffer> response = ar.result(); String uin =
		   response.bodyAsString(); givenUin = response.bodyAsJson(UinEntity.class);
		   givenUin.setStatus("ASSIGNED");
		   
		   System.out.println("------" + uin); context.assertEquals(200,
		   response.statusCode()); client.close(); async.complete(); } else {
		   System.out.println("Something went wrong " + ar.cause().getMessage()); } });
		  */

		// System.out.println("======"+uinRepository.findAll());

		
		  /* final String json = Json.encodePrettily(givenUin); final String length =
		   Integer.toString(json.length()); // UinRepository repo =
		   Mockito.mock(UinRepository.class); //
		   System.out.println("======"+repo.findAll()); //
		   ReflectionTestUtils.setField(uinGeneratorServiceImpl, "uinRepository", repo);
		   // Mockito.when(repo.findByUin(Mockito.anyString())).thenReturn(entity); //
		   Mockito.when(repo.save(Mockito.any())).thenReturn(givEntity);
		   vertx.createHttpClient().put(port, "localhost",
		   "/uin").putHeader("content-type", "application/json")
		   .putHeader("content-length", length)
		   
		   .handler(response -> { context.assertEquals(response.statusCode(), 200);
		   context.assertTrue(response.headers().get("content-type").contains(
		   "application/json")); response.bodyHandler(body -> { final UinEntity
		   uinEntity = Json.decodeValue(body.toString(), UinEntity.class);
		   context.assertEquals(uinEntity.getUin(), "7693463571");
		   context.assertEquals(uinEntity.getStatus(), "ASSIGNED"); async.complete();
		   }); }).write(json).end();*/
		   
		 
	}
}
