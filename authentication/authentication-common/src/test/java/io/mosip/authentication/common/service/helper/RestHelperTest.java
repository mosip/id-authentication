package io.mosip.authentication.common.service.helper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.tcp.BlockingNettyContext;

// TODO: Auto-generated Javadoc
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
@PrepareForTest({ WebClient.class, SslContextBuilder.class, Mock.class })
public class RestHelperTest {

	/** The rest helper. */
	@InjectMocks
	RestHelperImpl restHelper;

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
	AuditRequestFactory auditFactory;

	/** The rest factory. */
	@InjectMocks
	RestRequestFactory restFactory;

	/** The server. */
	static BlockingNettyContext server;

	/**
	 * Before.
	 *
	 * @throws SSLException the SSL exception
	 */
	@Before
	public void before() throws SSLException {
		ReflectionTestUtils.setField(auditFactory, "env", environment);
		ReflectionTestUtils.setField(restFactory, "env", environment);
		ReflectionTestUtils.setField(restHelper, "env", environment);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
		ReflectionTestUtils.setField(restHelper, "authToken", "1324");
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
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RestServiceException the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReqSync() throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"response\":{\"status\":\"success\"}}";
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}

	
	/**
	 * Test req sync with headers.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RestServiceException the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReqSyncWithHeaders() throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO=new RestRequestDTO();
		HttpHeaders headers=new HttpHeaders();
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
		PowerMockito.when(webClient.method(Mockito.any())).thenReturn(requestBodyUriSpec);
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}
	
	
	/**
	 * Test req sync unknown error.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RestServiceException the rest service exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected=RestServiceException.class)
	public void testReqSyncUnknownError() throws JsonParseException, JsonMappingException, IOException, RestServiceException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		String response = "{\"response\":{\"status\":\"success\"}}";
		RestRequestDTO restReqDTO=new RestRequestDTO();
		HttpHeaders headers=new HttpHeaders();
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}
	
	
	
	
	/**
	 * Test get auth token.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetAuthToken() throws JsonParseException, JsonMappingException, IOException {
		ReflectionTestUtils.setField(restHelper, "authToken", null);
		PowerMockito.mockStatic(WebClient.class);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		PowerMockito.when(WebClient.create(Mockito.any())).thenReturn(webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		PowerMockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
		RequestHeadersSpec requestHeadersSpec = PowerMockito.mock(RequestHeadersSpec.class);
		PowerMockito.when(requestBodyUriSpec.syncBody(Mockito.any())).thenReturn(requestHeadersSpec);
		ClientResponse clientResponse = PowerMockito.mock(ClientResponse.class);
		PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		MultiValueMap<String, ResponseCookie> map = new LinkedMultiValueMap<>();
		map.add("Authorization", ResponseCookie.from("Authorization", "1234").build());
		PowerMockito.when(clientResponse.cookies()).thenReturn(map);
		PowerMockito.when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);
		String response = "{\"response\":{\"status\":\"success\"}}";
		PowerMockito.when(clientResponse.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		ReflectionTestUtils.invokeMethod(restHelper, "getAuthToken");
	}
	
	
	/**
	 * Test get auth token invalid.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetAuthTokenInvalid() throws JsonParseException, JsonMappingException, IOException {
		ReflectionTestUtils.setField(restHelper, "authToken", null);
		PowerMockito.mockStatic(WebClient.class);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		PowerMockito.when(WebClient.create(Mockito.any())).thenReturn(webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		PowerMockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
		RequestHeadersSpec requestHeadersSpec = PowerMockito.mock(RequestHeadersSpec.class);
		PowerMockito.when(requestBodyUriSpec.syncBody(Mockito.any())).thenReturn(requestHeadersSpec);
		ClientResponse clientResponse = PowerMockito.mock(ClientResponse.class);
		PowerMockito.when(clientResponse.toEntity(Mockito.any(Class.class))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
		//PowerMockito.when(clientResponse.b(Mockito.any(Class.class))).thenReturn(clientResponse);
		PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		MultiValueMap<String, ResponseCookie> map = new LinkedMultiValueMap<>();
		map.add("Authorization", ResponseCookie.from("Authorization", "1234").build());
		PowerMockito.when(clientResponse.cookies()).thenReturn(map);
		PowerMockito.when(clientResponse.statusCode()).thenReturn(HttpStatus.CREATED);
		String response = "{\"response\":{\"status\":\"success\"}}";
		PowerMockito.when(clientResponse.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		ReflectionTestUtils.invokeMethod(restHelper, "getAuthToken");
	}
	
	

	/**
	 * test request sync with params.
	 *
	 * @throws IDDataValidationException             the ID data validation exception
	 * @throws RestServiceException             the rest service exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void vtestRequestSyncWithParams() throws IDDataValidationException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"response\":{\"status\":\"success\"}}";
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		MultiValueMap<String, String> params = new MultiValueMap<String, String>() {

			@Override
			public int size() {
				
				return 0;
			}

			@Override
			public boolean isEmpty() {
				
				return false;
			}

			@Override
			public boolean containsKey(Object key) {
				
				return false;
			}

			@Override
			public boolean containsValue(Object value) {
				
				return false;
			}

			@Override
			public List<String> get(Object key) {
				
				return null;
			}

			@Override
			public List<String> put(String key, List<String> value) {
				
				return null;
			}

			@Override
			public List<String> remove(Object key) {
				
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ? extends List<String>> m) {
				
				
			}

			@Override
			public void clear() {
				
				
			}

			@Override
			public Set<String> keySet() {
				
				return null;
			}

			@Override
			public Collection<List<String>> values() {
				
				return null;
			}

			@Override
			public Set<Entry<String, List<String>>> entrySet() {
				
				return null;
			}

			@Override
			public String getFirst(String key) {
				
				return null;
			}

			@Override
			public void add(String key, String value) {
				
				
			}

			@Override
			public void addAll(String key, List<? extends String> values) {
				
				
			}

			@Override
			public void addAll(MultiValueMap<String, String> values) {
				
				
			}

			@Override
			public void set(String key, String value) {
				
				
			}

			@Override
			public void setAll(Map<String, String> values) {
				
				
			}

			@Override
			public Map<String, String> toSingleValueMap() {
				
				return null;
			}
		};
		restReqDTO.setParams(params);
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}
	
	
	
	
	/**
	 * Vtest request sync with path variables.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws RestServiceException the rest service exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void vtestRequestSyncWithPathVariables() throws IDDataValidationException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"response\":{\"status\":\"success\"}}";
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		Map<String, String> pathVariables=new HashMap<>();;
		restReqDTO.setPathVariables(pathVariables);
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestSync(restReqDTO);
	}
	

	
	
	
	/**
	 * Utest request sync with timeout.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RestServiceException.class)
	public void utestRequestSyncWithTimeout() throws Exception {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		restReqDTO.setTimeout(1);
		restReqDTO.setResponseType(String.class);
		Map<String, String> pathVariables=new HashMap<>();;
		restReqDTO.setPathVariables(pathVariables);
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono(Mockito.any(Class.class))).thenReturn(Mono.error(new RuntimeException((new TimeoutException()))));
//		PowerMockito.doThrow(new RuntimeException((new TimeoutException()))).when(restHelper, "request", null,null);
		//ReflectionTestUtils.invokeMethod(target, name,
		restHelper.requestSync(restReqDTO);
	}
	



	/**
	 * Test request async.
	 *
	 * @throws IDDataValidationException             the ID data validation exception
	 * @throws RestServiceException             the rest service exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequestAsync() throws IDDataValidationException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//PowerMockito.when(requestHeadersSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"response\":{\"status\":\"success\"}}";
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		Map<String, String> pathVariables=new HashMap<>();;
		restReqDTO.setPathVariables(pathVariables);
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono((Class)null)).thenReturn( Mono.<Object>just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		restHelper.requestAsync(restReqDTO);
	}

	/**
	 * Test request async and return.
	 *
	 * @throws IDDataValidationException             the ID data validation exception
	 * @throws RestServiceException             the rest service exception
	 * @throws SSLException the SSL exception
	 */
	@Test(expected=RestServiceException.class)
	public void testRequestAsyncAndReturn() throws IDDataValidationException, RestServiceException, SSLException {
		PowerMockito.mockStatic(SslContextBuilder.class);
		SslContextBuilder sslContextBuilder = PowerMockito.mock(SslContextBuilder.class);
		PowerMockito.when(SslContextBuilder.forClient()).thenReturn(sslContextBuilder);
		PowerMockito.when(sslContextBuilder.trustManager(Mockito.any(TrustManagerFactory.class)))
				.thenReturn(sslContextBuilder);
		PowerMockito.when(sslContextBuilder.build()).thenThrow(new SSLException(""));
		Object object = restHelper.requestAsync(new RestRequestDTO()).get();
		if(object instanceof RestServiceException) {
			RestServiceException restServiceException = (RestServiceException) object;
			throw restServiceException;
			
		}
	}

