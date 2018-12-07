package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthSecureDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(IDAMappingConfig.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DemoAuthServiceTest {

	@Autowired
	private Environment environment;

//	@InjectMocks
//	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private DemoAuthServiceImpl demoAuthServiceImpl;

	@Autowired
	private IDAMappingConfig idMappingConfig;

//	private IdInfoMatcher demomatcher = new IdInfoMatcher();

	@Mock
	private IdInfoHelper idInfoHelper;

	@Mock
	private IdRepoService idInfoService;

	@Before
	public void before() {

		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);

		ReflectionTestUtils.setField(demoAuthServiceImpl, "environment", environment);
		ReflectionTestUtils.setField(demoAuthServiceImpl, "idInfoHelper", idInfoHelper);
//		ReflectionTestUtils.setField(demoAuthServiceImpl, "idInfoHelper", idInfoHelper);

//		ReflectionTestUtils.setField(demomatcher, "idInfoHelper", idInfoHelper);

	}

	@Test
	public void test() {
		System.err.println(environment.getProperty("mosip.secondary.lang-code"));
	}

	@Test
	public void fadMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAddress(false);
		authType.setFullAddress(true);
		authType.setBio(false);
		authType.setOtp(false);
		authType.setPersonalIdentity(false);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setId("IDA");
		authRequestDTO.setIdvId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue("Test");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setIdentity(identityDTO);
		authRequestDTO.setIdvIdType("D");
		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
		authSecureDTO.setPublicKeyCert("1234567890");
		authSecureDTO.setSessionKey("1234567890");
		authRequestDTO.setKey(authSecureDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("fullAddress");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfo.setMatchingThreshold(60);
		List<MatchInfo> matchInfolist = new ArrayList<>();
		matchInfolist.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfolist);
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setPinInfo(new ArrayList<>());
		authRequestDTO.setReqHmac("string");
		authRequestDTO.setReqTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		IdentityInfoDTO identityinfo = new IdentityInfoDTO();
		identityinfo.setLanguage("FR");
		identityinfo.setValue("exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3");
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> addresslist = new ArrayList<>();
		addresslist.add(identityinfo);
		identity.setFullAddress(addresslist);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
		Map<String, Object> matchProperties = new HashMap<>();
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_SEC,
				MatchingStrategyType.PARTIAL.getType(), 60, matchProperties));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		Mockito.when(idInfoHelper.constructMatchInput(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(listMatchInputsExp);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertEquals(listMatchInputsExp, listMatchInputsActual);
		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@Test
	public void adMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAddress(true);
		authType.setFullAddress(false);
		authType.setBio(false);
		authType.setOtp(false);
		authType.setPersonalIdentity(false);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setId("IDA");
		authRequestDTO.setId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("FR");
		identityInfoDTO.setValue(
				"exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3 Casablanca Tanger-Tétouan-Al Hoceima Fès-Meknès");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setIdentity(identityDTO);
		authRequestDTO.setIdvIdType("D");
		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
		authSecureDTO.setPublicKeyCert("1234567890");
		authSecureDTO.setSessionKey("1234567890");
		authRequestDTO.setKey(authSecureDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("address");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy(MatchingStrategyType.EXACT.getType());
		matchInfo.setMatchingThreshold(100);
		List<MatchInfo> matchInfolist = new ArrayList<>();
		matchInfolist.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfolist);
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setPinInfo(new ArrayList<>());
		authRequestDTO.setReqHmac("string");
		authRequestDTO.setReqTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		IdentityInfoDTO address1 = new IdentityInfoDTO();
		address1.setLanguage("FR");
		address1.setValue("exemple d'adresse ligne 1 ");
		IdentityInfoDTO address2 = new IdentityInfoDTO();
		address2.setLanguage("FR");
		address2.setValue("exemple d'adresse ligne 2");
		IdentityInfoDTO address3 = new IdentityInfoDTO();
		address3.setLanguage("FR");
		address3.setValue("exemple d'adresse ligne 3");
		IdentityInfoDTO location1 = new IdentityInfoDTO();
		location1.setLanguage("FR");
		location1.setValue("Casablanca");
		IdentityInfoDTO location2 = new IdentityInfoDTO();
		location2.setLanguage("FR");
		location2.setValue("Tanger-Tétouan-Al Hoceima");
		IdentityInfoDTO location3 = new IdentityInfoDTO();
		location3.setLanguage("FR");
		location3.setValue("Fès-Meknès");
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> addressLine1 = new ArrayList<>();
		addressLine1.add(address1);
		List<IdentityInfoDTO> addressLine2 = new ArrayList<>();
		addressLine2.add(address2);
		List<IdentityInfoDTO> addressLine3 = new ArrayList<>();
		addressLine3.add(address3);
		identity.setAddressLine1(addressLine1);
		identity.setAddressLine2(addressLine2);
		identity.setAddressLine3(addressLine3);
		List<IdentityInfoDTO> location1list = new ArrayList<>();
		location1list.add(location1);
		List<IdentityInfoDTO> location2list = new ArrayList<>();
		location2list.add(location2);
		List<IdentityInfoDTO> location3list = new ArrayList<>();
		location3list.add(location3);
		identity.setLocation1(location1list);
		identity.setLocation2(location2list);
		identity.setLocation3(location3list);
		request.setIdentity(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		Map<String, Object> matchProperties = new HashMap<>();
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE1_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE2_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE3_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION1_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION2_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION3_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.PINCODE_SEC, MatchingStrategyType.EXACT.getType(), 100));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		Mockito.when(idInfoHelper.constructMatchInput(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(listMatchInputsExp);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@Test
	public void pidMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAddress(false);
		authType.setFullAddress(false);
		authType.setBio(false);
		authType.setOtp(false);
		authType.setPersonalIdentity(true);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setId("IDA");
		authRequestDTO.setIdvId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		/* Name */
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO nameInfoDTO = new IdentityInfoDTO();
		nameInfoDTO.setLanguage("FR");
		nameInfoDTO.setValue("Ibrahim Ibn Ali");
		nameList.add(nameInfoDTO);
		identityDTO.setName(nameList);
		requestDTO.setIdentity(identityDTO);
		authRequestDTO.setIdvIdType("D");
		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
		authSecureDTO.setPublicKeyCert("1234567890");
		authSecureDTO.setSessionKey("1234567890");
		authRequestDTO.setKey(authSecureDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("personalIdentity");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy(MatchingStrategyType.EXACT.getType());
		matchInfo.setMatchingThreshold(100);
		List<MatchInfo> matchInfolist = new ArrayList<>();
		matchInfolist.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfolist);
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setPinInfo(new ArrayList<>());
		authRequestDTO.setReqHmac("string");
		authRequestDTO.setReqTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		request.setIdentity(identityDTO);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		Map<String, Object> matchProperties = new HashMap<>();
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.NAME_SEC,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.AGE, MatchingStrategyType.EXACT.getType(),
				100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.DOB, MatchingStrategyType.EXACT.getType(),
				100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.EMAIL, MatchingStrategyType.EXACT.getType(),
				100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.PHONE, MatchingStrategyType.EXACT.getType(),
				100, matchProperties));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.GENDER, MatchingStrategyType.EXACT.getType(),
				100, matchProperties));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		Mockito.when(idInfoHelper.constructMatchInput(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(listMatchInputsExp);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));
	}

	@Test
	public void constructMatchInputTestNoFad() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAddress(false);
		authType.setFullAddress(false);
		authType.setBio(false);
		authType.setOtp(false);
		authType.setPersonalIdentity(false);
		authType.setPin(false);
		authRequest.setAuthType(authType);
		List<MatchInput> matchInputs = new ArrayList<>();
		Mockito.when(idInfoHelper.constructMatchInput(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(matchInputs);
		Method constructInputMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		constructInputMethod.setAccessible(true);
		List<MatchInput> listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl,
				authRequest);
		assertTrue(listMatchInputsAct.isEmpty());
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl, authRequestDTO);
		assertTrue(listMatchInputsAct.isEmpty());
	}

