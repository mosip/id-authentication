package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;

/**
 * IdRepoServiceImplTest test class.
 *
 * @author Dinesh Karuppiah.T
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdRepoManagerTest {

	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restRequestFactory;

	@Autowired
	ConfigurableEnvironment env;

	@InjectMocks
	private IdRepoManager idReposerviceImpl;
	@Mock
	private IdRepoManager idReposerviceImplMock;

	@Autowired
	private Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idReposerviceImpl, "restHelper", restHelper);
		ReflectionTestUtils.setField(idReposerviceImpl, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(idReposerviceImpl, "environment", environment);
	}

	@Test
	public void testGetIdRepo() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "activated");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		Mockito.when(idReposerviceImpl.getIdenity("76746685", false)).thenReturn(response);
		assertNotNull(response);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException() throws IdAuthenticationBusinessException, RestServiceException {
		new RestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRegisteredStatus() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		MockEnvironment environment = new MockEnvironment();
		environment.merge(env);
		environment.setProperty(IdRepoConstants.ACTIVE_STATUS.getValue(), "Invalid");
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("status", "invalid");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException2() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException3() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdenity("76746685", true);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestUinNotFoundException() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errCode", IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode());
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidUinException() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errCode", IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestUnabletoprocess() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errCode", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode());
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestResponsebodyerrorlistEmpty() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestUinDeactivated() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.UIN_DEACTIVATED));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void checkerrorexist() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		responseBody.put("errors1", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void checkerrorexist1() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void checkerrorexist2() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errCode1", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode());
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void checkerrorexist3() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null, Map.class))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errCode", IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorCode());
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_UIN, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getIdenity("76746685", false);
	}
	
	/* this test method tests the positive scenario
	 *   to get the regId based on USERID
	 */
	
	@Test
	public void testGetRID() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("rid", "1112324546567879");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		String rid=idReposerviceImpl.getRIDByUID("76746685");
		assertEquals("1112324546567879", rid);
	}
	
	

	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to userId doesn't exists
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetRIDFailed() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "KER-ATH-003");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getRIDByUID("76746685");
	}
	
	
	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetRIDFailure() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "USER_ID_NOTEXIST_ERRORCODESSSS");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getRIDByUID("76746685");
	}
	
	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetRIDINVALID() throws IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException());
		idReposerviceImpl.getRIDByUID("76746685");
	}
	

	@Test
	public void testGetUINByRID() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("UIN", "1112324546567879923");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		Map<String,Object> uinMap=idReposerviceImpl.getUINByRID("76746685REGID");
		assertEquals("1112324546567879923", ((Map<String,Object>)uinMap.get("response")).get("UIN"));
	}
	
	
	/* this test method tests the negative scenario
	 *   to get the UIN based on regId,Here it gets failed due to inValid regId doesn't exists
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUINByRIDFailed() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "IDR-IDS-002");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getUINByRID("76746685");
	}
	
	
	/* this test method tests the negative scenario
	 *   to get the UIN based on regId,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUinByRIDFailure() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "USER_ID_NOTEXIST_ERRORCODESSSS");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, responseBody.toString(), (Object) responseBody));
		idReposerviceImpl.getUINByRID("76746685");
	}
	
	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUINBYRIDINVALID() throws IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException());
		idReposerviceImpl.getUINByRID("76746685");
	}
	
	
	
	
}
