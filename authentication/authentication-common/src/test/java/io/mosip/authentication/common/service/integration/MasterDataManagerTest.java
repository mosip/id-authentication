package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
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
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@WebMvcTest
public class MasterDataManagerTest {
	@Autowired
	private Environment env;

	/**
	 * The Rest Helper
	 */
	@Mock
	private RestHelperImpl restHelper;

	/**
	 * Id Info Helper
	 */
	@Mock
	private IdInfoHelper idInfoHelper;

	/**
	 * The Rest request factory
	 */
	@Mock
	private RestRequestFactory restFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	private MasterDataManager masterDataManager;
	
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(masterDataManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(masterDataManager, "restFactory", restFactory);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
	}
	
	@Test
	public void testGenderType() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		Mockito.when(restFactory.buildRequest(RestServicesConstants.GENDER_TYPE_SERVICE, null, Map.class)).thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getGender());
		Map<String, List<String>> fetchGenderType = masterDataManager.fetchGenderType();
		assertNotNull(fetchGenderType);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGenderTypeInValid() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		RestServiceException restServiceException = new RestServiceException();
		restServiceException.addInfo("12213", "restServiceException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.GENDER_TYPE_SERVICE, null, Map.class)).thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenThrow(restServiceException);
		masterDataManager.fetchGenderType();
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGenderTypeInValid2() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		RestServiceException restServiceException = new RestServiceException();
		restServiceException.addInfo("12213", "restServiceException");
		IDDataValidationException idDataValidationException = new IDDataValidationException();
		idDataValidationException.addInfo("12213", "idDataValidationException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.GENDER_TYPE_SERVICE, null, Map.class)).thenThrow(idDataValidationException);
		Mockito.when(restHelper.requestSync(buildRequest)).thenThrow(restServiceException);
		masterDataManager.fetchGenderType();
	}
	
	
	@Test
	public void testTitles() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class)).thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getTitles());
		Map<String, List<String>> fetchTitles = masterDataManager.fetchTitles();
		assertNotNull(fetchTitles);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testTitleInValid() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		RestServiceException restServiceException = new RestServiceException();
		restServiceException.addInfo("12213", "restServiceException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class)).thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenThrow(restServiceException);
		masterDataManager.fetchTitles();
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testTitleValid2() throws IdAuthenticationBusinessException, RestServiceException, JsonParseException, JsonMappingException, IOException {
		RestRequestDTO buildRequest  = new RestRequestDTO();
		RestServiceException restServiceException = new RestServiceException();
		restServiceException.addInfo("12213", "restServiceException");
		IDDataValidationException idDataValidationException = new IDDataValidationException();
		idDataValidationException.addInfo("12213", "idDataValidationException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class)).thenThrow(idDataValidationException);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getTitles());
		masterDataManager.fetchTitles();
	}
	
	private Map<String, Map<String, List<Map<String, Object>>>> getGender() throws JsonParseException, JsonMappingException, IOException{
		Map<String, Map<String, List<Map<String, Object>>>> resultMap = new HashMap<>();
		Map<String, List<Map<String, Object>>> readValue = mapper.readValue("{\r\n" + 
				"  \"genderType\": [\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MLE\",\r\n" + 
				"      \"genderName\": \"Male\",\r\n" + 
				"      \"langCode\": \"eng\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"FLE\",\r\n" + 
				"      \"genderName\": \"Female\",\r\n" + 
				"      \"langCode\": \"eng\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MLE\",\r\n" + 
				"      \"genderName\": \"ذكر\",\r\n" + 
				"      \"langCode\": \"ara\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"FLE\",\r\n" + 
				"      \"genderName\": \"أنثى\",\r\n" + 
				"      \"langCode\": \"ara\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MLE\",\r\n" + 
				"      \"genderName\": \"Mâle\",\r\n" + 
				"      \"langCode\": \"fra\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"FLE\",\r\n" + 
				"      \"genderName\": \"Femelle\",\r\n" + 
				"      \"langCode\": \"fra\",\r\n" + 
				"      \"isActive\": true\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}", new TypeReference<Map<String, List<Map<String, Object>>>>() {
				});
		resultMap.put("response", readValue);
		return resultMap;
		
	}
	
	private Map<String, Map<String, List<Map<String, String>>>> getTitles() throws JsonParseException, JsonMappingException, IOException{
		Map<String, Map<String, List<Map<String, String>>>> resultMap = new HashMap<>();
		Map<String, List<Map<String, String>>> readValue = mapper.readValue("{\r\n" + 
				"  \"titleList\": [\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIR\",\r\n" + 
				"      \"titleName\": \"Mr\",\r\n" + 
				"      \"titleDescription\": \"Male Title\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"eng\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MRS\",\r\n" + 
				"      \"titleName\": \"Mrs\",\r\n" + 
				"      \"titleDescription\": \"Female Title\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"eng\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIS\",\r\n" + 
				"      \"titleName\": \"Miss\",\r\n" + 
				"      \"titleDescription\": \"Unmarried Female Title\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"eng\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIR\",\r\n" + 
				"      \"titleName\": \"أستاذ\",\r\n" + 
				"      \"titleDescription\": \"العنوان الذكور\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"ara\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MRS\",\r\n" + 
				"      \"titleName\": \"ست \",\r\n" + 
				"      \"titleDescription\": \"عنوان أنثى\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"ara\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIS\",\r\n" + 
				"      \"titleName\": \"آنسة \",\r\n" + 
				"      \"titleDescription\": \"العنوان الإناث غير المتزوجات\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"ara\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIR\",\r\n" + 
				"      \"titleName\": \"Monsieur\",\r\n" + 
				"      \"titleDescription\": \"Titre masculin\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"fra\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MRS\",\r\n" + 
				"      \"titleName\": \"Madame\",\r\n" + 
				"      \"titleDescription\": \"Titre féminin\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"fra\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"code\": \"MIS\",\r\n" + 
				"      \"titleName\": \"Mademoiselle\",\r\n" + 
				"      \"titleDescription\": \"Titre de femme célibataire\",\r\n" + 
				"      \"isActive\": true,\r\n" + 
				"      \"langCode\": \"fra\"\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}", new TypeReference<Map<String, List<Map<String, String>>>>() {
				});
		resultMap.put("response", readValue);
		return resultMap;
		
	}

}
