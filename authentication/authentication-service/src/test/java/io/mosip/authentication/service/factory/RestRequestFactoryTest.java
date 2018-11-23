package io.mosip.authentication.service.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestRequestFactoryTest {

	@InjectMocks
	RestRequestFactory restFactory;

	@Autowired
	ConfigurableEnvironment env;

	@Autowired
	MockMvc mockMvc;

	@InjectMocks
	AuditRequestFactory auditFactory;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
	}

	@Test
	public void testBuildRequest() throws IDDataValidationException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
		auditRequest.setActionTimeStamp(null);

		RestRequestDTO request = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		RestRequestDTO testRequest = new RestRequestDTO();
		String serviceName = RestServicesConstants.AUDIT_MANAGER_SERVICE.getServiceName();
		String uri = env.getProperty(serviceName.concat(".rest.uri"));
		String httpMethod = env.getProperty(serviceName.concat(".rest.httpMethod"));
		String mediaType = env.getProperty(serviceName.concat(".rest.headers.mediaType"));
		String timeout = env.getProperty(serviceName.concat(".rest.timeout"));

		testRequest.setUri(uri);
		testRequest.setHttpMethod(HttpMethod.valueOf(httpMethod));
		testRequest.setRequestBody(auditRequest);
		testRequest.setResponseType(AuditResponseDto.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(mediaType));
		testRequest.setHeaders(headers);
		testRequest.setTimeout(Integer.parseInt(timeout));

		request.setHeaders(null);
		testRequest.setHeaders(null);

		assertEquals(testRequest, request);

	}
	
	@Test(expected=IDDataValidationException.class)
	public void testBuildRequestWithMultiValueMap() throws IDDataValidationException {
	    
		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.headers.mediaType", "multipart/form-data");

		ReflectionTestUtils.setField(restFactory, "env", environment);
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
		auditRequest.setActionTimeStamp(null);

		RestRequestDTO request = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);


	}

	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyUri() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.uri", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory
				.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	@Test(expected = IDDataValidationException.class)
	@DirtiesContext
	public void testBuildRequestNullProperties() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyHttpMethod() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.httpMethod", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory
				.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyResponseType() throws IDDataValidationException {

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"), null);
	}

	@Test
	public void testBuildRequestEmptyTimeout() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.timeout", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);

		// TODO Assert response
	}

	@Test
	public void testBuildRequestHeaders() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.headers.accept", "application/json");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

}
