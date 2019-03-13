package io.mosip.kernel.uingenerator.test.verticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.impl.UinGeneratorServiceImpl;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinStatusUpdateVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@RunWith(VertxUnitRunner.class)
public class UinStatusUpdateVerticleTest2 {

	private Vertx vertx;
	private int port;
	
	/*@MockBean
	private UinRepository uinRepository;*/
	
	/*@MockBean
	private UinGeneratorServiceImpl uinGeneratorServiceImpl;*/
	
	@Autowired
	UinGeneratorServiceImpl uinGeneratorServiceImpl;

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


	
/*	@Test
	public void checkThatWeCanAdd(TestContext context) {
		Async async = context.async();
		
		
	  String content = "{ \"uin\" : \"9723157067\", \"status\" : \"ASSIGNED\" }";
	  UinStatusUpdateReponseDto uinStatusUpdateReponseDto = new UinStatusUpdateReponseDto();
	  uinStatusUpdateReponseDto.setUin("7693463571");
	  uinStatusUpdateReponseDto.setStatus("ASSIGNED");
	  JsonObject uin = new JsonObject(content);
	  Mockito.when(uinGeneratorServiceImpl.updateUinStatus(uin)).thenReturn(uinStatusUpdateReponseDto);
		
		
//		UinEntity entity = new UinEntity("9723157067", "ISSUED");
//		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		
		
	  final String json = Json.encodePrettily(new UinEntity("7693463571", "ASSIGNED"));
	  final String length = Integer.toString(json.length());
	  vertx.createHttpClient().put(port, "localhost", "/uin")
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
	}*/
	
	
	@Test
	public void checkThatWeCanAdd(TestContext context) {
		
		 Async async = context.async();
		 
		UinEntity entity = new UinEntity("7693463571", "ISSUED");
		UinEntity givEntity = new UinEntity("7693463571", "ASSIGNED");
	 
	  final String json = Json.encodePrettily(new UinEntity("7693463571", "ASSIGNED"));
	  final String length = Integer.toString(json.length());
	  UinRepository repo = Mockito.mock(UinRepository.class);
	  //ReflectionTestUtils.setField(uinGeneratorServiceImpl, "uinRepository", repo);
	  Mockito.when(repo.findByUin(Mockito.anyString())).thenReturn(entity);
	  Mockito.when(repo.save(Mockito.any())).thenReturn(givEntity);
	  vertx.createHttpClient().put(port, "localhost", "/uin")
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
	
	/*@Test
	public void getUinExceptionTest(TestContext context) {
		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.put(port, "localhost", "/uin").send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				context.assertEquals(200, response.statusCode());
				client.close();
				async.complete();
			} else {
				System.out.println("Something went wrong " + ar.cause().getMessage());
			}
		});
	}*/
	
}
