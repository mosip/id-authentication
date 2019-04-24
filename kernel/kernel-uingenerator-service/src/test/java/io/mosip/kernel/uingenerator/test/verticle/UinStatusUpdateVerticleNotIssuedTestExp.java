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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UinStatusUpdateVerticleNotIssuedTestExp {

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

	@Test
	public void updateVerticle(TestContext context) throws IOException {
		Async async = context.async();

		ObjectMapper mapper = new ObjectMapper();

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		ResponseWrapper<UinResponseDto> uinResp = restTemplate
				.getForObject("http://localhost:" + port + "/uingenerator/uin", ResponseWrapper.class);
		UinResponseDto dto = mapper.convertValue(uinResp.getResponse(), UinResponseDto.class);

		UinStatusUpdateReponseDto requestDto = new UinStatusUpdateReponseDto();
		requestDto.setUin(dto.getUin());
		requestDto.setStatus("ASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrp = new RequestWrapper<>();
		requestWrp.setId("mosip.kernel.uinservice");
		requestWrp.setVersion("1.0");
		requestWrp.setRequest(requestDto);

		String reqJson = mapper.writeValueAsString(requestWrp);

		final String length = Integer.toString(reqJson.length());
		vertx.createHttpClient().put(port, "localhost", "/uingenerator/uin")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					response.bodyHandler(body -> {
						JsonObject json = body.toJsonObject();
					});
				}).write(reqJson).end();

		UinStatusUpdateReponseDto requestDtoIssue = new UinStatusUpdateReponseDto();
		requestDtoIssue.setUin(dto.getUin());
		requestDtoIssue.setStatus("UNASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrpIssue = new RequestWrapper<>();
		requestWrpIssue.setId("mosip.kernel.uinservice");
		requestWrpIssue.setVersion("1.0");
		requestWrpIssue.setRequest(requestDto);

		String reqIssueJson = mapper.writeValueAsString(requestWrpIssue);

		final String length1 = Integer.toString(reqIssueJson.length());
		vertx.createHttpClient().put(port, "localhost", "/uingenerator/uin")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					response.bodyHandler(body -> {
						JsonObject json = body.toJsonObject();
						async.complete();
					});
				}).write(reqJson).end();

	}

}
