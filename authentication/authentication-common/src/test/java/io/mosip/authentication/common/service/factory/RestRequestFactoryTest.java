package io.mosip.authentication.common.service.factory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.AuditResponseDto;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * The Class RestRequestFactoryTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Import(EnvUtil.class)
public class RestRequestFactoryTest {

	/** The rest factory. */
	@InjectMocks
	RestRequestFactory restFactory;

	/** The env. */
	@Autowired
	EnvUtil env;

	/** The mock mvc. */
	@Autowired
	MockMvc mockMvc;

	/** The audit factory. */
	@InjectMocks
	AuditRequestFactory auditFactory;
	
	@Autowired
	ObjectMapper mapper;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(restFactory, "env", env);
	}

	/**
	 * Test build request.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testBuildRequest() throws IDDataValidationException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
		auditRequest.getRequest().setActionTimeStamp(null);

		RestRequestDTO request = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		RestRequestDTO testRequest = new RestRequestDTO();
		String serviceName = RestServicesConstants.AUDIT_MANAGER_SERVICE.getServiceName();
		String uri = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_URI));
		String httpMethod = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_HTTP_METHOD));
		String mediaType = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_HEADERS_MEDIA_TYPE));
		String timeout = env.getProperty(serviceName.concat(IdAuthConfigKeyConstants.REST_TIMEOUT));

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
	
	/**
	 * Test build request with multi value map.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected=IDDataValidationException.class)
	public void testBuildRequestWithMultiValueMap() throws IDDataValidationException {
	    
		EnvUtil envMock = Mockito.mock(EnvUtil.class);
		when(envMock.getProperty("audit.rest.headers.mediaType")).thenReturn("multipart/form-data");
		ReflectionTestUtils.setField(restFactory, "env", envMock);
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
		auditRequest.getRequest().setActionTimeStamp(null);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);


	}

	/**
	 * Test build request empty uri.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyUri() throws IDDataValidationException {

		EnvUtil envMock = Mockito.mock(EnvUtil.class);
		when(envMock.getProperty("audit.rest.uri")).thenReturn("");
		ReflectionTestUtils.setField(restFactory, "env", envMock);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory
				.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	/**
	 * Test build request null properties.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	@DirtiesContext
	public void testBuildRequestNullProperties() throws IDDataValidationException {

		EnvUtil envMock = Mockito.mock(EnvUtil.class);
		when(envMock.getProperty(Mockito.any())).thenReturn(null);
		ReflectionTestUtils.setField(restFactory, "env", envMock);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	/**
	 * Test build request empty http method.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyHttpMethod() throws IDDataValidationException {
		EnvUtil envMock = Mockito.mock(EnvUtil.class);
		when(envMock.getProperty("audit.rest.httpMethod")).thenReturn("");
		ReflectionTestUtils.setField(restFactory, "env", envMock);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditFactory
				.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}

	/**
	 * Test build request empty response type.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testBuildRequestEmptyResponseType() throws IDDataValidationException {

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"), null);
	}

	/**
	 * Test build request empty timeout.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testBuildRequestEmptyTimeout() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("audit.rest.timeout", "");
		env.merge(environment);

		ReflectionTestUtils.setField(restFactory, "env", env);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);

		// TODO Assert response
	}

	/**
	 * Test build request headers.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testBuildRequestHeaders() throws IDDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge((ConfigurableEnvironment) env.getEnvironment());
		environment.setProperty("audit.rest.headers.accept", "application/json");

		env.merge(environment);

		ReflectionTestUtils.setField(restFactory, "env", env);

		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE,
				auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc"),
				AuditResponseDto.class);
	}
	
	/**
	 * Test build request multi value map.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testBuildRequestMultiValueMap() throws IDDataValidationException {
		MockEnvironment environment = new MockEnvironment();
		environment.merge((ConfigurableEnvironment) env.getEnvironment());
		environment.setProperty("audit.rest.headers.mediaType", "multipart/form-data");
		environment.setProperty("audit.rest.uri.queryparam.test", "yes");
		environment.setProperty("audit.rest.uri.pathparam.test", "yes");

		env.merge(environment);

		ReflectionTestUtils.setField(restFactory, "env", env);
		restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, new LinkedMultiValueMap<String, String>(),
				Object.class);
	}

}
