package io.mosip.authentication.common.service.impl;

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
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.repository.AuthAnonymousProfileRepository;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
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
	Environment env;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	AuthAnonymousProfileServiceImpl anonymousProfileServiceImpl;
	
	Map<String, Object> requestBody = null;
	Map<String, Object> responseBody = null;
	Map<String, Object> requestMetadata = null;
	Map<String, Object> responseMetadata = null;
	Map<String,List<IdentityInfoDTO>> idInfoMap = null;
	
	@Before
	public void before() {
		 requestBody = new HashMap<>();
		 responseBody = new HashMap<>();
		 requestMetadata = new HashMap<>();
		 responseMetadata = new HashMap<>();
		 idInfoMap = new HashMap<String, List<IdentityInfoDTO>>();
			
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "env", env);
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "mapper", mapper);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "preferredLangAttribName", "preferredLanguage");
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "locationProfileAttribName","locationHierarchyForProfiling");
		ReflectionTestUtils.setField(anonymousProfileServiceImpl, "dateOfBirthPattern", "yyyy/MM/dd");
	}
	
	@Test
	public void storeAnonymousProfileWith_YourOfBirthTest() throws IdAuthenticationBusinessException  {
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
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_PreferredLangTest() throws IdAuthenticationBusinessException  {
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
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_GenderTest() throws IdAuthenticationBusinessException  {
		List<IdentityInfoDTO> genderList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO gender = new IdentityInfoDTO();
		gender.setLanguage("Eng");
		gender.setValue("F");
		genderList.add(gender);
		idInfoMap.put("gender", genderList);
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.GENDER, "eng", idInfoMap)).thenReturn("F");
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody,requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_LocationTest() throws IdAuthenticationBusinessException  {
		List<IdentityInfoDTO> preferedLangList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO lang = new IdentityInfoDTO();
		lang.setLanguage("eng");
		lang.setValue("English");
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
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_BiometricInfoTest() throws IdAuthenticationBusinessException  {
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
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_AuthFactorsTest() throws IdAuthenticationBusinessException  {
		AutnTxn authTxn = new AutnTxn();
		authTxn.setAuthTypeCode("OTP-REQUEST,DEMO-AUTH,BIO-AUTH");
		responseMetadata.put("AutnTxn",authTxn);
		
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "true");
		authResponse.put("authToken", "1234567890");
		responseBody.put("response", authResponse);
		
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	@Test
	public void storeAnonymousProfileWith_PartnerTest() throws IdAuthenticationBusinessException  {
		Map<String, Object> partner = new HashMap<>();
		partner.put("partnerName", "SyncByte");
		requestMetadata.put("partnerId", "abc");
		requestMetadata.put("abc", partner);
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, true, null);
	}
	
	
	@Test
	public void storeAnonymousProfileExceptionTest() throws IdAuthenticationBusinessException  {
		Map<String, Object> authResponse = new HashMap<>();
		authResponse.put("authStatus", "false");
		authResponse.put("authToken", "");
		responseBody.put("response", authResponse);
		
		List<AuthError>  errorsList = new ArrayList<>();
		errorsList.add(new AuthError("IDA-MLC-007",  "IDA-MLC-007"));
		anonymousProfileServiceImpl.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, false, errorsList);
	}

}
