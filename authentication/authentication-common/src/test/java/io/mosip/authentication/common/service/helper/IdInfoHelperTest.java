package io.mosip.authentication.common.service.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingFactory.class,
		IDAMappingConfig.class })

@RunWith(SpringRunner.class)

@WebMvcTest
public class IdInfoHelperTest {

	@InjectMocks
	IdInfoHelper idInfoHelper;

	@InjectMocks
	IdInfoFetcherImpl idInfoFetcherImpl;

	@Autowired
	private Environment environment;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
	}

	@Test
	public void TestgetLanguageName() {
		String langCode = "ara";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty("mosip.phonetic.lang.".concat(langCode.toLowerCase()), "arabic-ar");
		mockenv.setProperty("mosip.phonetic.lang.ar", "arabic-ar");
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", mockenv);
		Optional<String> languageName = idInfoFetcherImpl.getLanguageName(langCode);
		String value = languageName.get();
		assertEquals("arabic", value);
	}

	@Test
	public void TestgetLanguageCode() {
		String priLangCode = "mosip.primary-language";
		String secLangCode = "mosip.secondary-language";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty(priLangCode, "ara");
		mockenv.setProperty(secLangCode, "fra");
		String languageCode = idInfoFetcherImpl.getLanguageCode(LanguageType.PRIMARY_LANG);
		assertEquals("ara", languageCode);
		String languageCode2 = idInfoFetcherImpl.getLanguageCode(LanguageType.SECONDARY_LANG);
		assertEquals("fra", languageCode2);
	}

	@Test
	public void TestValidgetIdentityValuefromMap() {
		List<IdentityInfoDTO> identityList = getValueList();
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		String key = "FINGER_Left IndexFinger_2";
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map);
	}

	@Test
	public void TestValidgetIdentityValuefromMapwithEmpty() {
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		String key = "FINGER_Left IndexFinger_2";
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put(key, new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map);

		List<IdentityInfoDTO> identityList1 = null;
		Map<String, Entry<String, List<IdentityInfoDTO>>> map1 = new HashMap<>();
		map1.put(key, new SimpleEntry<>("leftIndex", identityList1));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, "ara", map1);

	}

	@Test
	public void TestInvalidtIdentityValuefromMap() {
		String language = "ara";
		String key = "FINGER_Left IndexFinger_2";
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		ReflectionTestUtils.invokeMethod(idInfoHelper, "getIdentityValueFromMap", key, language, map);
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
	public void TestgetIdMappingValue() throws IdAuthenticationBusinessException {
		MatchType matchType = DemoMatchType.ADDR;
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.NAME);
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
	public void TestgetUinType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		IdType uinType = idInfoFetcherImpl.getUinOrVidType(authRequestDTO);
		assertEquals(IdType.UIN, uinType);
	}

	@Test
	public void TestgetVidType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualIdType(IdType.VID.getType());
		IdType uinType = idInfoFetcherImpl.getUinOrVidType(authRequestDTO);
		assertEquals(IdType.VID, uinType);
	}

	@Test
	public void TestgetUinorVid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		Optional<String> uinOrVid = idInfoFetcherImpl.getUinOrVid(authRequestDTO);
		assertNotEquals(Optional.empty(), uinOrVid.get());
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
		idInfoHelper.getEntityInfoAsString(DemoMatchType.ADDR, idInfo);
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
		AuthType demoAuthType = null;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = DemoMatchType.PHONE;
		MatchingStrategy strategy = null;
		Map<String, String> reqInfo = new HashMap<>();
		try {
			ReflectionTestUtils.invokeMethod(idInfoHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, reqInfo, "426789089018");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestgetEntityInfowithBio() throws Throwable {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("test@test.com");
		identityInfoList.add(identityInfoDTO);
		demoEntity.put("phoneNumber", identityInfoList);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthType demoAuthType = BioAuthType.FACE_IMG;
		Map<String, Object> matchProperties = null;
		MatchInput matchInput = new MatchInput(demoAuthType, BioMatchType.FACE, MatchingStrategyType.PARTIAL.getType(),
				60, matchProperties, null);
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = BioMatchType.FACE;
		MatchingStrategy strategy = null;
		Map<String, String> reqInfo = new HashMap<>();
		try {
			ReflectionTestUtils.invokeMethod(idInfoHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, reqInfo, "426789089018");
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
		MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, "fra");
		EntityValueFetcher entityValueFetcher = null;
		MatchType matchType = DemoMatchType.PHONE;
		MatchingStrategy strategy = null;
		Map<String, String> reqInfo = new HashMap<>();
		try {
			ReflectionTestUtils.invokeMethod(idInfoHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
					matchInput, entityValueFetcher, matchType, strategy, reqInfo, "426789089018");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}

	}

	@Test
	public void TestgetAuthReqestInfo() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		idInfoHelper.getAuthReqestInfo(DemoMatchType.ADDR, authRequestDTO);
	}

	@SuppressWarnings("null")
	@Test
	public void TestmatchIdentityData() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
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
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.PHONE, MatchingStrategyType.PARTIAL.getType(),
				60, matchProperties, "fra"));
		idInfoHelper.matchIdentityData(authRequestDTO, identityEntity, listMatchInputsExp, "12523823232");
	}

	@Test
	public void TestisMatchtypeEnabled() {
		idInfoHelper.isMatchtypeEnabled(DemoMatchType.GENDER);
	}

	@Test
	public void TestconcatValues() {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) environment));
		mockenv.setProperty(IdAuthConfigKeyConstants.MOSIP_SUPPORTED_LANGUAGES, "");
		ReflectionTestUtils.setField(idInfoHelper, "environment", mockenv);
		idInfoHelper.getAllowedLang();
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestmappingInternal() throws IdAuthenticationBusinessException {
		MatchType matchType = BioMatchType.FACE;
		List<String> value = new ArrayList<>();
		value.add(IdaIdMapping.ADDRESSLINE1.getIdname());
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.ADDR_LINE1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestmappingInternalthrowsException() throws IdAuthenticationBusinessException {
		MatchType matchType = DemoMatchType.NAME;
		List<String> value = new ArrayList<>();
		value.add(IdaIdMapping.NAME.getIdname());
		idInfoHelper.getIdMappingValue(matchType.getIdMapping(), DemoMatchType.NAME);
	}

	@Test
	public void TestmatchType() {
		MatchInput matchInput = new MatchInput(DemoAuthType.ADDRESS, DemoMatchType.PHONE, "EXACT", 60, null, null);
		ReflectionTestUtils.invokeMethod(idInfoHelper, "matchType", null, null, null, matchInput, null, null);
	}

	@Test
	public void TestconcatValue() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> value = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue("2232222222");
		value.add(identityInfoDTO);
		demoEntity.put("phone", value);
		idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, demoEntity);
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

}
