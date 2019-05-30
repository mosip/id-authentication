package io.mosip.kernel.uingenerator.test.verticle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.uingenerator.config.UinServiceConfiguration;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.verticle.HttpServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@RunWith(VertxUnitRunner.class)
public class IntegrationTest {

	private static Vertx vertx;
	private static int port;
	private static AbstractApplicationContext context;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Logger LOGGER;

	@BeforeClass
	public static void setup(TestContext testContext) throws IOException {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		objectMapper.registerModule(new JavaTimeModule());
		LOGGER = LoggerFactory.getLogger(IntegrationTest.class);
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		context = new AnnotationConfigApplicationContext(UinServiceConfiguration.class);
		vertx = Vertx.vertx();
		Verticle[] verticles = { new UinGeneratorVerticle(context), new HttpServerVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
		try {
			LOGGER.info("Waiting for UIN generation : 5s");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void cleanup(TestContext testContext) {
		if (vertx != null && testContext != null)
			vertx.close(testContext.asyncAssertSuccess());
		if (context != null)
			context.close();
	}

	@Test
	public void getUinSuccessTest(TestContext context) {
		LOGGER.info("getUinSuccessTest execution...");
		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.get(port, "localhost", "/v1/uingenerator/uin").send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> httpResponse = ar.result();
				LOGGER.info(httpResponse.bodyAsString());
				context.assertEquals(200, httpResponse.statusCode());
				try {
					ResponseWrapper<?> uinResp = objectMapper.readValue(httpResponse.bodyAsString(),
							ResponseWrapper.class);
					UinResponseDto dto = objectMapper.convertValue(uinResp.getResponse(), UinResponseDto.class);
					context.assertNotNull(dto.getUin());
				} catch (IOException e) {
					e.printStackTrace();
				}
				client.close();
				async.complete();
			} else {
				LOGGER.error(ar.cause().getMessage());
			}
		});
	}

	@Test
	public void uinStatusUpdateSuccessTest(TestContext context) throws JsonProcessingException {
		LOGGER.info("uinStatusUpdateSuccessTest execution...");
		Async async = context.async();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		ResponseWrapper<?> uinResp = restTemplate.getForObject("http://localhost:" + port + "/v1/uingenerator/uin",
				ResponseWrapper.class);
		UinResponseDto dto = objectMapper.convertValue(uinResp.getResponse(), UinResponseDto.class);

		UinStatusUpdateReponseDto requestDto = new UinStatusUpdateReponseDto();
		requestDto.setUin(dto.getUin());
		requestDto.setStatus("ASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrp = new RequestWrapper<>();
		requestWrp.setId("mosip.kernel.uinservice");
		requestWrp.setVersion("1.0");
		requestWrp.setRequest(requestDto);

		String reqJson = objectMapper.writeValueAsString(requestWrp);

		final String length = Integer.toString(reqJson.length());
		WebClient client = WebClient.create(vertx);
		client.put(port, "localhost", "/v1/uingenerator/uin").putHeader("content-type", "application/json")
				.putHeader("content-length", length).sendJson(requestWrp, response -> {
					UinStatusUpdateReponseDto uinStatusUpdateReponseDto = null;
					if (response.succeeded()) {
						HttpResponse<Buffer> httpResponse = response.result();
						LOGGER.info(httpResponse.bodyAsString());
						context.assertEquals(httpResponse.statusCode(), 200);
						try {
							uinStatusUpdateReponseDto = objectMapper.readValue(
									httpResponse.bodyAsJsonObject().getValue("response").toString(),
									UinStatusUpdateReponseDto.class);
						} catch (IOException exception) {
							exception.printStackTrace();
						}
						context.assertEquals(uinStatusUpdateReponseDto.getStatus(), UinGeneratorConstant.ASSIGNED);
						client.close();
						async.complete();
					} else {
						LOGGER.error( response.cause().getMessage());
					}
				});

	}

	@Test
	public void uinStausUpdateUinNotFoundExpTest(TestContext context) throws IOException {
		LOGGER.info("uinStausUpdateUinNotFoundExpTest execution...");
		Async async = context.async();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		UinStatusUpdateReponseDto requestDto = new UinStatusUpdateReponseDto();
		requestDto.setUin("7676676");
		requestDto.setStatus("ASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrp = new RequestWrapper<>();
		requestWrp.setId("mosip.kernel.uinservice");
		requestWrp.setVersion("1.0");
		requestWrp.setRequest(requestDto);

		String reqJson = objectMapper.writeValueAsString(requestWrp);

		final String length = Integer.toString(reqJson.length());
		WebClient client = WebClient.create(vertx);
		client.put(port, "localhost", "/v1/uingenerator/uin").putHeader("content-type", "application/json")
				.putHeader("content-length", length).sendJson(requestWrp, response -> {
					if (response.succeeded()) {
						HttpResponse<Buffer> httpResponse = response.result();
						LOGGER.info(httpResponse.bodyAsString());
						context.assertEquals(httpResponse.statusCode(), 200);
						List<ServiceError> validationErrorsList = ExceptionUtils
								.getServiceErrorList(httpResponse.bodyAsString());
						assertTrue(validationErrorsList.size() > 0);
						boolean errorFound = false;
						for (ServiceError sr : validationErrorsList) {
							if (sr.getErrorCode().equals(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode())) {
								errorFound = true;
								break;
							}
						}
						context.assertTrue(errorFound);
						client.close();
						async.complete();
					} else {
						LOGGER.error( response.cause().getMessage());
					}
				});

	}

	@Test
	public void uinStatusUpdateStatusNotFoundExpTest(TestContext context) throws IOException {
		LOGGER.info("uinStatusUpdateStatusNotFoundExpTest execution...");
		Async async = context.async();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		RestTemplate restTemplate = new RestTemplateBuilder().defaultMessageConverters()
				.additionalMessageConverters(converter).build();

		ResponseWrapper<?> uinResp = restTemplate.getForObject("http://localhost:" + port + "/v1/uingenerator/uin",
				ResponseWrapper.class);
		UinResponseDto dto = objectMapper.convertValue(uinResp.getResponse(), UinResponseDto.class);

		UinStatusUpdateReponseDto requestDto = new UinStatusUpdateReponseDto();
		requestDto.setUin(dto.getUin());
		requestDto.setStatus("FailASSIGNED");

		RequestWrapper<UinStatusUpdateReponseDto> requestWrp = new RequestWrapper<>();
		requestWrp.setId("mosip.kernel.uinservice");
		requestWrp.setVersion("1.0");
		requestWrp.setRequest(requestDto);

		String reqJson = objectMapper.writeValueAsString(requestWrp);

		final String length = Integer.toString(reqJson.length());

		WebClient client = WebClient.create(vertx);
		client.put(port, "localhost", "/v1/uingenerator/uin").putHeader("content-type", "application/json")
				.putHeader("content-length", length).sendJson(requestWrp, response -> {
					if (response.succeeded()) {
						HttpResponse<Buffer> httpResponse = response.result();
						LOGGER.info(httpResponse.bodyAsString());
						context.assertEquals(httpResponse.statusCode(), 200);
						List<ServiceError> validationErrorsList = ExceptionUtils
								.getServiceErrorList(httpResponse.bodyAsString());
						context.assertTrue(validationErrorsList.size() > 0);
						boolean errorFound = false;
						for (ServiceError sr : validationErrorsList) {
							if (sr.getErrorCode().equals(UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorCode())) {
								errorFound = true;
								break;
							}
						}
						context.assertTrue(errorFound);
						client.close();
						async.complete();
					} else {
						LOGGER.error(response.cause().getMessage());
					}
				});
	}

}
