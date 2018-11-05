package io.mosip.authentication.service.impl.indauth.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;
import io.mosip.authentication.service.repository.DemoRepository;
import io.mosip.authentication.service.repository.LocationRepository;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DemoAuthServiceTest {

	@Autowired
	private Environment environment;

	@InjectMocks
	private DemoAuthServiceImpl demoAuthServiceImpl;

	private DemoMatcher demomatcher = new DemoMatcher();

	@Mock
	private DemoRepository demoRepository;

	@Mock
	private LocationRepository locRepository;

	@Before
	public void before() {
		ReflectionTestUtils.setField(demoAuthServiceImpl, "environment", environment);
	}

	@Test
	public void fadMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		AuthTypeDTO authType = new AuthTypeDTO();
//		authType.setAddress(false);
//		authType.setFullAddress(true);
//		authType.setBio(false);
//		authType.setOtp(false);
//		authType.setPersonalIdentity(false);
//		authType.setPin(false);
//		authRequestDTO.setAuthType(authType);
//		authRequestDTO.setId("IDA");
//		authRequestDTO.setIndId("426789089018");
//		RequestDTO requestDTO = new RequestDTO();
//		IdentityDTO identityDTO = new IdentityDTO();
//		List<IdentityInfoDTO> infoList = new ArrayList<>();
//		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
//		identityInfoDTO.setLanguage("EN");
//		identityInfoDTO.setValue("Test");
//		infoList.add(identityInfoDTO);
//		identityDTO.setFullAddress(infoList);
//		requestDTO.setIdentity(identityDTO);
//		authRequestDTO.setIndIdType("D");
//		fad.setMsPri(MatchingStrategyType.PARTIAL.getType());
//		demoDTO.setFad(fad);
//		pidData.setDemo(demoDTO);
//		authRequestDTO.setPii(pidData);
//
//		authRequestDTO.setAuthType(authType);
//		List<MatchInput> listMatchInputsExp = new ArrayList<>();
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_PRI, MatchingStrategyType.PARTIAL.getType(), 60));
//		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
//				AuthRequestDTO.class);
//		demoImplMethod.setAccessible(true);
//		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
//				authRequestDTO);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@Test
	public void adMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
//		PersonalAddressDTO ad = new PersonalAddressDTO();
//		PersonalIdentityDataDTO pidData = new PersonalIdentityDataDTO();
//		DemoDTO demoDTO = new DemoDTO();
//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		ad.setAddrLine1Pri("155 second street");
//		ad.setAddrLine2Pri("Anna Nagar");
//		ad.setAddrLine3Pri("Red Hills");
//		ad.setCityPri("Chennai");
//		ad.setStatePri("TamilNadu");
//		ad.setCountryPri("India");
//		ad.setPinCodePri("700105");
//		demoDTO.setAd(ad);
//		pidData.setDemo(demoDTO);
//		authRequestDTO.setPii(pidData);
//		AuthTypeDTO authType = new AuthTypeDTO();
//		authType.setAd(true);
//		authType.setFad(false);
//		authType.setBio(false);
//		authType.setOtp(false);
//		authType.setPi(false);
//		authType.setPin(false);
//		authRequestDTO.setAuthType(authType);
//		List<MatchInput> listMatchInputsExp = new ArrayList<>();
////		DemoMatchType demoMatchType = Mockito.mock(DemoMatchType.class);
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE1_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE2_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE3_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.CITY_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.STATE_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.COUNTRY_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.PINCODE_PRI, MatchingStrategyType.EXACT.getType(), 100));
//		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
//				AuthRequestDTO.class);
//		demoImplMethod.setAccessible(true);
//		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
//				authRequestDTO);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));

	}

	@Test
	public void pidMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
//		PersonalIdentityDTO pid = new PersonalIdentityDTO();
//		PersonalIdentityDataDTO pidData = new PersonalIdentityDataDTO();
//		DemoDTO demoDTO = new DemoDTO();
//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		pid.setAge(55);
//		pid.setDob("05/06/1963");
//		pid.setEmail("xxx@xyz.com");
//		pid.setGender("M");
//		pid.setPhone("9876543222");
//		pid.setNamePri("John");
//		pid.setMsPri(MatchingStrategyType.PARTIAL.getType());
//		demoDTO.setPi(pid);
//		pidData.setDemo(demoDTO);
//		authRequestDTO.setPii(pidData);
//		AuthTypeDTO authType = new AuthTypeDTO();
//		authType.setAd(false);
//		authType.setFad(false);
//		authType.setBio(false);
//		authType.setOtp(false);
//		authType.setPi(true);
//		authType.setPin(false);
//		authRequestDTO.setAuthType(authType);
//		List<MatchInput> listMatchInputsExp = new ArrayList<>();
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.NAME_PRI, MatchingStrategyType.PARTIAL.getType(), 60));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.AGE, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.DOB, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.EMAIL, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.MOBILE, MatchingStrategyType.EXACT.getType(), 100));
//		listMatchInputsExp.add(new MatchInput(DemoMatchType.GENDER, MatchingStrategyType.EXACT.getType(), 100));
//		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
//				AuthRequestDTO.class);
//		demoImplMethod.setAccessible(true);
//		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
//				authRequestDTO);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsActual.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsActual));
	}

	@Test
	public void constructMatchInputTest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