//	@Ignore
//	@Test
//	public void getDemoEntityTest() throws IdAuthenticationBusinessException {
//		// Mockito.when(demoRepository.findByUinRefIdAndLangCode("12345", "EN"));
//		Map<String, List<IdentityInfoDTO>> demoEntity = demoAuthServiceImpl.getDemoEntity("12345");
//		System.out.println(demoEntity);
//	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidgetDemoStatuswithException() throws IdAuthenticationBusinessException {
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		DemoAuthServiceImpl demoAuthService = Mockito.mock(DemoAuthServiceImpl.class);
		Mockito.when(
				demoAuthService.getDemoStatus(Mockito.any(AuthRequestDTO.class), Mockito.anyString(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException());
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		demoAuthService.getDemoStatus(authRequestDTO, "", idInfo);
	}

	@Test
	public void TestValidgetDemoStatus()
			throws IdAuthenticationBusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IdAuthenticationDaoException {
		ReflectionTestUtils.setField(demoAuthServiceImpl, "idInfoHelper", idInfoHelper);
		Map<String, List<IdentityInfoDTO>> entityInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO infoDTO = new IdentityInfoDTO();
		infoDTO.setLanguage("FR");
		infoDTO.setValue("Ibrahim");
		identityInfoList.add(infoDTO);
		entityInfo.put("firstName", identityInfoList);
		Mockito.when(idInfoService.getIdInfo(Mockito.anyMap())).thenReturn(entityInfo);
		AuthRequestDTO authRequestDTO = generateData();
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		AuthStatusInfo authStatusInfovalue = new AuthStatusInfo();
		authStatusInfovalue.setStatus(false);
		Mockito.when(idInfoHelper.buildStatusInfo(Mockito.anyBoolean(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(authStatusInfovalue);
		AuthStatusInfo authStatusInfo = demoAuthServiceImpl.getDemoStatus(authRequestDTO, "121212", idInfo);
		assertTrue(!authStatusInfo.isStatus());
	}

	private AuthRequestDTO generateData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAddress(false);
		authType.setFullAddress(false);
		authType.setBio(false);
		authType.setOtp(false);
		authType.setPersonalIdentity(true);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setId("IDA");
		authRequestDTO.setIdvId("426789089018");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		/* Name */
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO nameInfoDTO = new IdentityInfoDTO();
		nameInfoDTO.setLanguage("FR");
		nameInfoDTO.setValue("Ibrahim Ibn Ali");
		nameList.add(nameInfoDTO);
		identityDTO.setName(nameList);
		requestDTO.setIdentity(identityDTO);
		authRequestDTO.setIdvIdType("D");
		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
		authSecureDTO.setPublicKeyCert("1234567890");
		authSecureDTO.setSessionKey("1234567890");
		authRequestDTO.setKey(authSecureDTO);
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("personalIdentity");
		matchInfo.setLanguage("FR");
		matchInfo.setMatchingStrategy(MatchingStrategyType.EXACT.getType());
		matchInfo.setMatchingThreshold(100);
		List<MatchInfo> matchInfolist = new ArrayList<>();
		matchInfolist.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfolist);
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setPinInfo(new ArrayList<>());
		authRequestDTO.setReqHmac("string");
		authRequestDTO.setReqTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		request.setIdentity(identityDTO);
		authRequestDTO.setRequest(request);
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
		return authRequestDTO;

	}

//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void TestInValidgetDemoStatus()
//			throws IdAuthenticationBusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException, IdAuthenticationDaoException {
//		ReflectionTestUtils.setField(demoAuthServiceImpl, "demoMatcher", demomatcher);
//		Mockito.when(idInfoService.getIdInfo(Mockito.anyString())).thenReturn(null);
//		AuthRequestDTO authRequestDTO = generateData();
//		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
//		list.add(new IdentityInfoDTO("en", "mosip"));
//		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
//		idInfo.put("name", list);
//		idInfo.put("email", list);
//		idInfo.put("phone", list);
//		AuthStatusInfo authStatusInfo = demoAuthServiceImpl.getDemoStatus(authRequestDTO, "121212",idInfo);
//	}

}
