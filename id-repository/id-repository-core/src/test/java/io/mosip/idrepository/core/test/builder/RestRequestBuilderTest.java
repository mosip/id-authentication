package io.mosip.idrepository.core.test.builder;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.builder.AuditRequestBuilder;
import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.constant.IdType;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.AuditRequestDTO;
import io.mosip.idrepository.core.dto.AuditResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.kernel.core.http.RequestWrapper;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class RestRequestBuilderTest {

	@InjectMocks
	RestRequestBuilder restBuilder;

	@Autowired
	ConfigurableEnvironment env;

	@Autowired
	MockMvc mockMvc;

	@InjectMocks
	AuditRequestBuilder auditBuilder;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditBuilder, "env", env);
		ReflectionTestUtils.setField(restBuilder, "env", env);
	}

	@Test
	public void testBuildRequest() throws IdRepoDataValidationException {
		RequestWrapper<AuditRequestDTO> auditRequest = auditBuilder.buildRequest(AuditModules.ID_REPO_CORE_SERVICE,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc");
		auditRequest.getRequest().setActionTimeStamp(null);

		RestRequestDTO request = restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDTO.class);

		RestRequestDTO testRequest = new RestRequestDTO();
		String serviceName = RestServicesConstants.AUDIT_MANAGER_SERVICE.getServiceName();
		String uri = env.getProperty(serviceName.concat(".rest.uri"));
		String httpMethod = env.getProperty(serviceName.concat(".rest.httpMethod"));
		String mediaType = env.getProperty(serviceName.concat(".rest.headers.mediaType"));
		String timeout = env.getProperty(serviceName.concat(".rest.timeout"));

		testRequest.setUri(uri);
		testRequest.setHttpMethod(HttpMethod.valueOf(httpMethod));
		testRequest.setRequestBody(auditRequest);
		testRequest.setResponseType(AuditResponseDTO.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(mediaType));
		testRequest.setHeaders(headers);
		testRequest.setTimeout(Integer.parseInt(timeout));

		request.setHeaders(null);
		testRequest.setHeaders(null);
	}

	@Test(expected = IdRepoDataValidationException.class)
	public void testBuildRequestWithMultiValueMap() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.headers.mediaType", "multipart/form-data");
		environment.setProperty("mosip.idrepo.audit.rest.uri.queryparam.test", "yes");
		environment.setProperty("mosip.idrepo.audit.rest.uri.pathparam.test", "yes");

		ReflectionTestUtils.setField(restBuilder, "env", environment);
		RequestWrapper<AuditRequestDTO> auditRequest = auditBuilder.buildRequest(AuditModules.ID_REPO_CORE_SERVICE,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc");
		auditRequest.getRequest().setActionTimeStamp(null);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDTO.class);

	}

	@Test(expected = IdRepoDataValidationException.class)
	public void testBuildRequestEmptyUri() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.uri", "");

		ReflectionTestUtils.setField(restBuilder, "env", environment);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder
				.buildRequest(AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc"),
				AuditResponseDTO.class);
	}

	@Test(expected = IdRepoDataValidationException.class)
	@DirtiesContext
	public void testBuildRequestNullProperties() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();

		ReflectionTestUtils.setField(restBuilder, "env", environment);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder
				.buildRequest(AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", IdType.REG_ID,"desc"),
				AuditResponseDTO.class);
	}

	@Test(expected = IdRepoDataValidationException.class)
	public void testBuildRequestEmptyHttpMethod() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.httpMethod", "");

		ReflectionTestUtils.setField(restBuilder, "env", environment);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder
				.buildRequest(AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc"),
				AuditResponseDTO.class);
	}

	@Test(expected = IdRepoDataValidationException.class)
	public void testBuildRequestEmptyResponseType() throws IdRepoDataValidationException {

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder.buildRequest(
				AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", IdType.REG_ID,"desc"), null);
	}
	
	@Test
	public void testBuildRequestMultiValueMap() throws IdRepoDataValidationException {
		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.headers.mediaType", "multipart/form-data");
		environment.setProperty("mosip.idrepo.audit.rest.uri.queryparam.test", "yes");
		environment.setProperty("mosip.idrepo.audit.rest.uri.pathparam.test", "yes");

		ReflectionTestUtils.setField(restBuilder, "env", environment);
		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, new LinkedMultiValueMap<String, String>(),
				Object.class);
	}

	@Test
	public void testBuildRequestEmptyTimeout() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.timeout", "");

		ReflectionTestUtils.setField(restBuilder, "env", environment);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder
				.buildRequest(AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc"),
				AuditResponseDTO.class);
	}

	@Test
	public void testBuildRequestHeaders() throws IdRepoDataValidationException {

		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty("mosip.idrepo.audit.rest.headers.accept", "application/json");

		ReflectionTestUtils.setField(restBuilder, "env", environment);

		restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditBuilder
				.buildRequest(AuditModules.ID_REPO_CORE_SERVICE, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc"),
				AuditResponseDTO.class);
	}

}