//		PersonalAddressDTO ad = new PersonalAddressDTO();
//		PersonalFullAddressDTO fad = new PersonalFullAddressDTO();
//		PersonalIdentityDTO pid = new PersonalIdentityDTO();
//		DemoDTO demoDTO = new DemoDTO();
//		PersonalIdentityDataDTO personalData = new PersonalIdentityDataDTO();
//		AuthRequestDTO authRequest = new AuthRequestDTO();
//		demoDTO.setAd(ad);
//		demoDTO.setFad(fad);
//		demoDTO.setPi(pid);
//		personalData.setDemo(demoDTO);
//		authRequest.setPii(personalData);
//		AuthTypeDTO authType = new AuthTypeDTO();
//		authType.setAd(false);
//		authType.setFad(true);
//		authType.setBio(false);
//		authType.setOtp(false);
//		authType.setPi(false);
//		authType.setPin(false);
//		authRequest.setAuthType(authType);
//		Method constructInputMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
//				AuthRequestDTO.class);
//		constructInputMethod.setAccessible(true);
//		List<MatchInput> listMatchInputsExp = new ArrayList<>();
//		List<MatchInput> listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl,
//				authRequest);
//		assertEquals(listMatchInputsExp.size(), listMatchInputsAct.size());
//		assertTrue(listMatchInputsExp.containsAll(listMatchInputsAct));
	}

	@Test
	public void constructMatchInputTestNoFad() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
////		PersonalAddressDTO ad = new PersonalAddressDTO();
////		PersonalFullAddressDTO fad = new PersonalFullAddressDTO();
////		PersonalIdentityDTO pid = new PersonalIdentityDTO();
////		DemoDTO demoDTO = new DemoDTO();
////		PersonalIdentityDataDTO personalData = new PersonalIdentityDataDTO();
////		pid.setDob("1980-09-09");
////		AuthRequestDTO authRequest = new AuthRequestDTO();
////		demoDTO.setAd(ad);
////		demoDTO.setFad(fad);
////		demoDTO.setPi(pid);
////		personalData.setDemo(demoDTO);
////		authRequest.setPii(personalData);
////		AuthTypeDTO authType = new AuthTypeDTO();
////		authType.setAd(false);
////		authType.setFad(false);
////		authType.setBio(false);
////		authType.setOtp(false);
////		authType.setPi(false);
////		authType.setPin(false);
////		authRequest.setAuthType(authType);
////		Method constructInputMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
////				AuthRequestDTO.class);
////		constructInputMethod.setAccessible(true);
////		List<MatchInput> listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl,
////				authRequest);
////		assertTrue(listMatchInputsAct.isEmpty());
//		
//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		PersonalIdentityDataDTO personalData1 = new PersonalIdentityDataDTO();
//		authRequestDTO.setPii(personalData1 );
//		personalData1.setDemo(new DemoDTO());
//		listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl,
//				authRequestDTO);
//		assertTrue(listMatchInputsAct.isEmpty());
	}

	@Ignore
	@Test
	public void getDemoEntityTest() throws IdAuthenticationBusinessException {
		// Mockito.when(demoRepository.findByUinRefIdAndLangCode("12345", "EN"));
		Map<String, List<IdentityInfoDTO>> demoEntity = demoAuthServiceImpl.getDemoEntity("12345");
		System.out.println(demoEntity);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidgetDemoStatuswithException() throws IdAuthenticationBusinessException {
		DemoAuthServiceImpl demoAuthService = Mockito.mock(DemoAuthServiceImpl.class);
		Mockito.when(demoAuthService.getDemoStatus(Mockito.any(AuthRequestDTO.class), Mockito.anyString()))
				.thenThrow(new IdAuthenticationBusinessException());
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		demoAuthService.getDemoStatus(authRequestDTO, "");
	}

	@Test
	public void TestValidgetDemoStatus() throws IdAuthenticationBusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		ReflectionTestUtils.setField(demoAuthServiceImpl, "demoMatcher", demomatcher);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setActive(true);
//		demoEntity.setFirstName("Dinesh");
//		demoEntity.setLastName("Karuppiah");
//		Mockito.when(demoAuthServiceImpl.getDemoEntity(Mockito.anyString(), Mockito.anyString()))
//				.thenReturn(demoEntity);
//		AuthRequestDTO authRequestDTO = generateData();
//		AuthStatusInfo authStatusInfo = demoAuthServiceImpl.getDemoStatus(authRequestDTO, "121212");
//		assertTrue(authStatusInfo.isStatus());
	}

