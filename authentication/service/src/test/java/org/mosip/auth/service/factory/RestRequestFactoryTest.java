/*package org.mosip.auth.service.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.auth.core.constant.AuditServicesConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.factory.AuditRequestFactory;
import org.mosip.auth.core.factory.RestRequestFactory;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.IdAuthenticationApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IdAuthenticationApplication.class)
@WebAppConfiguration
@TestPropertySource("classpath:rest-services.properties")
public class RestRequestFactoryTest {

	@Autowired
	ApplicationContext context;

	@Autowired
	AuditRequestFactory auditFactory;

	@Autowired
	RestRequestFactory restFactory;

	@Autowired
	ConfigurableEnvironment env;

	// @Test
	public void testBuildRequest() throws IdValidationFailedException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);
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
		testRequest.setHeaders(headers -> headers.setContentType(MediaType.valueOf(mediaType)));
		testRequest.setTimeout(Integer.parseInt(timeout));

		request.setHeaders(null);
		testRequest.setHeaders(null);

		assertEquals(testRequest, request);

	}

	@Test
	public void testBuildRequestEmptyUri() throws IdValidationFailedException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.uri", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE),
				AuditResponseDto.class);
	}
	
	@Test
	public void testBuildRequestNullProperties() throws IdValidationFailedException {

		MockEnvironment environment = new MockEnvironment();

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE),
				AuditResponseDto.class);
	}

	@Test
	public void testBuildRequestEmptyHttpMethod() throws IdValidationFailedException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.httpMethod", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE),
				AuditResponseDto.class);
	}

	@Test
	public void testBuildRequestEmptyResponseType() throws IdValidationFailedException {

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE), null);
	}

	@Test
	public void testBuildRequestEmptyTimeout() throws IdValidationFailedException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.timeout", "");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE),
				AuditResponseDto.class);
		
		//TODO Assert response
	}
	
	@Test
	public void testBuildRequestHeaders() throws IdValidationFailedException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("audit.rest.headers.accept", "application/json");

		ReflectionTestUtils.setField(restFactory, "env", environment);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE),
				AuditResponseDto.class);
	}

}
*/