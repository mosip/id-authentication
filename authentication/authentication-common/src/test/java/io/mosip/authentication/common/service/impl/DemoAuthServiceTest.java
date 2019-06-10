package io.mosip.authentication.common.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DOBType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(IDAMappingConfig.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingConfig.class,
		IDAMappingFactory.class })
public class DemoAuthServiceTest {

	@Autowired
	private Environment environment;

	@InjectMocks
	private DemoAuthServiceImpl demoAuthServiceImpl;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@InjectMocks
	private MatchInputBuilder matchInputBuilder;

	@Mock
	private IdService<?> idInfoService;

	@Mock
	private MasterDataManager masterDataManager;

	@Autowired
	private IDAMappingConfig idaMappingConfig;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idaMappingConfig);
		ReflectionTestUtils.setField(demoAuthServiceImpl, "environment", environment);
		ReflectionTestUtils.setField(demoAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(demoAuthServiceImpl, "idaMappingConfig", idaMappingConfig);
		ReflectionTestUtils.setField(demoAuthServiceImpl, "matchInputBuilder", matchInputBuilder);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fadMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
		Map<String, Object> matchProperties = new HashMap<>();
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR, MatchingStrategyType.PARTIAL.getType(),
				60, matchProperties, "fre"));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertNotNull(listMatchInputsActual);
