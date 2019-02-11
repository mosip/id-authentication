package io.mosip.kernel.idrepo.helper;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idrepo.constant.AuditEvents;
import io.mosip.kernel.core.idrepo.constant.AuditModules;
import io.mosip.kernel.core.idrepo.constant.RestServicesConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.kernel.core.idrepo.exception.RestServiceException;
import io.mosip.kernel.idrepo.dto.AuditRequestDto;
import io.mosip.kernel.idrepo.dto.AuditResponseDto;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.RestRequestDTO;
import io.mosip.kernel.idrepo.factory.AuditRequestFactory;
import io.mosip.kernel.idrepo.factory.RestRequestFactory;
import io.netty.handler.ssl.SslContext;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpResources;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

/**
 * The Class RestUtilTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
	AuditRequestFactory auditFactory;

	/** The rest factory. */
	@InjectMocks
	RestRequestFactory restFactory;

	/** The server. */
	static BlockingNettyContext server;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", environment);
		ReflectionTestUtils.setField(restFactory, "env", environment);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
	}

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {

		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/audits"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new AuditResponseDto(true)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		System.err.println("started server");

	}

	/**
	 * After class.
	 */
	@AfterClass
	public static void afterClass() {
		server.shutdown();
		HttpResources.reset();
	}

	/**
	 * Test request sync.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestSync() throws IdRepoDataValidationException, RestServiceException {

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(100);

		AuditResponseDto response = null;
		response = restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());

	}

	/**
	 * test request sync with params.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void vtestRequestSyncWithParams() throws IdRepoDataValidationException, RestServiceException {
		MockEnvironment env = new MockEnvironment();
		env.merge(((AbstractEnvironment) environment));
		env.setProperty("audit.rest.uri.queryparam.audit", "yes");
		ReflectionTestUtils.setField(restFactory, "env", env);
		server.shutdown();
		HttpResources.reset();
		RouterFunction<?> functionSuccess = RouterFunctions.route(
				RequestPredicates.POST("/auditmanager/audits")
						.and(RequestPredicates.queryParam("audit", value -> value.equals("yes"))),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new AuditResponseDto(true)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(100);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("audit", "yes");
		restRequest.setParams(params);

		AuditResponseDto response = null;
		response = restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());

	}

	/**
	 * test request sync with path var.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void vtestRequestSyncWithPathVar() throws IdRepoDataValidationException, RestServiceException {
		MockEnvironment env = new MockEnvironment();
		env.merge(((AbstractEnvironment) environment));
		env.setProperty("audit.rest.uri.pathparam.audit", "yes");
		ReflectionTestUtils.setField(restFactory, "env", env);
		server.shutdown();
		HttpResources.reset();
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/{service}"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new AuditResponseDto(true)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(100);
		restRequest.setUri("http://127.0.0.1:8082/auditmanager/{service}");
		Map<String, String> params = new HashMap<>();
		params.put("service", "audit");
		restRequest.setPathVariables(params);

		AuditResponseDto response = null;
		response = restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());

	}

	/**
	 * test request sync with timeout.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test(expected = RestServiceException.class)
	public void utestRequestSyncWithTimeout() throws IdRepoDataValidationException, RestServiceException {
		server.shutdown();
		HttpResources.reset();
		server = HttpServer.create(8065).start((req, resp) -> {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return resp.status(200).send();
		});

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(1);

		restHelper.requestSync(restRequest);
	}

	/**
	 * Test request sync without timeout.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestSyncWithoutTimeout() throws IdRepoDataValidationException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(null);

		AuditResponseDto response = null;
		response = restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());
	}

	/**
	 * Test request async.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestAsync() throws IdRepoDataValidationException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restHelper.requestAsync(restRequest);
	}

	/**
	 * Test request async and return.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestAsyncAndReturn() throws IdRepoDataValidationException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(100);

		AuditResponseDto response = null;
		response = (AuditResponseDto) restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());;
	}

	/**
	 * Test request async without headers.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestAsyncWithoutHeaders() throws IdRepoDataValidationException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setHeaders(null);

		restHelper.requestAsync(restRequest);
	}

	/**
	 * Test request without body.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test
	public void testRequestWithoutBody() throws IdRepoDataValidationException, RestServiceException {
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, null,
				AuditResponseDto.class);

		restHelper.requestAsync(restRequest);
	}

	/**
	 * Test request without body null.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test(expected = RestServiceException.class)
	public void testRequestWithoutBodyNull() throws IdRepoDataValidationException, RestServiceException {
		restHelper.requestSync(null);
	}

	/**
	 * test request sync for 4 xx.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test(expected = RestServiceException.class)
	public void ztestRequestSyncWebClientResponseException() throws IdRepoDataValidationException, RestServiceException, InterruptedException {
		server.shutdown();
		HttpResources.reset();
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/audits"),
				request -> ServerResponse.status(HttpStatus.BAD_REQUEST).body(Mono.just(new AuditResponseDto(false)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restHelper.requestSync(restRequest);
	}

	/**
	 * test request sync for 5 xx.
	 *
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 * @throws RestServiceException
	 *             the rest service exception
	 */
	@Test(expected = RestServiceException.class)
	public void ztestRequestSyncFor5xx() throws IdRepoDataValidationException, RestServiceException {
		server.shutdown();
		HttpResources.reset();
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/audits"),
				request -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Mono.just(new AuditResponseDto(true)), AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restHelper.requestSync(restRequest);
	}

	@Test
	public void testGetSslContext() {
		assertTrue(ReflectionTestUtils.invokeMethod(restHelper, "getSslContext") instanceof SslContext);
	}

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

	@Test
	public void testHandleStatusError4xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 400, "failed", null,
									mapper.writeValueAsBytes(new IdRequestDTO()), null),
							IdRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test
	public void testHandleStatusError5xx() throws Throwable {
		try {
			assertTrue(ReflectionTestUtils
					.invokeMethod(restHelper, "handleStatusError",
							new WebClientResponseException("message", 500, "failed", null,
									mapper.writeValueAsBytes(new IdRequestDTO()), null),
							IdRequestDTO.class)
					.getClass().equals(RestServiceException.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test(expected = RestServiceException.class)
	public void testRequestSyncRuntimeException() throws IdRepoDataValidationException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",  "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restRequest.setUri("https://localhost:8082/auditmanager/v1.0/audits");
		restRequest.setTimeout(100);

		restHelper.requestSync(restRequest);
	}
	
}
