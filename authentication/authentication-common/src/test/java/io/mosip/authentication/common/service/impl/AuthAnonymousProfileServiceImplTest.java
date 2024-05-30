package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.repository.AuthAnonymousProfileRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@Import(EnvUtil.class)
public class AuthAnonymousProfileServiceImplTest {

	
	@Mock
	private IdInfoHelper idInfoHelper;
	
	@Mock
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;
	
	@Mock
	private AuthAnonymousProfileRepository authAnonymousProfileRepository;
	
	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	AuthAnonymousProfileServiceImpl anonymousProfileServiceImpl;
	
	Map<String, Object> requestBody = null;
	Map<String, Object> responseBody = null;
	Map<String, Object> requestMetadata = null;
	Map<String, Object> responseMetadata = null;
	Map<String,List<IdentityInfoDTO>> idInfoMap = null;
	List<AuthError> errorCodes = null;
	
	@Before
	public void before() {
		 requestBody = new HashMap<>();
		 responseBody = new HashMap<>();
		 requestMetadata = new HashMap<>();
		 responseMetadata = new HashMap<>();
		 idInfoMap = new HashMap<String, List<IdentityInfoDTO>>();
		 errorCodes = new ArrayList<>();
			
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "mapper", mapper);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "preferredLangAttribName", "preferredLanguage");
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "locationProfileAttribName","locationHierarchyForProfiling");
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "dateOfBirthPattern", "yyyy/MM/dd");
	}
	
	@Ignore
	@Test
	public void createAnonymousProfileWith_YourOfBirthTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		List<IdentityInfoDTO> dobList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO dob = new IdentityInfoDTO();
		dob.setLanguage("Eng");
		dob.setValue("1993/04/11");
		dobList.add(dob);
		idInfoMap.put("dateOfBirth", dobList);
		responseMetadata.put("IDENTITY_INFO", idInfoMap );

		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.DOB, idInfoMap)).thenReturn("1993/04/11");
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",
			requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(anonymousProfile.getYearOfBirth(), "1993");
	}
	
	@Ignore
	@Test
	public void createAnonymousProfileWith_PreferredLangTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		List<IdentityInfoDTO> preferedLangList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO lang = new IdentityInfoDTO();
		lang.setLanguage("eng");
		lang.setValue("English");
		preferedLangList.add(lang);
		idInfoMap.put("preferredLanguage", preferedLangList);
		responseMetadata.put("IDENTITY_INFO", idInfoMap );
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		Mockito.when(idInfoHelper.getDynamicEntityInfoAsString(idInfoMap, null, "preferredLanguage")).thenReturn("eng");
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(List.of("eng"), anonymousProfile.getPreferredLanguages());
	}
	
	@Ignore
	@Test
	public void createAnonymousProfileWith_GenderTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		List<IdentityInfoDTO> genderList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO gender = new IdentityInfoDTO();
		gender.setLanguage("eng");
		gender.setValue("Female");
		genderList.add(gender);
		idInfoMap.put("gender", genderList);
		responseMetadata.put("IDENTITY_INFO", idInfoMap );
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.GENDER, "eng", idInfoMap)).thenReturn("Female");
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody,requestMetadata, responseMetadata, true, errorCodes);
		assertEquals("Female", anonymousProfile.getGender());
	}
	
	@Ignore
	@Test
	public void createAnonymousProfileWith_LocationTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		List<IdentityInfoDTO> preferedLangList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO lang = new IdentityInfoDTO();
		lang.setLanguage(null);
		lang.setValue("eng");
		preferedLangList.add(lang);
		idInfoMap.put("preferredLanguage", preferedLangList);
		responseMetadata.put("IDENTITY_INFO", idInfoMap );
		
		Map<String,String> locationMap = new HashMap<>();
		locationMap.put("zone", "zone1");
		locationMap.put("postalCode", "123456");
		responseMetadata.put("IDENTITY_INFO", idInfoMap );
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		Mockito.when(idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfoMap, "eng", "locationHierarchyForProfiling")).thenReturn(locationMap);
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(List.of("zone1", "123456"), anonymousProfile.getLocation());
	}
	
	@Ignore
	@Test
	public void createAnonymousProfileWith_BiometricInfoTest() throws IdAuthenticationBusinessException, IOException {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		List<IdentityInfoDTO> preferedLangList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO lang = new IdentityInfoDTO();
		lang.setLanguage("eng");
		lang.setValue("English");
		preferedLangList.add(lang);
		idInfoMap.put("preferredLanguage", preferedLangList);
		responseMetadata.put("IDENTITY_INFO", idInfoMap );
		
		Map<String, Object> bioData = new HashMap<String, Object>();
		Map<String,Object> digitalIdObj = new HashMap<String, Object>();
		digitalIdObj.put("serialNo", "9149795");
		digitalIdObj.put("make", "eyecool");
		bioData.put("bioType", "Iris");
		bioData.put("bioSubType", "LEFT");
		bioData.put("digitalId", digitalIdObj);
		bioData.put("qualityScore", "70");
		Map<String, Object> bioSegmentMap = new HashMap<String, Object>(); 
		bioSegmentMap.put("data", bioData);
		List biometricList = new ArrayList<>();
		biometricList.add(bioSegmentMap);
		Map<String, Object> requestData = new HashMap<String, Object>(); 
		requestData.put("biometrics", biometricList);
		requestBody.put("request", requestData);
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(1, anonymousProfile.getBiometricInfo().size());
		assertEquals("Iris", anonymousProfile.getBiometricInfo().get(0).getType());
		assertEquals("LEFT", anonymousProfile.getBiometricInfo().get(0).getSubtype());
		assertEquals("70", anonymousProfile.getBiometricInfo().get(0).getQualityScore());
		assertEquals(digitalIdObj, mapper.readValue(anonymousProfile.getBiometricInfo().get(0).getDigitalId(), Map.class));
	}
	
	@Test
	public void createAnonymousProfileWith_AuthFactorsTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		AutnTxn authTxn = new AutnTxn();
		authTxn.setAuthTypeCode("OTP-REQUEST,DEMO-AUTH,BIO-AUTH");
		responseMetadata.put("AutnTxn",authTxn);
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(3, anonymousProfile.getAuthFactors().size());
		assertEquals(List.of("OTP-REQUEST","DEMO-AUTH","BIO-AUTH"), anonymousProfile.getAuthFactors());

	}
	
	@Test
	public void createAnonymousProfileWith_PartnerTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		PartnerDTO partner = new PartnerDTO();
		partner.setPartnerName("SyncByte");
		partner.setPartnerId("abc");
		requestMetadata.put("partnerId", "abc");
		requestMetadata.put("abc", partner);
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, true, errorCodes);
		assertEquals(partner.getPartnerName(), anonymousProfile.getPartnerName());
	}
	
	
	@Test
	public void createAnonymousProfileExceptionTest() throws IdAuthenticationBusinessException  {
		requestBody = new HashMap<>();
		requestMetadata = new HashMap<>();
		errorCodes = new ArrayList<>();
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "false");
		authResponse.put("authToken", "");
		responseBody.put("response", authResponse);
		
		List<AuthError>  errorsList = new ArrayList<>();
		errorsList.add(new AuthError("IDA-MLC-007",  "error"));
		errorsList.add(new AuthError("IDA-MLC-008",  "error1"));
		AnonymousAuthenticationProfile anonymousProfile = ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "createAnonymousProfile",requestBody, requestMetadata, responseMetadata, false, errorsList);
		assertEquals(List.of("IDA-MLC-007",  "IDA-MLC-008"), anonymousProfile.getErrorCode());
	}
	
	@Test
	public void test_storeAnonymouseProfileToDB() {
		AnonymousAuthenticationProfile anonymouseProfile = Mockito.mock(AnonymousAuthenticationProfile.class);
		ReflectionTestUtils.invokeMethod(anonymousProfileServiceImpl, "storeAnonymousProfile", anonymouseProfile);
		verify(authAnonymousProfileRepository, times(1)).save(Mockito.any());
		verify(authAnonymousProfileRepository, times(1)).flush();
	}
	
	@Test
	public void test_storeAnonymouseProfile() {
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
		verify(authAnonymousEventPublisher, times(1)).publishEvent(Mockito.any());
		verify(authAnonymousProfileRepository, times(1)).save(Mockito.any());
		verify(authAnonymousProfileRepository, times(1)).flush();
	}
	

}