//		assertEquals(listMatchInputsExp, listMatchInputsActual);
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void adMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("IDA");

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		IdentityDTO identityDTO = new IdentityDTO();
		List<IdentityInfoDTO> infoList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue(
				"exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3 Casablanca Tanger-Tétouan-Al Hoceima Fès-Meknès");
		infoList.add(identityInfoDTO);
		identityDTO.setFullAddress(infoList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		IdentityInfoDTO address1 = new IdentityInfoDTO();
		address1.setLanguage("fre");
		address1.setValue("exemple d'adresse ligne 1 ");
		IdentityInfoDTO address2 = new IdentityInfoDTO();
		address2.setLanguage("fre");
		address2.setValue("exemple d'adresse ligne 2");
		IdentityInfoDTO address3 = new IdentityInfoDTO();
		address3.setLanguage("fre");
		address3.setValue("exemple d'adresse ligne 3");
		IdentityInfoDTO location1 = new IdentityInfoDTO();
		location1.setLanguage("fre");
		location1.setValue("Casablanca");
		IdentityInfoDTO location2 = new IdentityInfoDTO();
		location2.setLanguage("fre");
		location2.setValue("Tanger-Tétouan-Al Hoceima");
		IdentityInfoDTO location3 = new IdentityInfoDTO();
		location3.setLanguage("fre");
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
		request.setDemographics(identity);
		authRequestDTO.setRequest(request);
		authRequestDTO.setIndividualId("426789089018");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		Map<String, Object> matchProperties = new HashMap<>();
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE1,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE2,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.ADDR_LINE3,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION1,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION2,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.LOCATION3,
				MatchingStrategyType.EXACT.getType(), 100, matchProperties, "fre"));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.PINCODE_SEC, MatchingStrategyType.EXACT.getType(), 100));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertNotNull(listMatchInputsActual);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void pidMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("IDA");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();

		/* Name */
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO nameInfoDTO = new IdentityInfoDTO();
		nameInfoDTO.setLanguage("fre");
		nameInfoDTO.setValue("Ibrahim Ibn Ali");
		nameList.add(nameInfoDTO);
		identityDTO.setName(nameList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		request.setDemographics(identityDTO);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(request);
		authRequestDTO.setIndividualId("426789089018");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		AuthType demoAuthType = null;
		Map<String, Object> matchProperties = new HashMap<>();
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.NAME, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.AGE, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.DOB, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.EMAIL, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.PHONE, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		listMatchInputsExp.add(new MatchInput(demoAuthType, DemoMatchType.GENDER, MatchingStrategyType.EXACT.getType(),
				100, matchProperties, "fre"));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO);
		assertNotNull(listMatchInputsActual);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void constructMatchInputTestNoFad() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequest.setRequestedAuth(authTypeDTO);
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
		Mockito.when(demoAuthService.authenticate(Mockito.any(AuthRequestDTO.class), Mockito.anyString(), Mockito.any(),
				Mockito.anyString())).thenThrow(new IdAuthenticationBusinessException());
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		demoAuthService.authenticate(authRequestDTO, "", idInfo, "123456");
	}

	@Test
	public void TestValidgetDemoStatus()
			throws IdAuthenticationBusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IdAuthenticationDaoException {
		ReflectionTestUtils.setField(demoAuthServiceImpl, "idInfoHelper", idInfoHelper);
		Map<String, List<IdentityInfoDTO>> entityInfo = new HashMap<>();
		List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
		IdentityInfoDTO infoDTO = new IdentityInfoDTO();
		infoDTO.setLanguage("fre");
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
		AuthStatusInfo authStatusInfo = demoAuthServiceImpl.authenticate(authRequestDTO, "121212", idInfo, "123456");
		assertTrue(!authStatusInfo.isStatus());
	}

	private AuthRequestDTO generateData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthRequestDTO authRequest = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequest.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("IDA");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		/* Name */
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO nameInfoDTO = new IdentityInfoDTO();
		nameInfoDTO.setLanguage("fre");
		nameInfoDTO.setValue("Ibrahim Ibn Ali");
		nameList.add(nameInfoDTO);
		identityDTO.setName(nameList);
		requestDTO.setDemographics(identityDTO);
		authRequestDTO.setRequestTime("2018-10-30T11:02:22.778+0000");
		RequestDTO request = new RequestDTO();
		request.setDemographics(identityDTO);
		authRequestDTO.setRequest(request);
		authRequestDTO.setIndividualId("426789089018");
		authRequestDTO.setTransactionID("1234567890");
		return authRequestDTO;

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidgetDemoStatus() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = generateData();
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		demoAuthServiceImpl.authenticate(authRequestDTO, "121212", idInfo, "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestdemoEntityisNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = null;
		String uin = "";
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoAuthServiceImpl.authenticate(authRequestDTO, uin, demoEntity, "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestDemoAuthStatus() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);

		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Ibrahim";
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue(value);
		nameList.add(identityInfoDTO);
		identity.setName(nameList);
		requestDTO.setDemographics(identity);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		authRequestDTO.setIndividualId("426789089018");
		Map<String, List<IdentityInfoDTO>> demoIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("fre");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		demoIdentity.put("firstName", identityList);
		String uin = "274390482564";
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.primary-language", "fre");
		mockenv.setProperty("mosip.secondary-language", "ara");
		mockenv.setProperty("mosip.supported-languages", "eng,ara,fre");
		ReflectionTestUtils.setField(idInfoHelper, "environment", mockenv);
		Mockito.when(masterDataManager.fetchTitles()).thenReturn(createFetcher());
		demoAuthServiceImpl.authenticate(authRequestDTO, uin, demoIdentity, "123456");
	}

	@Test
	public void TestValidDemographicData() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String individualId = "274390482564";
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fra");
		identityInfoDTO.setValue("Dinesh");
		nameList.add(identityInfoDTO);
		demographics.setName(nameList);
		request.setDemographics(demographics);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("fullName", nameList);
		Set<String> valueSet = new HashSet<>();
		valueSet.add("fra");
		AuthStatusInfo authenticate = demoAuthServiceImpl.authenticate(authRequestDTO, individualId, demoEntity,
				"1234567890");
		assertTrue(authenticate.isStatus());
	}

	@Test
	public void TestconstructDemoError() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String individualId = "274390482564";
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		demographics.setPhoneNumber("0000000000");
		demographics.setEmailId("abc@test.com");
		demographics.setDob("11/09/1898");
		List<IdentityInfoDTO> dobType = new ArrayList<>();
		IdentityInfoDTO reqIdentityInfodto = new IdentityInfoDTO();
		reqIdentityInfodto.setValue(DOBType.VERIFIED.name());
		demographics.setDobType(dobType);
		demographics.setAge("20");
		request.setDemographics(demographics);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> phoneList = new ArrayList<>();
		List<IdentityInfoDTO> mailList = new ArrayList<>();
		List<IdentityInfoDTO> dobList = new ArrayList<>();
		IdentityInfoDTO phonedto = new IdentityInfoDTO();
		phonedto.setValue("0000000001");
		phoneList.add(phonedto);
		demoEntity.put("phone", phoneList);
		IdentityInfoDTO maildto = new IdentityInfoDTO();
		maildto.setValue("invalid");
		mailList.add(maildto);
		demoEntity.put("email", mailList);
		IdentityInfoDTO dobdto = new IdentityInfoDTO();
		dobdto.setValue("1990/09/11");
		dobList.add(dobdto);
		demoEntity.put("dateOfBirth", dobList);
		Set<String> valueSet = new HashSet<>();
		valueSet.add("fra");
		try {
			 demoAuthServiceImpl.authenticate(authRequestDTO, individualId, demoEntity,"1234567890");
		 }
		catch(IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestInValidLangDemographicData() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String individualId = "274390482564";
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue("Dinesh");
		nameList.add(identityInfoDTO);
		demographics.setName(nameList);
		request.setDemographics(demographics);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> nameListEntity = new ArrayList<>();
		IdentityInfoDTO identityInfoDTOEntity = new IdentityInfoDTO();
		identityInfoDTOEntity.setLanguage("fra");
		identityInfoDTOEntity.setValue("Dinesh1");
		nameListEntity.add(identityInfoDTOEntity);
		demoEntity.put("fullName", nameListEntity);
		try {
			 demoAuthServiceImpl.authenticate(authRequestDTO, individualId, demoEntity,"1234567890");
		 }
		catch(IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorCode(), ex.getErrorCode());
		}
	}
		
	
	
	
	@Test
	public void TestInValidDemographicData() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = getTestData();
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fra");
		identityInfoDTO.setValue("Dinesh1");
		demoEntity.put("fullName", nameList);
		String individualId = "274390482564";
		demoAuthServiceImpl.authenticate(authRequestDTO, individualId, demoEntity,
				"1234567890");
		try {
			 demoAuthServiceImpl.authenticate(authRequestDTO, individualId, demoEntity,"1234567890");
		 }
		catch(IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.DEMO_MISSING.getErrorCode(), ex.getErrorCode());
		}
	}
	

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestcontstructMatchInputisNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = null;
		String uin = null;
		Map<String, List<IdentityInfoDTO>> demoEntity = null;
		demoAuthServiceImpl.authenticate(authRequestDTO, uin, demoEntity, "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestcontstructMatchInputisEmpty() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String uin = null;
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoAuthServiceImpl.authenticate(authRequestDTO, uin, demoEntity, "123456");
	}

	@Test
	public void TestgetDemoStatus() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(false);
		authTypeDTO.setDemo(true);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPin(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
//		authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Ibrahim";
		identityInfoDTO.setLanguage("fre");
		identityInfoDTO.setValue(value);
		nameList.add(identityInfoDTO);
		identity.setName(nameList);
		requestDTO.setDemographics(identity);
		authRequestDTO.setRequest(requestDTO);
		authRequestDTO.setIndividualId("426789089018");
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("name", nameList);
		demoAuthServiceImpl.authenticate(authRequestDTO, "274390482564", demoEntity, "123456");

	}

	private AuthRequestDTO getTestData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String individualId = "274390482564";
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage("fra");
		identityInfoDTO.setValue("Dinesh");
		nameList.add(identityInfoDTO);
		demographics.setName(nameList);
		request.setDemographics(demographics);
		authRequestDTO.setRequest(request);
		return authRequestDTO;
	}

	private Map<String, List<String>> createFetcher() {
		List<String> l = new ArrayList<>();
		l.add("Mr");
		l.add("Dr");
		l.add("Mrs");
		Map<String, List<String>> map = new HashMap<>();
		map.put("fra", l);
		return map;
	}

}
