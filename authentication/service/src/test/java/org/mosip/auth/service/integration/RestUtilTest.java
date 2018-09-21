/*package org.mosip.auth.service.integration;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.auth.core.constant.AuditServicesConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.factory.AuditRequestFactory;
import org.mosip.auth.core.factory.RestRequestFactory;
import org.mosip.auth.core.util.RestUtil;
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
import reactor.ipc.netty.http.server.HttpServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IdAuthenticationApplication.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class RestUtilTest {
	
	@Autowired
	AuditRequestFactory auditFactory;

	@Autowired
	RestRequestFactory restFactory;

	@BeforeClass
	public static void beforeClass() {

		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/auditmanager/audits"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new AuditResponseDto(true)),
						AuditResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		HttpServer.create(8082).start(adapter);

		System.err.println("started server");

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
	
	@Test
	public void testRequestWithoutBody() throws IdValidationFailedException {
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, null,
				AuditResponseDto.class);

		RestUtil.requestAsync(restRequest);
	}
}
*/