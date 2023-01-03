package io.mosip.authentication.common.service.helper;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.core.util.DemoNormalizer;
import io.mosip.kernel.biometrics.constant.BiometricType;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingFactory.class,
		IDAMappingConfig.class })

@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
@WebMvcTest
public class IdInfoHelperTest {

	@Value("${ida.id.attribute.separator.fullAddress}")
	private String fullAddrSep;

	@InjectMocks
	IdInfoHelper idInfoHelper;

	@InjectMocks
	IdInfoFetcherImpl idInfoFetcherImpl;

	@Autowired
	private EnvUtil env;

	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		//	ReflectionTestUtils.setField(idInfoHelper, "ida-default-identity-filter-attributes", "phone,fullName,dateOfBirth,email,preferredLang");
		ReflectionTestUtils.setField(idInfoHelper, "env", env);
		ReflectionTestUtils.setField(idInfoHelper, "objectMapper", objectMapper);
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void TestgetAuthReqestInfo() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		idInfoHelper.getAuthReqestInfo(DemoMatchType.ADDR, authRequestDTO);
	}

	@Test
	public void TestgetIdentityValue() {
		List<IdentityInfoDTO> identityInfoList = getValueList();
		String language = "ara";
		String key = "FINGER_Left IndexFinger_2";
		Map<String, List<IdentityInfoDTO>> demoInfo = new HashMap<>();
		demoInfo.put(key, identityInfoList);
		ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "getIdentityValue", key, language, demoInfo);
	}

	@Test
	public void TestInvalidIdentityValue() {
		String key = "FINGER_Left IndexFinger_2";
		List<IdentityInfoDTO> identityInfoList = null;
		Map<String, List<IdentityInfoDTO>> demoInfo = new HashMap<>();
		demoInfo.put(key, identityInfoList);
		ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "getIdentityValue", key, "ara", demoInfo);
	}

	@Test
	public void TestValidgetIdentityValuefromMap() {
		List<IdentityInfoDTO> identityList = getValueList();
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		String key = "FINGER_Left IndexFinger_2";
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map, BioMatchType.FGRMIN_LEFT_INDEX);
	}

	@Test
	public void TestValidgetIdentityValuefromMapwithEmpty() {
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		String key = "FINGER_Left IndexFinger_2";
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put(key, new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map, BioMatchType.FGRMIN_LEFT_INDEX);

		List<IdentityInfoDTO> identityList1 = null;
		Map<String, Entry<String, List<IdentityInfoDTO>>> map1 = new HashMap<>();
		map1.put(key, new SimpleEntry<>("leftIndex", identityList1));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map1, BioMatchType.FGRMIN_LEFT_INDEX);

	}

	@Test
	public void TestInvalidtIdentityValuefromMap() {
		String language = "ara";
		String key = "FINGER_Left IndexFinger_2";
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, language, map, BioMatchType.FGRMIN_LEFT_INDEX);
	}

	@Test
	public void checkLanguageType() {
		ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "checkLanguageType", null, null);
	}

	@Test
	public void checkLanguageTypeEmpty() {
		ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "checkLanguageType", "", "");
	}

	@Test
	public void checkLanguageTypenull() {
		ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "checkLanguageType", "null", "null");
	}

	@Test
	public void TestgetIdMappingValue() throws IdAuthenticationBusinessException {
		MatchType matchType = DemoMatchType.ADDR;
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.NAME);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestmappingInternal() throws IdAuthenticationBusinessException {
		MatchType matchType = BioMatchType.FACE;
		List<String> value = new ArrayList<>();
		value.add(IdaIdMapping.ADDRESSLINE1.getIdname());
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.ADDR_LINE1);
	}

	@Test
	public void TestmappingInternalthrowsException() throws IdAuthenticationBusinessException {
		MatchType matchType = DemoMatchType.DOB;
		List<String> value = new ArrayList<>();
		value.add(IdaIdMapping.DOB.getIdname());
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.DOB);
	}

	@Test
	public void TestisMatchtypeEnabled() {
		idInfoHelper.isMatchtypeEnabled(DemoMatchType.GENDER);
	}

	@Test
	public void TestgetEntityInfoAsString() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		idInfo.put("phoneNumber", identityInfoList);
		List<String> value = new ArrayList<>();
		value.add("fullAddress");

		IdInfoHelper idInfoHelperSpy = Mockito.spy(idInfoHelper);
		Mockito.doReturn(null).when(idInfoHelperSpy).getIdEntityInfoMap(DemoMatchType.ADDR, idInfo, null, null);
		idInfoHelperSpy.getEntityInfoAsString(DemoMatchType.ADDR, idInfo);
	}


	@Test
	public void TestconcatValue() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> value = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("2232222222");
		value.add(identityInfoDTO);
		demoEntity.put("phone", value);
		Map<String, String> entityInfoMap = new HashMap<>();
		entityInfoMap.put("phone", "2232222222");
		entityInfoMap.put("face", "2232222224");

		IdInfoHelper idInfoHelperSpy =  Mockito.spy(idInfoHelper);
		Mockito.doReturn(entityInfoMap).when(idInfoHelperSpy).getIdEntityInfoMap(DemoMatchType.PHONE, demoEntity, null);
		idInfoHelperSpy.getEntityInfoAsString(DemoMatchType.PHONE, demoEntity);
	}


	@Test
	public void TestmatchIdentityData() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("Test");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestHMAC("string");
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		IdentityInfoDTO identityinfo = new IdentityInfoDTO();
		identityinfo.setLanguage("fre");
		identityinfo.setValue("exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3");
		List<IdentityInfoDTO> addresslist = new ArrayList<>();
		addresslist.add(identityinfo);
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		identity.setFullAddress(addresslist);
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTransactionID("1234567890");
		Map<String, List<IdentityInfoDTO>> identityEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO maildto = new IdentityInfoDTO();
		maildto.setValue("12121212121212");
		identityInfoList.add(maildto);
		identityEntity.put("phoneNumber", identityInfoList);
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = DemoAuthType.PERSONAL_IDENTITY;
		Map<String, Object> matchProperties = new HashMap<>();
		MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE, MatchingStrategyType.PARTIAL.getType(),
				60, matchProperties, "fra");
		listMatchInputsExp.add(matchInput);
		idInfoHelper.matchIdentityData(authRequestDTO, identityEntity, listMatchInputsExp, "12523823232");
	}

	@Test
	public void matchIdentityDataTest1() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("Test");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestHMAC("string");
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		IdentityInfoDTO identityinfo = new IdentityInfoDTO();
		identityinfo.setLanguage("fre");
		identityinfo.setValue("exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3");
		List<IdentityInfoDTO> addresslist = new ArrayList<>();
		addresslist.add(identityinfo);
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		identity.setFullAddress(addresslist);
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTransactionID("1234567890");
		Map<String, List<IdentityInfoDTO>> identityEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO maildto = new IdentityInfoDTO();
		maildto.setValue("12121212121212");
		identityInfoList.add(maildto);
		identityEntity.put("phoneNumber", identityInfoList);
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = DemoAuthType.PERSONAL_IDENTITY;
		Map<String, Object> matchProperties = new HashMap<>();
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE, null,
				60, matchProperties, "fra"));
		idInfoHelper.matchIdentityData(authRequestDTO,"426789089018", listMatchInputsExp,null,"12523823232");
	}
	
	@Test
	public void matchFullAddressTest1() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("Test");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestHMAC("string");
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		IdentityInfoDTO identityinfo = new IdentityInfoDTO();
		identityinfo.setLanguage("fre");
		identityinfo.setValue("Address Line1 Address Line2 Address Line3 City Region Province 11223344");
		List<IdentityInfoDTO> addresslist = new ArrayList<>();
		addresslist.add(identityinfo);
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		identity.setFullAddress(addresslist);
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTransactionID("1234567890");
		Map<String, List<IdentityInfoDTO>> identityEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("Address Line1");
		identityInfoList.add(addrLine);
		identityEntity.put("addressLine1", identityInfoList);
		
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("Address Line2");
		identityInfoList.add(addrLine);
		identityEntity.put("addressLine2", identityInfoList);
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("Address Line3");
		identityInfoList.add(addrLine);
		identityEntity.put("addressLine3", identityInfoList);
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("City");
		identityInfoList.add(addrLine);
		identityEntity.put("city", identityInfoList);
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("Region");
		identityInfoList.add(addrLine);
		identityEntity.put("region", identityInfoList);
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setLanguage("fre");
		addrLine.setValue("Province");
		identityInfoList.add(addrLine);
		identityEntity.put("province", identityInfoList);
		
		identityInfoList = new ArrayList<>();
		addrLine = new IdentityInfoDTO();
		addrLine.setValue("11223344");
		identityInfoList.add(addrLine);
		identityEntity.put("postalCode", identityInfoList);
		
		
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = DemoAuthType.ADDRESS;
		Map<String, Object> matchProperties = new HashMap<>();
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		Mockito.when(demoMatcherUtil.doExactMatch("Address Line1 Address Line2 Address Line3 City Region Province 11223344", "Address Line1 Address Line2 Address Line3 City Region Province 11223344")).thenReturn(100);
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
		DemoNormalizer demoNormalizer = Mockito.mock(DemoNormalizer.class);
		Mockito.when(demoNormalizer.normalizeAddress("Address Line1 Address Line2 Address Line3 City Region Province 11223344", "fre"))
			.thenReturn("Address Line1 Address Line2 Address Line3 City Region Province 11223344");
		matchProperties.put("demoNormalizer", demoNormalizer);
		matchProperties.put("langCode", "fre");
		
		
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR.getIdMapping().getIdname(), DemoMatchType.ADDR, null,
				100, matchProperties, "fre"));
		List<MatchOutput> matchOutput = idInfoHelper.matchIdentityData(authRequestDTO,identityEntity, listMatchInputsExp,"12523823232");
		assertEquals(100, matchOutput.get(0).getMatchValue());
	}



	@Test
	public void getEntityInfoTest1() throws Throwable {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		demoEntity.put("phoneNumber", identityInfoList);

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthType demoAuthType = DemoAuthType.DYNAMIC;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, BioMatchType.FACE.getIdMapping().getIdname(), BioMatchType.FACE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = BioMatchType.FACE;
		MatchingStrategy strategy = null;
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("1", "a");
		entityInfo.put("2", "b");
		entityInfo.put("3", "c");
		IdInfoHelper idInfoHelperSpy = Mockito.spy(idInfoHelper);
		Mockito.doReturn(entityInfo).when(idInfoHelperSpy).getIdEntityInfoMap(matchType, demoEntity, matchInput.getLanguage(), matchType.getIdMapping().getIdname());

//		System.out.println("request= "+matchType.hasRequestEntityInfo() + " id= " + matchType.hasIdEntityInfo());
		ReflectionTestUtils.invokeMethod(idInfoHelperSpy, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
				matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
	}

	@Test
	public void TestmatchType() {
		MatchInput matchInput = new MatchInput(DemoAuthType.ADDRESS, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE, "EXACT", 60, null, null);
		ReflectionTestUtils.invokeMethod(idInfoHelper, "matchType", null, null, null, matchInput, null, null);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestgetEntityInfo() throws Throwable {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		demoEntity.put("phoneNumber", identityInfoList);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthType demoAuthType = DemoAuthType.DYNAMIC;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = DemoMatchType.PHONE;
		MatchingStrategy strategy = null;
		try {
			ReflectionTestUtils.invokeMethod(idInfoHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestBiogetEntityInfo() throws Throwable {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		demoEntity.put("phoneNumber", identityInfoList);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthType demoAuthType = DemoAuthType.DYNAMIC;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, BioMatchType.FACE.getIdMapping().getIdname(), BioMatchType.FACE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
		EntityValueFetcher entityValueFetcher = null;

		MatchType matchType = BioMatchType.FACE;
		MatchingStrategy strategy = null;

		Map<String, String> entityInfo = new HashMap<>();
		IdInfoHelper idInfoHelperSpy = Mockito.spy(idInfoHelper);
		Mockito.doReturn(entityInfo).when(idInfoHelperSpy).getIdEntityInfoMap(matchType, demoEntity, matchInput.getLanguage(), matchType.getIdMapping().getIdname());

		try {
			ReflectionTestUtils.invokeMethod(idInfoHelperSpy, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}


	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestgetEntityInfowithBiowithLanguage() throws Throwable {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		demoEntity.put("phoneNumber", identityInfoList);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthType demoAuthType = DemoAuthType.PERSONAL_IDENTITY;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, "fra");
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = DemoMatchType.PHONE;
		MatchingStrategy strategy = null;
		try {
			ReflectionTestUtils.invokeMethod(idInfoHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void getDynamicEntityInfoTest() {
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		filteredIdentityInfo.put("phoneNumber", identityInfoList);
		assertEquals("test@test.com", idInfoHelper.getDynamicEntityInfoAsString(filteredIdentityInfo, "eng", "phoneNumber"));
	}

	@Test
	public void getDynamicEntityInfoExceptionTest() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		IdInfoHelper idInfoHelperSpy= Mockito.spy(idInfoHelper);
		IdAuthenticationBusinessException exception =
				new IdAuthenticationBusinessException("101", "error");

		Mockito.doThrow(exception).when(idInfoHelperSpy).getIdEntityInfoMap(DemoMatchType.DYNAMIC, filteredIdentityInfo, "eng", "phoneNumber");
		filteredIdentityInfo.put("phoneNumber", identityInfoList);
		idInfoHelperSpy.getDynamicEntityInfoAsString(filteredIdentityInfo, "eng", "phoneNumber");
	}


	@Test
	public void getDefaultFilterAttributesTest() {
		idInfoHelper.getDefaultFilterAttributes();
	}

	@Test
	public void buildDemoAttributeFiltersTest() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		authRequestDTO.setRequest(requestDTO);
		Set<String> buildDemoAttributeFilters = idInfoHelper.buildDemoAttributeFilters(authRequestDTO);
		assertEquals(0, buildDemoAttributeFilters.size());
	}
	
	@Test
	public void buildDemoAttributeFiltersTest_demoAuth() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> names = List.of(new IdentityInfoDTO("eng", "My Name"));
		identityDTO.setName(names );
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequest(requestDTO);
		Set<String> buildDemoAttributeFilters = idInfoHelper.buildDemoAttributeFilters(authRequestDTO);
		assertEquals(1, buildDemoAttributeFilters.size() );
		assertEquals("fullName", buildDemoAttributeFilters.iterator().next() );

	}
	
	@Test
	public void buildDemoAttributeFiltersTest_dynamicAttrib() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> residenceStatus = List.of(new IdentityInfoDTO("eng", "Indian"));
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequest(requestDTO);
		identityDTO.setMetadata(Map.of("residenceStatus", objectMapper.convertValue(residenceStatus, List.class)));
		Set<String> buildDemoAttributeFilters = idInfoHelper.buildDemoAttributeFilters(authRequestDTO);
		assertEquals(1, buildDemoAttributeFilters.size() );
		assertEquals("residenceStatus", buildDemoAttributeFilters.iterator().next() );

	}

	@Test
	public void getPropertyNamesForMatchTypeTest() {
		List<String> list = new ArrayList<String>();
		list.add("fullName");
		assertEquals(list, idInfoHelper.getIdentityAttributesForMatchType(DemoMatchType.NAME, "name"));
	}

	@Test
	public void getPropertyNamesForMatchTypeTest1() {
		List<String> list = new ArrayList<String>();
		list.add("fullName");
		assertEquals(list, idInfoHelper.getIdentityAttributesForMatchType(DemoMatchType.DYNAMIC, "fullName"));
	}

	@Test
	public void getPropertyNamesForMatchTypeTest2() {
		idInfoHelper.getIdentityAttributesForMatchType(DemoMatchType.NAME, "");
	}

	@Test
	public void getPropertyNamesForMatchTypeTest3() {
		List<String> list = new ArrayList<String>();
		list.add("preferredLang");
		assertEquals(list, idInfoHelper.getIdentityAttributesForMatchType(DemoMatchType.DYNAMIC, "preferredLanguage"));
	}

	@Test
	public void getIdentityAttributesForMatchTypeTest() throws IdAuthenticationBusinessException {
		List<String> list = new ArrayList<String>();
		list.add("fullName");

		IdInfoHelper idInfoHelperSpy = Mockito.spy(idInfoHelper);
		Mockito.doThrow(IdAuthenticationBusinessException.class).when(idInfoHelperSpy).getIdMappingValue(DemoMatchType.NAME.getIdMapping(), DemoMatchType.NAME);
		idInfoHelperSpy.getIdentityAttributesForMatchType(DemoMatchType.NAME, "name");
	}

	@Test
	public void buildBioFiltersTest_Empty() {
		Set<String> bioFilterExpected = new HashSet<String>();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		authRequestDTO.setRequest(requestDTO);

		assertEquals(bioFilterExpected.size(), idInfoHelper.buildBioFilters(authRequestDTO).size());
	}
	
	@Test
	public void buildBioFiltersTest_Finger() {
		Set<String> bioFilterExpected = new HashSet<String>();
		bioFilterExpected.add("Finger_Left Thumb");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("Left Thumb");
		dataDto1.setBioType("Finger");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		requestDTO.setBiometrics(bioDataList);
		authRequestDTO.setRequest(requestDTO);

		assertEquals(bioFilterExpected.size(), idInfoHelper.buildBioFilters(authRequestDTO).size());
	}

	@Test
	public void buildBioFiltersTest_Iris() {
		Set<String> bioFilterExpected = new HashSet<String>();
		bioFilterExpected.add("Iris_LEFT");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("LEFT");
		dataDto1.setBioType("Iris");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		requestDTO.setBiometrics(bioDataList);
		authRequestDTO.setRequest(requestDTO);

		assertEquals(bioFilterExpected.size(), idInfoHelper.buildBioFilters(authRequestDTO).size());
	}

	@Test
	public void buildBioFiltersTest_IrisUnkown() {
		Set<String> bioFilterExpected = new HashSet<String>();
		bioFilterExpected.add("Iris_LEFT");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("UNKNOWN");
		dataDto1.setBioType("Iris");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		requestDTO.setBiometrics(bioDataList);
		authRequestDTO.setRequest(requestDTO);

		idInfoHelper.buildBioFilters(authRequestDTO);
	}

	@Test
	public void buildBioFiltersTest_Face() {
		Set<String> bioFilterExpected = new HashSet<String>();
		bioFilterExpected.add("FACE");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();

		BioIdentityInfoDTO bioIdInfoDto2 = new BioIdentityInfoDTO();
		DataDTO dataDto2 = new DataDTO();
		dataDto2.setBioSubType("UNKNOWN");
		dataDto2.setBioType("FACE");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("9149791");
		digitalId2.setMake("eyecool");
		dataDto2.setDigitalId(digitalId2);
		dataDto2.setDomainUri("dev.mosip.net");
		dataDto2.setPurpose("Registration");
		dataDto2.setQualityScore(75f);
		dataDto2.setRequestedScore(95f);
		bioIdInfoDto2.setData(dataDto2);
		bioIdInfoDto2.setHash("12342");
		bioIdInfoDto2.setSessionKey("Testsessionkey2");
		bioIdInfoDto2.setSpecVersion("Spec1.2.0");
		bioIdInfoDto2.setThumbprint("testvalue2");

		bioDataList.add(bioIdInfoDto2);
		requestDTO.setBiometrics(bioDataList);
		authRequestDTO.setRequest(requestDTO);
		assertEquals(bioFilterExpected.size(), idInfoHelper.buildBioFilters(authRequestDTO).size());
	}

	@Test
	public void getBioSubTypesIRIS(){
		List<String> bioSubTypesIris = new ArrayList<String>();
		bioSubTypesIris.add("Iris_Left");
		bioSubTypesIris.add("Iris_Right");
		assertEquals(bioSubTypesIris, idInfoHelper.getBioSubTypes(BiometricType.IRIS));
	}


	@Test
	public void buildBioFiltersTest_FingerUnknown() {
		Set<String> bioFilterExpected = new HashSet<String>();
		bioFilterExpected.add("Finger_Right Thumb");
		bioFilterExpected.add("Finger_Left MiddleFinger");
		bioFilterExpected.add("Finger_Right LittleFinger");
		bioFilterExpected.add("Finger_Left IndexFinger");
		bioFilterExpected.add("Finger_Right RingFinger");
		bioFilterExpected.add("Finger_Right IndexFinger");
		bioFilterExpected.add("Finger_Right MiddleFinger");
		bioFilterExpected.add("Finger_Left Thumb");
		bioFilterExpected.add("Finger_Left RingFinger");
		bioFilterExpected.add("Finger_Left LittleFinger");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();

		BioIdentityInfoDTO bioIdInfoDto3 = new BioIdentityInfoDTO();
		DataDTO dataDto3 = new DataDTO();
		dataDto3.setBioSubType("UNKNOWN");
		dataDto3.setBioType("Finger");
		DigitalId digitalId3 = new DigitalId();
		digitalId3.setSerialNo("9149793");
		digitalId3.setMake("eyecool");
		dataDto3.setDigitalId(digitalId3);
		dataDto3.setDomainUri("dev.mosip.net");
		dataDto3.setPurpose("Registration");
		dataDto3.setQualityScore(77f);
		dataDto3.setRequestedScore(97f);
		bioIdInfoDto3.setData(dataDto3);
		bioIdInfoDto3.setHash("12343");
		bioIdInfoDto3.setSessionKey("Testsessionkey3");
		bioIdInfoDto3.setSpecVersion("Spec1.3.0");
		bioIdInfoDto3.setThumbprint("testvalue3");
		bioDataList.add(bioIdInfoDto3);
		requestDTO.setBiometrics(bioDataList);
		authRequestDTO.setRequest(requestDTO);
		assertEquals(bioFilterExpected.size(), idInfoHelper.buildBioFilters(authRequestDTO).size());
	}


	@Test
	public void getPropertyNamesForIdNameTest() throws IdAuthenticationBusinessException {
		idInfoHelper.getIdentityAttributesForIdName("metadata", true);
	}


	@Test
	public void containsPhotoKYCAttributeTest() {
		EkycAuthRequestDTO KycAuthRequestDTO = new  EkycAuthRequestDTO();
		List<String> allowedKycAttributes = new ArrayList<String>();
		allowedKycAttributes.add("photo");
		allowedKycAttributes.add("fullName");
		allowedKycAttributes.add("residenceStatus");
		allowedKycAttributes.add("dateOfBirth");
		KycAuthRequestDTO.setAllowedKycAttributes(allowedKycAttributes);
		assertEquals(true,idInfoHelper.containsPhotoKYCAttribute(KycAuthRequestDTO));
	}


	@Test
	public void TestgetLanguageName() {
		String langCode = "ara";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.setProperty("mosip.phonetic.lang.".concat(langCode.toLowerCase()), "arabic-ar");
		mockenv.setProperty("mosip.phonetic.lang.ar", "arabic-ar");
		env.merge(mockenv);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", env);
		Optional<String> languageName = idInfoFetcherImpl.getLanguageName(langCode);
		String value = languageName.get();
		assertEquals("arabic", value);
	}


	@Test
	public void TestEmail() {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		idInfo.put("phoneNumber", identityInfoList);
	}

	private List<IdentityInfoDTO> getValueList() {
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String language = "ara";
		identityInfoDTO.setLanguage(language);
		identityInfoDTO.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO);
		return identityList;
	}

	@Test
	public void getDataCapturedLanguagesTest() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> dobList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO dob = new IdentityInfoDTO();
		dob.setLanguage("Eng");
		dob.setValue("1993/04/11");
		dobList.add(dob);
		idInfo.put("dateOfBirth", dobList);
		List<String> expectedDobList = new ArrayList<String>();
		expectedDobList.add("Eng");
		assertEquals(expectedDobList, idInfoHelper.getDataCapturedLanguages(DemoMatchType.DOB, idInfo));
	}

	@Test
	public void getBioSubTypesTest(){
		BiometricType type = BiometricType.FINGER;
		idInfoHelper.getBioSubTypes(type);
//		type = BiometricType.IRIS;
//		idInfoHelper.getBioSubTypes(type);
		type = BiometricType.FACE;
		idInfoHelper.getBioSubTypes(type);
	}

	@Test
	public void getIdentityAttributesForIdNameTest() throws IdAuthenticationBusinessException {
		idInfoHelper.getIdentityAttributesForIdName("idName");
	}

	@Test
	public void getBioIdsTest(){
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO requestDTO = new RequestDTO();
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		authRequestDTO.setRequest(requestDTO);

		ReflectionTestUtils.invokeMethod(idInfoHelper, "getBioIds", authRequestDTO, "Iris");
	}
	
	@Test
	public void testGetFullAddressWithConfiguredSeperator() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"addressLine1", List.of(new IdentityInfoDTO("eng", "Address Line1")),
				"addressLine2", List.of(new IdentityInfoDTO("eng", "Address Line2")),
				"addressLine3", List.of(new IdentityInfoDTO("eng", "Address Line3")),
				"city", List.of(new IdentityInfoDTO("eng", "City")),
				"region", List.of(new IdentityInfoDTO("eng", "Region")),
				"province", List.of(new IdentityInfoDTO("eng", "Province")),
				"postalCode", List.of(new IdentityInfoDTO(null, "12345"))
				);
		String entityInfoAsString = idInfoHelper.getEntityInfoAsString(DemoMatchType.ADDR, "eng", idInfo);
		assertEquals(
			   "Address Line1" + fullAddrSep
			 + "Address Line2" + fullAddrSep
			 + "Address Line3" + fullAddrSep
			 + "City" + fullAddrSep
			 + "Region" + fullAddrSep
			 + "Province" + fullAddrSep
			 + "12345"
		, entityInfoAsString);
	}
	
	@Test
	public void testGetNameWithoutConfiguredSeperator() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"fullName", List.of(new IdentityInfoDTO("eng", "My Name"))
				);
		String entityInfoAsString = idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, "eng", idInfo);
		assertEquals("My Name", entityInfoAsString);
	}
	
	@Test
	public void testGetNameMap2WithoutConfiguredSeperator() throws IdAuthenticationBusinessException {
		IDAMappingConfig config = Mockito.mock(IDAMappingConfig.class);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", config);
		Mockito.when(config.getName()).thenReturn(List.of("firstName", "lastName"));
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		String entityInfoAsString = idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, "eng", idInfo);
		assertEquals("First Name Last Name", entityInfoAsString);
	}
	
	@Test
	public void testGetNameMap2WithConfiguredSeperator() throws IdAuthenticationBusinessException {
		IDAMappingConfig config = Mockito.mock(IDAMappingConfig.class);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", config);
		EnvUtil environment = Mockito.mock(EnvUtil.class);
		Mockito.when(environment.getProperty(IdAuthConfigKeyConstants.IDA_ID_ATTRIBUTE_SEPARATOR_PREFIX + "name",
				IdAuthCommonConstants.DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE))
				.thenReturn("-");
		ReflectionTestUtils.setField(idInfoHelper, "env", environment);
		Mockito.when(config.getName()).thenReturn(List.of("firstName", "lastName"));
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		String entityInfoAsString = idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, "eng", idInfo);
		assertEquals("First Name-Last Name", entityInfoAsString);
	}
	
	@Test
	public void testGetName2WithoutConfiguredSeperator() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, "eng", "name2");
		assertEquals("First Name Last Name", entityInfoAsString);
	}
	
	@Test
	public void testGetPhoneWithoutConfiguredSeperator() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"phone", List.of(new IdentityInfoDTO(null, "9988776655"))
				);
		String entityInfoAsString = idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, null, idInfo);
		assertEquals("9988776655", entityInfoAsString);
	}
	
	@Test
	public void testGetMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"residenceStatus", List.of(new IdentityInfoDTO("eng", "Citizen"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, "eng", "residenceStatus");
		assertEquals("Citizen", entityInfoAsString);
	}
	
	@Test
	public void testGetNonMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute", List.of(new IdentityInfoDTO("eng", "New Attribute"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, "eng", "newAttribute");
		assertEquals("New Attribute", entityInfoAsString);
	}
	
	@Test
	public void testGetMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"introducerRID", List.of(new IdentityInfoDTO(null, "11223344"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, null, "introducerRID");
		assertEquals("11223344", entityInfoAsString);
	}
	
	@Test
	public void testGetNonMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute1", List.of(new IdentityInfoDTO(null, "New Attribute1"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, null, "newAttribute1");
		assertEquals("New Attribute1", entityInfoAsString);
	}
	
	
	
	
	@Test
	public void testGetMapOfFullAddress() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"addressLine1", List.of(new IdentityInfoDTO("eng", "Address Line1")),
				"addressLine2", List.of(new IdentityInfoDTO("eng", "Address Line2")),
				"addressLine3", List.of(new IdentityInfoDTO("eng", "Address Line3")),
				"city", 		List.of(new IdentityInfoDTO("eng", "City")),
				"region", 		List.of(new IdentityInfoDTO("eng", "Region")),
				"province", 	List.of(new IdentityInfoDTO("eng", "Province")),
				"postalCode", 	List.of(new IdentityInfoDTO(null, "12345"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.ADDR, idInfo, "eng");
		assertEquals(
			   Map.of("addressLine1_eng", "Address Line1",
			         "addressLine2_eng", "Address Line2" ,
			         "addressLine3_eng", "Address Line3" ,
			         "city_eng", 		"City",
			         "region_eng", 		"Region" ,
			         "province_eng", 	"Province" ,
			         "postalCode", 	"12345")
		, entityInfo);
	}
	
	@Test
	public void testGetMapOfName() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"fullName", List.of(new IdentityInfoDTO("eng", "My Name"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.NAME, idInfo, "eng");
		assertEquals(Map.of("fullName_eng", "My Name"), entityInfo);
	}
	
	@Test
	public void testGetMapOfNameMap2() throws IdAuthenticationBusinessException {
		IDAMappingConfig config = Mockito.mock(IDAMappingConfig.class);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", config);
		Mockito.when(config.getName()).thenReturn(List.of("firstName", "lastName"));
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.NAME, idInfo, "eng");
		assertEquals(Map.of("firstName_eng", "First Name", "lastName_eng", "Last Name"), entityInfo);
	}
	
	//name2 is mapped in test mapping json for testing
	@Test
	public void testGetMapOfName2() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfo, "eng", "name2");
		assertEquals(Map.of("firstName_eng", "First Name", "lastName_eng", "Last Name"), entityInfo);
	}
	
	@Test
	public void testGetMapOfPhone() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"phone", List.of(new IdentityInfoDTO(null, "9988776655"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.PHONE, idInfo, null);
		assertEquals(Map.of("phone", "9988776655"), entityInfo);
	}
	
	@Test
	public void testGetStringOfMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"residenceStatus", List.of(new IdentityInfoDTO("eng", "Citizen"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, "eng", "residenceStatus");
		assertEquals("Citizen", entityInfoAsString);
		
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfo, "eng", "residenceStatus");
		assertEquals(Map.of("residenceStatus_eng", "Citizen"), entityInfo);
	}
	
	@Test
	public void testGetMapOfMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"residenceStatus", List.of(new IdentityInfoDTO("eng", "Citizen"))
				);
		Map<String, String> entityInfo = idInfoHelper.getDynamicEntityInfo(idInfo, "eng", "residenceStatus");
		assertEquals(Map.of("residenceStatus_eng", "Citizen"), entityInfo);
	}
	
	@Test
	public void testGetMapOfNonMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute", List.of(new IdentityInfoDTO("eng", "New Attribute"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfo, "eng", "newAttribute");
		assertEquals(Map.of("newAttribute_eng", "New Attribute"), entityInfo);
	}
	
	@Test
	public void testGetStringOfMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"introducerRID", List.of(new IdentityInfoDTO(null, "11223344"))
				);
		String entityInfoAsString = idInfoHelper.getDynamicEntityInfoAsString(idInfo, null, "introducerRID");
		assertEquals("11223344", entityInfoAsString);
	}
	
	@Test
	public void testGetMapOfMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"introducerRID", List.of(new IdentityInfoDTO(null, "11223344"))
				);
		Map<String, String> entityInfo =  idInfoHelper.getDynamicEntityInfo(idInfo, null, "introducerRID");
		assertEquals(Map.of("introducerRID", "11223344"), entityInfo);
	}
	
	@Test
	public void testGetMapOfNonMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute1", List.of(new IdentityInfoDTO(null, "New Attribute1"))
				);
		Map<String, String> entityInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfo, null, "newAttribute1");
		assertEquals(Map.of("newAttribute1", "New Attribute1"), entityInfo);
	}
	
}
