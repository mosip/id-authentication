package io.mosip.authentication.service.impl.id.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoServiceImpl;

/**
 * IdRepoServiceImplTest test class.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdRepoServiceImplTest {

	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private IdRepoServiceImpl idReposerviceImpl;
	@Mock
	private IdRepoServiceImpl idReposerviceImplMock;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idReposerviceImpl, "restHelper", restHelper);
		ReflectionTestUtils.setField(idReposerviceImpl, "restRequestFactory", restRequestFactory);
	}

	@Test
	public void testGetIdRepo() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
		Mockito.when(idReposerviceImpl.getIdRepo("76746685")).thenReturn(response);

		assertNotNull(response);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdRepo("76746685");
	}

	@Test
	public void testGetIdInfo() throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, IOException {
//		Map<String, Object> idResponseDTO = new HashMap<>();
//		Map<String, Object> responseMap = new HashMap<>();
//		//responseMap.put("identity", "identity");
//		//idResponseDTO.put("response", responseMap);
//		
//		IdentityInfoDTO identityName = new IdentityInfoDTO();
//		identityName.setLanguage("FR");
//		identityName.setValue("Ibrahim");
//		List<IdentityInfoDTO> name = new ArrayList<>();
//		name.add(identityName);
//		
//		IdentityDTO  identity = new IdentityDTO();
//		identity.setName(name);
//		responseMap.put("identity", identity);
//		idResponseDTO.put("response", responseMap);

		String res = "{\r\n" + 
				"              \"id\": \"mosip.id.read\",\r\n" + 
				"              \"ver\": \"1.0\",\r\n" + 
				"              \"timestamp\": \"\",\r\n" + 
				"              \"err\": \"\",\r\n" + 
				"              \"status\": \"SUCCCESSFUL\",\r\n" + 
				"              \"errmsg\": \"\",\r\n" + 
				"              \"responseCode\": \"OK\",\r\n" + 
				"              \"uin\": \"7867780967875678\",\r\n" + 
				"              \"response\": {\r\n" + 
				"                             \"identity\": {\r\n" + 
				"                                           \"firstName\": [{\r\n" + 
				"                                                          \"language\": \"AR\",\r\n" + 
				"                                                          \"label\": \"\\u0627\\u0644\\u0627\\u0633\\u0645 \\u0627\\u0644\\u0627\\u0648\\u0644\",\r\n" + 
				"                                                          \"value\": \"\\u0627\\u0628\\u0631\\u0627\\u0647\\u064A\\u0645\"\r\n" + 
				"                                           }, {\r\n" + 
				"                                                          \"language\": \"FR\",\r\n" + 
				"                                                          \"label\": \"Prénom\",\r\n" + 
				"                                                          \"value\": \"Ibrahim\"\r\n" + 
				"                                           }]\r\n" + 
				"                             }\r\n" + 
				"              }\r\n" + 
				"}\r\n" + 
				"";
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> idResponseDTO = mapper.readValue(res.getBytes(), Map.class);
		// Mockito.when(idReposerviceImplMock.getIdInfo(Mockito.anyMap())).thenReturn(Mockito.anyMap());
		ReflectionTestUtils.invokeMethod(idReposerviceImpl, "getIdInfo", idResponseDTO);
	}
}