	/**
	 * Test request async without headers.
	 *
	 * @throws IDDataValidationException             the ID data validation exception
	 * @throws RestServiceException             the rest service exception
	 * @throws InterruptedException the interrupted exception
	 */
	/*@Test
	public void testRequestAsyncWithoutHeaders() throws IDDataValidationException, RestServiceException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restRequest.setHeaders(null);

		restHelper.requestAsync(restRequest);
	}*/

	/**
	 * Test request without body.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	/*@Test
	public void testRequestWithoutBody() throws IDDataValidationException, RestServiceException {
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, null,
				AuditResponseDto.class);

		restHelper.requestAsync(restRequest);
	}

	*//**
	 * Test request without body null.
	 *
	 * @throws IDDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 *//*
	@Test(expected = RestServiceException.class)
	public void testRequestWithoutBodyNull() throws IDDataValidationException, RestServiceException {
		restHelper.requestSync(null);
	}*/

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
			throws IDDataValidationException, RestServiceException, InterruptedException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		restReqDTO.setTimeout(1);
		restReqDTO.setResponseType(String.class);
		Map<String, String> pathVariables=new HashMap<>();;
		restReqDTO.setPathVariables(pathVariables);
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono(Mockito.any(Class.class))).thenReturn(Mono.error(new WebClientResponseException("message", 200, "statusText", null, null, null)));
		restHelper.requestSync(restReqDTO);
	}

	/*
	 * this method is used to handle the unauthorised rest error
	 */
	
	/**
	 * Ztest request sync unauthorised web client response exception.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws RestServiceException the rest service exception
	 * @throws InterruptedException the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	@Ignore
	@Test(expected = RestServiceException.class)
	public void ztestRequestSyncUnauthorisedWebClientResponseException()
			throws IDDataValidationException, RestServiceException, InterruptedException {
		PowerMockito.mockStatic(WebClient.class);
		ResponseSpec responseSpec=PowerMockito.mock(ResponseSpec.class);
		PowerMockito.mock(ClientResponse.class);
		//Mono<? extends ObjectNode> monoResponse= Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class));
		RestRequestDTO restReqDTO=new RestRequestDTO();
		restReqDTO.setTimeout(1);
		restReqDTO.setResponseType(String.class);
		Map<String, String> pathVariables=new HashMap<>();;
		restReqDTO.setPathVariables(pathVariables);
		//restReqDTO.setResponseType(Mockito.any(Class.class));
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
		Function<UriBuilder, URI> uriFunction=Mockito.any();
		PowerMockito.when(requestBodyUriSpec.uri(uriFunction)).thenReturn(requestBodySpec);
		PowerMockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		PowerMockito.when(responseSpec.bodyToMono(Mockito.any(Class.class))).thenReturn(Mono.error(new WebClientResponseException("{\"errors\":[{\"errorCode\":\"KER-ATH-402\"}]}" , 401, "statusText", null, null, null)));
		restHelper.requestSync(restReqDTO);
	}
	
	
	
	
	
	/**
	 * Test get ssl context.
	 *
	 * @throws SSLException the SSL exception
	 */
	@Test()
	public void testGetSslContext() throws SSLException {
		assertTrue(ReflectionTestUtils.invokeMethod(restHelper, "getSslContext") instanceof SslContext);
	}

	/**
	 * Test get ssl context exception.
	 *
	 * @throws Throwable the throwable
	 */
	@Test(expected = RestServiceException.class)
	public void testGetSslContextException() throws Throwable {
		try {
			PowerMockito.mockStatic(SslContextBuilder.class);
			SslContextBuilder sslContextBuilder = PowerMockito.mock(SslContextBuilder.class);
			PowerMockito.when(SslContextBuilder.forClient()).thenReturn(sslContextBuilder);
			PowerMockito.when(sslContextBuilder.trustManager(Mockito.any(TrustManagerFactory.class)))
					.thenReturn(sslContextBuilder);
			PowerMockito.when(sslContextBuilder.build()).thenThrow(new SSLException(""));
			ReflectionTestUtils.invokeMethod(restHelper, "getSslContext");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * Test handle status error without response body.
	 *
	 * @throws Throwable the throwable
	 */
	@Test
	public void testHandleStatusErrorWithoutResponseBody() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 400, "failed", null, null, null), String.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	/**
	 * Test handle status error without response body unauthorised error.
	 *
	 * @throws Throwable the throwable
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
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 401, "failed", null, null, null), String.class)
					.equals(true));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	

	/**
	 * Test handle status error 4 xx.
	 *
	 * @throws Throwable the throwable
	 */
	@Test
	public void testHandleStatusError4xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 400, "failed", null,
									mapper.writeValueAsBytes(new AuthRequestDTO()), null),
							AuthRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * Test handle status error 5 xx.
	 *
	 * @throws Throwable the throwable
	 */
	@Test
	public void testHandleStatusError5xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 500, "failed", null,
									mapper.writeValueAsBytes(new AuthRequestDTO()), null),
							AuthRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * Test auth token valid.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAuthTokenValid() throws JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		PowerMockito.when(WebClient.create(Mockito.any())).thenReturn(webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		PowerMockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.cookie(Mockito.any(), Mockito.any())).thenReturn(requestBodyUriSpec);
		ClientResponse clientResponse = PowerMockito.mock(ClientResponse.class);
		PowerMockito.when(requestBodyUriSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"errors\":[{\"errorCode\":\"KER-ATH-401\"}]}";
		PowerMockito.when(clientResponse.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		ReflectionTestUtils.invokeMethod(restHelper, "checkAuthTokenExpired");
	}
	
	/**
	 * Test auth token expired.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAuthTokenExpired() throws JsonParseException, JsonMappingException, IOException {
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
		ReflectionTestUtils.invokeMethod(restHelper, "checkAuthTokenExpired");
	}

	/**
	 * Test auth token not expired.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAuthTokenNotExpired() throws JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(WebClient.class);
		WebClient webClient = PowerMockito.mock(WebClient.class);
		PowerMockito.when(WebClient.create(Mockito.any())).thenReturn(webClient);
		RequestBodyUriSpec requestBodyUriSpec = PowerMockito.mock(RequestBodyUriSpec.class);
		PowerMockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
		PowerMockito.when(requestBodyUriSpec.cookie(Mockito.any(), Mockito.any())).thenReturn(requestBodyUriSpec);
		ClientResponse clientResponse = PowerMockito.mock(ClientResponse.class);
		PowerMockito.when(requestBodyUriSpec.exchange()).thenReturn(Mono.just(clientResponse));
		String response = "{\"errors\":[{\"errorCode\":\"\"}]}";
		PowerMockito.when(clientResponse.bodyToMono(Mockito.any(Class.class)))
				.thenReturn(Mono.just(mapper.readValue(response.getBytes(), ObjectNode.class)));
		ReflectionTestUtils.invokeMethod(restHelper, "checkAuthTokenExpired");
	}

	/**
	 * Test check error response exception.
	 *
	 * @throws Throwable the throwable
	 */
	@Test(expected = RestServiceException.class)
	public void testCheckErrorResponseException() throws Throwable {
		try {
			String response = "{\"errors\":[{\"errorCode\":\"\"}]}";
			ReflectionTestUtils.invokeMethod(restHelper, "checkErrorResponse",
					mapper.readValue(response.getBytes(), Object.class), ObjectNode.class);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * Test check error response retry.
	 *
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCheckErrorResponseRetry() throws JsonParseException, JsonMappingException, IOException {
		String response = "{\"errors\":[{\"errorCode\":\"KER-ATH-401\"}]}";
		ReflectionTestUtils.invokeMethod(restHelper, "checkErrorResponse",
				mapper.readValue(response.getBytes(), Object.class), ObjectNode.class);
	}
}
