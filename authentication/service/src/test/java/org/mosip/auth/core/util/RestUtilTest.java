/*package org.mosip.auth.core.util;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mosip.auth.core.constant.AuditServicesConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.factory.AuditRequestFactory;
import org.mosip.auth.core.factory.RestRequestFactory;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.IdAuthenticationApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpResources;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IdAuthenticationApplication.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestUtilTest {
	
	//TODO write test for all HttpMethods(post, get, etc)
	//TODO test for timeout
	
	@Autowired
	AuditRequestFactory auditFactory;

	@Autowired
	RestRequestFactory restFactory;
	
	static BlockingNettyContext server;

	@BeforeClass
	public static void beforeClass() {

		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/audits"),
				request -> ServerResponse.status(HttpStatus.BAD_REQUEST).body(Mono.just(new AuditResponseDto(true)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		server = HttpServer.create(8082).start(adapter);
		server.installShutdownHook();

		System.err.println("started server");

	}
	
	@AfterClass
	public static void afterClass() {
		server.shutdown();
		HttpResources.reset();
	}

	@Test
	public void testRequestSyncWithTimeout() throws IdValidationFailedException, RestServiceException {

		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(100);

		AuditResponseDto response = RestUtil.requestSync(restRequest);
		
		assertTrue(response.isStatus());

	}

	@Test
	public void testRequestSyncWithoutTimeout() throws IdValidationFailedException, RestServiceException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setTimeout(null);

		AuditResponseDto response = RestUtil.requestSync(restRequest);
		
		assertTrue(response.isStatus());
	}
	
	@Test
	public void testRequestAsync() throws IdValidationFailedException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		RestUtil.requestAsync(restRequest);
	}
	
	@Test
	public void testRequestAsyncAndReturn() throws IdValidationFailedException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		AuditResponseDto response = (AuditResponseDto) RestUtil.requestAsync(restRequest).get();
		
		assertTrue(response.isStatus());
	}
	
	@Test
	public void testRequestAsyncWithoutHeaders() throws IdValidationFailedException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restRequest.setHeaders(null);

		RestUtil.requestAsync(restRequest);
	}
	
	@Test(expected=RestServiceException.class)
	public void ztestRequestSyncFor4xx() throws InterruptedException, IdValidationFailedException, RestServiceException {
		server.shutdown();
		HttpResources.reset();
		server = HttpServer.create(8082)
				.start((req, resp) -> resp.status(400).send());
		
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		RestUtil.requestSync(restRequest);
	}
	
	@Test(expected=RestServiceException.class)
	public void ztestRequestSyncFor5xx() throws InterruptedException, IdValidationFailedException, RestServiceException {
		server.shutdown();
		HttpResources.reset();
		server = HttpServer.create(8082)
				.start((req, resp) -> resp.status(500).send());
		
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		RestUtil.requestSync(restRequest);
	}
	
	@Test
	public void testRequestWithoutBody() throws IdValidationFailedException {
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, null,
				AuditResponseDto.class);

		RestUtil.requestAsync(restRequest);
	}
}
*/