//	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidgetDemoStatus() throws IdAuthenticationBusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		ReflectionTestUtils.setField(demoAuthServiceImpl, "demoMatcher", demomatcher);
//		Mockito.when(demoAuthServiceImpl.getDemoEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
//		AuthRequestDTO authRequestDTO = generateData();
//		AuthStatusInfo authStatusInfo = demoAuthServiceImpl.getDemoStatus(authRequestDTO, "121212");
	}

	@Test
	public void TestgetLocation() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
//		LocationEntity cityLocationEntity = new LocationEntity();
//		cityLocationEntity.setLangcode("EN");
//		cityLocationEntity.setCode("CHN");
//		cityLocationEntity.setName("CNENNAI");
//		cityLocationEntity.setHierarchylevelname("CITY");
//		cityLocationEntity.setParentloccode("TN");
//		
//		LocationEntity stateLocationEntity = new LocationEntity();
//		stateLocationEntity.setLangcode("EN");
//		stateLocationEntity.setCode("TN");
//		stateLocationEntity.setName("TAMILNADU");
//		stateLocationEntity.setHierarchylevelname("STATE");
//		stateLocationEntity.setParentloccode("IND");
//		
//		Mockito.when(locRepository.findByCodeAndIsActive("CHN", true))
//				.thenReturn(Optional.of(cityLocationEntity));
//		Mockito.when(locRepository.findByCodeAndIsActive("TN", true))
//				.thenReturn(Optional.of(stateLocationEntity));
//		Optional<String> optvalue = demoAuthServiceImpl.getLocation(LocationLevel.CITY, "CHN");
//		assertEquals(cityLocationEntity.getName(), optvalue.get());
//		optvalue = demoAuthServiceImpl.getLocation(LocationLevel.STATE, "CHN");
//		assertEquals(stateLocationEntity.getName(), optvalue.get());
//		assertFalse(demoAuthServiceImpl.getLocation(LocationLevel.STATE, "CHNNN").isPresent());
	}

//	private AuthRequestDTO generateData() {

//		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
//		AuthTypeDTO authType = new AuthTypeDTO();
//		authType.setAd(false);
//		authType.setBio(false);
//		authType.setFad(false);
//		authType.setOtp(false);
//		authType.setPi(true);
//		authType.setPin(false);
//		authRequestDTO.setAuthType(authType);
//		authRequestDTO.setData("Value1");
//		authRequestDTO.setHmac("HMAC");
//		authRequestDTO.setId("234567890124");
//		authRequestDTO.setIdType("D");
//		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
//		authSecureDTO.setPublicKeyCert("12345");
//		authSecureDTO.setSessionKey("SESSION");
//		authRequestDTO.setKey(authSecureDTO);
//		authRequestDTO.setMsaLicenseKey("LICENSE!@#$");
//		authRequestDTO.setMuaCode("1234567890");
//		DemoDTO demoDTO = new DemoDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setNamePri("dinesh karuppiah");
////		personalIdentityDTO.setMsPri("P");
////		personalIdentityDTO.setMtPri(50);
////		personalIdentityDTO.setDob("2001-07-16");
//		demoDTO.setPi(personalIdentityDTO);
////		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
////		personalFullAddressDTO.setAddrPri("#12, Rajaji Avenue, Sathya Nagar, East Mambalam, 600017");
////		personalFullAddressDTO.setMsPri("P");
////		personalFullAddressDTO.setMtPri(60);
////		demoDTO.setFad(personalFullAddressDTO);
//		PersonalIdentityDataDTO personalDataDTO = new PersonalIdentityDataDTO();
//		personalDataDTO.setDemo(demoDTO);
//		authRequestDTO.setPii(personalDataDTO);
//		authRequestDTO.setReqTime("2018-10-15T07:22:57.086+0000");
//		authRequestDTO.setSignature("test");
//		authRequestDTO.setTxnID("1234567890");
//		authRequestDTO.setVer("1.0");
//		return authRequestDTO;
//	}

}
