package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.dto.vid.ResponseDTO;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.util.DateUtils;

/**
 * IdRepoServiceImplTest test class.
 *
 * @author Dinesh Karuppiah.T
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdRepoManagerTest {

	private static final String EXPIRED_VID = "VID is EXPIRED";

	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restRequestFactory;

	@Autowired
	ConfigurableEnvironment env;

	@InjectMocks
	private IdRepoManager idRepomanager;
	
	@Mock
	private IdRepoManager idReposerviceImplMock;

	@Autowired
	private Environment environment;

	@Mock
	private IdentityCacheRepository idRepo;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(idRepomanager, "restHelper", restHelper);
		ReflectionTestUtils.setField(idRepomanager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(idRepomanager, "environment", environment);
		//ReflectionTestUtils.setField(idRepomanager, "mapper", mapper);
		when(securityManager.hash(Mockito.any())).thenReturn("1234");
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
		String rid=idRepomanager.getRIDByUID("76746685");
		assertEquals("1112324546567879", rid);
	}
	
	

	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to userId doesn't exists
	 */
	
	@Test
	public void testGetRIDFailed() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthCommonConstants.KER_USER_ID_NOTEXIST_ERRORCODE, IdAuthCommonConstants.KER_USER_ID_NOTEXIST_ERRORMSG, new Exception()));
		try
		{
			idRepomanager.getRIDByUID("76746685");
		}
		catch(IdAuthenticationBusinessException ex) {
			  assertEquals(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(), ex.getErrorCode());
		}
	}
	
	@Test
	public void testGetRIDFailedUnknown() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, responseBody.toString(), (Object) responseBody));
		try
		{
			idRepomanager.getRIDByUID("76746685");
		}
		catch(IdAuthenticationBusinessException ex) {
			  assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), ex.getErrorCode());
		}
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
		idRepomanager.getRIDByUID("76746685");
	}
	
	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetRIDINVALID() throws IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER));
		idRepomanager.getRIDByUID("76746685");
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testGetUINByRID() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("UIN", "1112324546567879923");
		response.put(IdAuthCommonConstants.STATUS, "ACTIVATED");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		Map<String,Object> uinMap=idRepomanager.getIdByRID("76746685REGID", false);
		assertEquals("1112324546567879923", ((Map<String,Object>)uinMap.get("response")).get("UIN"));
	}
	
	@Test
	public void testGetUINByRIDWithBio() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("UIN", "1112324546567879923");
		response.put(IdAuthCommonConstants.STATUS, "ACTIVATED");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		Map<String,Object> uinMap=idRepomanager.getIdByRID("76746685REGID", true);
		assertEquals("1112324546567879923", ((Map<String,Object>)uinMap.get("response")).get("UIN"));
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUINByRIDWithDeactivatedStatus() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("UIN", "1112324546567879923");
		response.put(IdAuthCommonConstants.STATUS, "DE-ACTIVATED");
		Map<String, Map<String, Object>> finalMap = new HashMap<>();
		finalMap.put("response", response);
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(finalMap);
		Map<String,Object> uinMap=idRepomanager.getIdByRID("76746685REGID", true);
		assertEquals("1112324546567879923", ((Map<String,Object>)uinMap.get("response")).get("UIN"));
	}
	
	
	/* this test method tests the negative scenario
	 *   to get the UIN based on regId,Here it gets failed due to inValid regId doesn't exists
	 */
	
	@Test
	public void testGetUINByRIDFailed() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "IDR-IDC-002");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, responseBody.toString(), (Object) responseBody));
		try
		{
		 idRepomanager.getIdByRID("234433356", false);
		}
		catch(IdAuthenticationBusinessException ex) {
			  assertEquals(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(), ex.getErrorCode());
		}
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
		idRepomanager.getIdByRID("76746685", false);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUinByRIDFailureUnknown() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(restRequestDTO);
		Map<String, Object> responseBody = new HashMap<>();
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, responseBody.toString(), (Object) responseBody));
		idRepomanager.getIdByRID("76746685", false);
	}
	
	/* this test method tests the negative scenario
	 *   to get the regId based on USERID,Here it gets failed due to unexpected error occurs.
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetUINBYRIDINVALID() throws IdAuthenticationBusinessException, RestServiceException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER));
		idRepomanager.getIdByRID("76746685", false);
	}
	
	
	/**
	 * Tests the positive scenario for VID Generation
	 *
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	@Ignore
	public void testGetVID() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restReq=new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restReq);
		ResponseDTO response=new ResponseDTO();
		response.setVid("12123234432243");
		VIDResponseDTO vidResponse=new VIDResponseDTO();
		vidResponse.setResponse(response);
		Map<String,Object> vidResponseMap=new HashMap<>();
		Map<String,Object> vidMap=new HashMap<>();
		vidMap.put("UIN", 12123234432243L);
		vidResponseMap.put("response", vidMap);
		Mockito.when(restHelper.requestSync(restReq)).thenReturn(vidResponseMap);
//		long actualVidResponse=idReposerviceImpl.getUINByVID("234433356");
//		assertEquals(12123234432243L, actualVidResponse);
		}
	
	/**
	 * Tests the negative scenario when VID Generation gets failed.
	 *
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	@Ignore
	public void testInvalidVID() throws RestServiceException, IdAuthenticationBusinessException {
		  RestRequestDTO restReq=new RestRequestDTO();
			Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restReq);
			Map<String, Object> responseBody = new HashMap<>();
			List<Map<String, Object>> valuelist = new ArrayList<>();
			Map<String, Object> errorcode = new HashMap<>();
			errorcode.put("errorCode",  IdRepoErrorConstants.INVALID_VID.getErrorCode());
			errorcode.put("message", IdRepoErrorConstants.INVALID_VID.getErrorMessage());
			valuelist.add(errorcode);
			responseBody.put("errors", valuelist);
			Mockito.when(restHelper.requestSync(restReq)).thenThrow(new RestServiceException(
					IdAuthenticationErrorConstants.INVALID_VID, responseBody.toString(), (Object) responseBody));
//			try
//			{
//			 idReposerviceImpl.getUINByVID("234433356");
//			}
//			catch(IdAuthenticationBusinessException ex) {
//				  assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(), ex.getErrorCode());
//				  assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage(), ex.getErrorText());
//			}
		}
	
	
	/**
	 * Tests the negative scenario when VID Generation gets failed.
	 *
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	@Ignore
	public void testExpiredVID() throws RestServiceException, IdAuthenticationBusinessException {
	  RestRequestDTO restReq=new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restReq);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "IDR-VID-002");
		errorcode.put("message", EXPIRED_VID);
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(restReq)).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.EXPIRED_VID, responseBody.toString(), (Object) responseBody));
//		try
//		{
//		 idReposerviceImpl.getUINByVID("234433356");
//		}
//		catch(IdAuthenticationBusinessException ex) {
//			  assertEquals(IdAuthenticationErrorConstants.EXPIRED_VID.getErrorCode(), ex.getErrorCode());
//		}
	}
	
	@Test
	@Ignore
	public void testIDNotAvailableForVID() throws RestServiceException, IdAuthenticationBusinessException {
	  RestRequestDTO restReq=new RestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restReq);
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> valuelist = new ArrayList<>();
		Map<String, Object> errorcode = new HashMap<>();
		errorcode.put("errorCode", "IDR-IDC-007");
		errorcode.put("message", "VID not available in database");
		valuelist.add(errorcode);
		responseBody.put("errors", valuelist);
		Mockito.when(restHelper.requestSync(restReq)).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.ID_NOT_AVAILABLE, responseBody.toString(), (Object) responseBody));
//		try
//		{
//		 idReposerviceImpl.getUINByVID("234433356");
//		}
//		catch(IdAuthenticationBusinessException ex) {
//			  assertEquals(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(), ex.getErrorCode());
//		}
	}
	
	
	
	/**
	 * Tests the negative scenario when VID Generation gets failed because of rest request(RestRequestDTO).
	 *
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	@Ignore
	public void testIDDataValaidationException() throws RestServiceException, IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER));
//		idReposerviceImpl.getUINByVID("234433356");
		}
	
	

	@Test(expected=IdAuthenticationBusinessException.class)
	@Ignore
	public void testRestServiceException() throws RestServiceException, IdAuthenticationBusinessException {
		 RestRequestDTO restReq=new RestRequestDTO();
		 Mockito.when(restRequestFactory.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restReq);
		  Mockito.when(restHelper.requestSync(restReq)).thenThrow(new RestServiceException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
//		   idReposerviceImpl.getUINByVID("234433356");
		}	

}
	
