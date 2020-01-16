package io.mosip.authentication.common.service.helper;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.AuditResponseDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.http.RequestWrapper;

@Ignore
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestSampleTest {

	/** The WebTest Client. */
	WebTestClient client;

	/** The audit factory. */
	@InjectMocks
	AuditRequestFactory auditFactory;

	@InjectMocks
	ObjectMapper mapper;

	/** The rest factory. */
	@InjectMocks
	RestRequestFactory restFactory;

	@Autowired
	Environment environment;

	@InjectMocks
	RestHelper restHelper;

	@InjectMocks
	TestController controller;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", environment);
		ReflectionTestUtils.setField(restFactory, "env", environment);
		ReflectionTestUtils.setField(controller, "restHelper", restHelper);
		client = WebTestClient.bindToController(new AuditTestController()).configureClient().baseUrl("/").build();
	}

	@Ignore
	@Test
	public void TestRest() {
		client.get().uri("/test").accept(MediaType.APPLICATION_JSON_UTF8).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);

	}

	@Test
	public void testRequestSync() throws Exception {
		PowerMockito.mockStatic(WebClient.class);
		PowerMockito.when(WebClient.class, WebClient.builder().clientConnector(Mockito.any()))
				.thenReturn(WebClient.builder());
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");

		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restRequest.setUri("http://localhost:8080/auditmanager/audits");
		restRequest.setTimeout(100);

		AuditResponseDto response = null;
		response = restHelper.requestSync(restRequest);

		assertTrue(response.isStatus());

//		ResponseSpec response = client.post().uri("/requestSync").accept(MediaType.APPLICATION_JSON)
//				.syncBody(restRequest)
//				.exchange()
//				.expectStatus().isOk().expectHeader()
//				.contentType(MediaType.APPLICATION_JSON_VALUE);
//		response.expectStatus();

	}

}
