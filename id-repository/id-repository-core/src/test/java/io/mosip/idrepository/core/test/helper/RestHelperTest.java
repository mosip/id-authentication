package io.mosip.idrepository.core.test.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.builder.AuditRequestBuilder;
import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.AuditRequestDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.AuthenticationException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.core.publisher.Mono;

/**
 * The Class RestUtilTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PrepareForTest({ WebClient.class, SslContextBuilder.class })
public class RestHelperTest {

	/** The rest helper. */
	@InjectMocks
	RestHelper restHelper;

	/** The environment. */
	@Autowired
	Environment environment;

	/** The mapper. */
	@Autowired
	ObjectMapper mapper;

	/** The mock mvc. */
	@Autowired
	MockMvc mockMvc;

	/** The audit factory. */
	@InjectMocks
	AuditRequestBuilder auditBuilder;

	/** The rest factory. */
	@InjectMocks
	RestRequestBuilder restBuilder;

	/**
	 * Before.
	 *
	 * @throws SSLException
	 *             the SSL exception
	 */
	@Before
	public void before() throws SSLException {
		ReflectionTestUtils.setField(auditBuilder, "env", environment);
		ReflectionTestUtils.setField(restBuilder, "env", environment);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
		PowerMockito.mockStatic(SslContextBuilder.class);
		SslContextBuilder sslContextBuilder = PowerMockito.mock(SslContextBuilder.class);
		PowerMockito.when(SslContextBuilder.forClient()).thenReturn(sslContextBuilder);
		PowerMockito.when(sslContextBuilder.trustManager(Mockito.any(TrustManagerFactory.class)))
				.thenReturn(sslContextBuilder);
		PowerMockito.when(sslContextBuilder.build()).thenReturn(Mockito.mock(SslContext.class));
	}

	/**
	 * Test req sync.
	 *
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReqSync() throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		WebClient webClient = PowerMockito.mock(WebClient.class);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReqSyncWithTimeout()
			throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		restReqDTO.setTimeout(1);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Test req sync with headers.
	 *
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReqSyncWithHeaders()
			throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restReqDTO.setHeaders(headers);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.headers(Mockito.any())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Test req sync unknown error.
	 *
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = RestServiceException.class)
	public void testReqSyncUnknownError()
			throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restReqDTO.setHeaders(headers);
		restReqDTO.setResponseType(String.class);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		Function<UriBuilder, URI> uriFunction = Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * test request sync with params.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void vtestRequestSyncWithParams() throws IdRepoDataValidationException, RestServiceException,
			JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		restReqDTO.setParams(new LinkedMultiValueMap<>(0));
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Vtest request sync with path variables.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void vtestRequestSyncWithPathVariables() throws IdRepoDataValidationException, RestServiceException,
			JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		Map<String, String> pathVariables = new HashMap<>();
		restReqDTO.setPathVariables(pathVariables);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Utest request sync with timeout.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RestServiceException.class)
	public void utestRequestSyncWithTimeout() throws Exception {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		RestRequestDTO restReqDTO = new RestRequestDTO();
		restReqDTO.setTimeout(1);
		restReqDTO.setResponseType(String.class);
		Map<String, String> pathVariables = new HashMap<>();
		restReqDTO.setPathVariables(pathVariables);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		Function<UriBuilder, URI> uriFunction = Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.error(new RuntimeException((new TimeoutException()))));
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Test request async.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequestAsync() throws IdRepoDataValidationException, RestServiceException, JsonParseException,
			JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO = new RestRequestDTO();
		Map<String, String> pathVariables = new HashMap<>();
		restReqDTO.setPathVariables(pathVariables);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		RequestHeadersSpec responseHeaderSpec = PowerMockito.mock(RequestHeadersSpec.class);
		PowerMockito.when(requestBodySpec.syncBody(Mockito.any())).thenReturn(responseHeaderSpec);
		PowerMockito.when(responseHeaderSpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class) null))
				.thenReturn(Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restReqDTO.setHttpMethod(HttpMethod.GET);
		restReqDTO.setUri("0.0.0.0");
		restReqDTO.setRequestBody(response);
		restHelper.requestAsync(restReqDTO);
	}

	/**
	 * test request sync for 4 xx.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RestServiceException.class)
	public void ztestRequestSyncWebClientResponseException()
			throws IdRepoDataValidationException, RestServiceException, InterruptedException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec = PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		RestRequestDTO restReqDTO = new RestRequestDTO();
		restReqDTO.setTimeout(1);
		restReqDTO.setResponseType(String.class);
		Map<String, String> pathVariables = new HashMap<>();
		restReqDTO.setPathVariables(pathVariables);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = PowerMockito.mock(RequestBodySpec.class);
		Builder mockBuilder = PowerMockito.mock(WebClient.Builder.class);
		PowerMockito.when(WebClient.builder()).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.clientConnector(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.baseUrl(Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.defaultHeader(Mockito.any(), Mockito.any())).thenReturn(mockBuilder);
		PowerMockito.when(mockBuilder.build()).thenReturn(webClient);
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		Function<UriBuilder, URI> uriFunction = Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.error(new WebClientResponseException("message", 200, "statusText", null, null, null)));
		restHelper.requestSync(restReqDTO);
	}

	/**
	 * Test handle status error without response body.
	 *
	 * @throws Throwable
	 *             the throwable
	 */
	@Test
	public void testHandleStatusErrorWithErrorResponseBody() throws Throwable {
		try {
			RestRequestDTO restRequestDTO = new RestRequestDTO();
			restRequestDTO.setUri("0.0.0.0");
			restRequestDTO.setResponseType(ObjectNode.class);
			WebClient webClient = PowerMockito.mock(WebClient.class);
			PowerMockito.when(webClient.method(Mockito.any())).thenThrow(new WebClientResponseException("message", 400,
					"failed", null, mapper.writeValueAsBytes(restRequestDTO), null));
			restRequestDTO.setParams(new LinkedMultiValueMap<>(0));
			restRequestDTO.setPathVariables(Collections.singletonMap("", ""));
			ReflectionTestUtils.setField(restHelper, "webClient", webClient);
			restHelper.requestSync(restRequestDTO);
		} catch (RestServiceException e) {
			assertEquals(e.getErrorCode(), IdRepoErrorConstants.CLIENT_ERROR.getErrorCode());
			assertEquals(e.getErrorText(), IdRepoErrorConstants.CLIENT_ERROR.getErrorMessage());
		}
	}

	@Test
	public void testHandleTimeoutException() throws Throwable {
		try {
			RestRequestDTO restRequestDTO = new RestRequestDTO();
			restRequestDTO.setParams(new LinkedMultiValueMap<>(0));
			restRequestDTO.setPathVariables(Collections.singletonMap("", ""));
			restRequestDTO.setUri("0.0.0.0");
			restRequestDTO.setResponseType(String.class);
			WebClient webClient = PowerMockito.mock(WebClient.class);
			PowerMockito.when(webClient.method(Mockito.any()))
					.thenThrow(new RuntimeException(new TimeoutException("")));
			ReflectionTestUtils.setField(restHelper, "webClient", webClient);
			restHelper.requestSync(restRequestDTO);
		} catch (RestServiceException e) {
			assertEquals(e.getErrorCode(), IdRepoErrorConstants.CONNECTION_TIMED_OUT.getErrorCode());
			assertEquals(e.getErrorText(), IdRepoErrorConstants.CONNECTION_TIMED_OUT.getErrorMessage());
		}
	}

	/**
	 * Test handle status error without response body unauthorised error.
	 *
	 * @throws Throwable
	 *             the throwable
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleStatusErrorWithoutResponseBodyUnauthorisedError() throws Throwable {
		try {
			PowerMockito.mockStatic(WebClient.class);
			WebClient webClient = PowerMockito.mock(WebClient.class);
			PowerMockito.when(WebClient.create(Mockito.any())).thenReturn(webClient);
			RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
			PowerMockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
			PowerMockito.when(requestBodyUriSpec.cookie(Mockito.any(), Mockito.any())).thenReturn(requestBodyUriSpec);
			ClientResponse clientResponse = PowerMockito.mock(ClientResponse.class);
			PowerMockito.when(requestBodyUriSpec.exchange()).thenReturn(Mono.just(clientResponse));
			String response = "{\"errors\":[{\"errorCode\":\"KER-ATH-402\"}]}";
			PowerMockito.when(clientResponse.bodyToMono(Mockito.any(Class.class)))
					.thenReturn(Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class)));
			ReflectionTestUtils.invokeMethod(restHelper, "handleStatusError",
					new WebClientResponseException("message", 401, "failed", null, response.getBytes(), null),
					String.class);
		} catch (UndeclaredThrowableException | AuthenticationException e) {
			if (Objects.nonNull(e.getCause())) {
				AuthenticationException ex = (AuthenticationException) e.getCause();
				assertEquals(ex.getErrorCode(), "KER-ATH-402");
				assertTrue(Objects.isNull(ex.getErrorText()));
			} else {
				assertEquals(((AuthenticationException) e).getErrorCode(), "KER-ATH-402");
				assertTrue(Objects.isNull(((AuthenticationException) e).getErrorText()));
			}
		}
	}

	/**
	 * Test handle status error 4 xx.
	 *
	 * @throws Throwable
	 *             the throwable
	 */
	@Test
	public void testHandleStatusError4xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 400, "failed", null,
									mapper.writeValueAsBytes(new AuditRequestDTO()), null),
							AuditRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * Test handle status error 5 xx.
	 *
	 * @throws Throwable
	 *             the throwable
	 */
	@Test
	public void testHandleStatusError5xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 500, "failed", null,
									mapper.writeValueAsBytes(new AuditRequestDTO()), null),
							AuditRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test
	public void testHandleStatusErrorIOException() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 500, "failed", null,
									mapper.writeValueAsBytes(new AuditRequestDTO()), null),
							RestRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			RestServiceException ex = (RestServiceException) e.getCause();
			assertEquals(ex.getErrorCode(), IdRepoErrorConstants.CLIENT_ERROR.getErrorCode());
			assertEquals(ex.getErrorText(), IdRepoErrorConstants.CLIENT_ERROR.getErrorMessage());
		}
	}

	/**
	 * Test check error response exception.
	 *
	 * @throws Throwable
	 *             the throwable
	 */
	@Test
	public void testCheckErrorResponseException() throws Throwable {
		try {
			String response = "{\"errors\":[{\"errorCode\":\"\"}]}";
			ReflectionTestUtils.invokeMethod(restHelper, "checkErrorResponse",
					mapper.readValue(response.getBytes(), Object.class), ObjectNode.class);
		} catch (UndeclaredThrowableException e) {
			RestServiceException ex = (RestServiceException) e.getCause();
			assertEquals(ex.getErrorCode(), IdRepoErrorConstants.CLIENT_ERROR.getErrorCode());
			assertEquals(ex.getErrorText(), IdRepoErrorConstants.CLIENT_ERROR.getErrorMessage());
		}
	}
	
	@Test
	public void testCheckErrorResponseIOException() throws Throwable {
		try {
			String response = "{\"errors\":[{\"errorCode\":\"\"}]}";
			ReflectionTestUtils.invokeMethod(restHelper, "checkErrorResponse",
					mapper.readValue(response.getBytes(), Object.class), RestRequestDTO.class);
		} catch (UndeclaredThrowableException e) {
			RestServiceException ex = (RestServiceException) e.getCause();
			assertEquals(ex.getErrorCode(), IdRepoErrorConstants.UNKNOWN_ERROR.getErrorCode());
			assertEquals(ex.getErrorText(), IdRepoErrorConstants.UNKNOWN_ERROR.getErrorMessage());
		}
	}

	/**
	 * Test check error response retry.
	 *
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCheckErrorResponseRetry() throws JsonParseException, JsonMappingException, IOException {
		try {
			String response = "{\"errors\":[{\"errorCode\":\"KER-ATH-401\"}]}";
			ReflectionTestUtils.invokeMethod(restHelper, "checkErrorResponse",
					mapper.readValue(response.getBytes(), Object.class), ObjectNode.class);
		} catch (UndeclaredThrowableException e) {
			RestServiceException cause = (RestServiceException) e.getCause();
			assertEquals(cause.getErrorCode(), IdRepoErrorConstants.CLIENT_ERROR.getErrorCode());
			assertEquals(cause.getErrorText(), IdRepoErrorConstants.CLIENT_ERROR.getErrorMessage());
		}
	}
}