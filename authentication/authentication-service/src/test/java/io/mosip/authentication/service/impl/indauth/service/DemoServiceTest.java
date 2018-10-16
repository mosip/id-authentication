package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import io.mosip.authentication.core.dto.indauth.AuthSecureDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;
import io.mosip.authentication.service.repository.DemoRepository;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DemoServiceTest {

	@Autowired
	private Environment environment;

	@InjectMocks
	private DemoAuthServiceImpl demoAuthServiceImpl;

	@Mock
	private DemoRepository demoRepository;

	@Before
	public void before() {
		ReflectionTestUtils.setField(demoAuthServiceImpl, "environment", environment);
	}

	@Test
	public void fadMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		PersonalFullAddressDTO fad = new PersonalFullAddressDTO();
		PersonalIdentityDataDTO pidData = new PersonalIdentityDataDTO();
		DemoDTO demoDTO = new DemoDTO();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		fad.setAddrPri("23 Bandra Road Mumbai India 809890");
		fad.setMsPri(MatchingStrategyType.PARTIAL.getType());
		demoDTO.setPersonalFullAddressDTO(fad);
		pidData.setDemoDTO(demoDTO);
		authRequestDTO.setPersonalDataDTO(pidData);
		List<MatchInput> listMatchInputs = new ArrayList<>();
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_PRI, MatchingStrategyType.PARTIAL.getType(), 60));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructFadMatchInput",
				AuthRequestDTO.class, List.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO, listMatchInputs);
		assertEquals(listMatchInputsExp, listMatchInputsActual);

	}

	@Test
	public void adMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		PersonalAddressDTO ad = new PersonalAddressDTO();
		PersonalIdentityDataDTO pidData = new PersonalIdentityDataDTO();
		DemoDTO demoDTO = new DemoDTO();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		ad.setAddrLine1Pri("155 second street");
		ad.setAddrLine2Pri("Anna Nagar");
		ad.setAddrLine3Pri("Red Hills");
		ad.setCountryPri("India");
		ad.setPinCodePri("700105");
		demoDTO.setPersonalAddressDTO(ad);
		pidData.setDemoDTO(demoDTO);
		authRequestDTO.setPersonalDataDTO(pidData);
		List<MatchInput> listMatchInputs = new ArrayList<>();
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE1_PRI, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE2_PRI, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.ADDR_LINE3_PRI, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.COUNTRY_PRI, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.PINCODE_PRI, MatchingStrategyType.EXACT.getType(), 100));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructAdMatchInput",
				AuthRequestDTO.class, List.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO, listMatchInputs);
		assertEquals(listMatchInputsExp, listMatchInputsActual);

	}

	@Test
	public void pidMatchInputtest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		PersonalIdentityDTO pid = new PersonalIdentityDTO();
		PersonalIdentityDataDTO pidData = new PersonalIdentityDataDTO();
		DemoDTO demoDTO = new DemoDTO();
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		pid.setAge(55);
		pid.setDob("05/06/1963");
		pid.setEmail("xxx@xyz.com");
		pid.setGender("M");
		pid.setPhone("9876543222");
		pid.setNamePri("John");
		pid.setMsPri(MatchingStrategyType.PARTIAL.getType());
		demoDTO.setPersonalIdentityDTO(pid);
		pidData.setDemoDTO(demoDTO);
		authRequestDTO.setPersonalDataDTO(pidData);
		List<MatchInput> listMatchInputs = new ArrayList<>();
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		listMatchInputsExp.add(new MatchInput(DemoMatchType.NAME_PRI, MatchingStrategyType.PARTIAL.getType(), 60));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.AGE, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.DOB, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.EMAIL, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.MOBILE, MatchingStrategyType.EXACT.getType(), 100));
		listMatchInputsExp.add(new MatchInput(DemoMatchType.GENDER, MatchingStrategyType.EXACT.getType(), 100));
		Method demoImplMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructPIDMatchInput",
				AuthRequestDTO.class, List.class);
		demoImplMethod.setAccessible(true);
		List<MatchInput> listMatchInputsActual = (List<MatchInput>) demoImplMethod.invoke(demoAuthServiceImpl,
				authRequestDTO, listMatchInputs);
		assertEquals(listMatchInputsExp, listMatchInputsActual);
	}

	@Test
	public void constructMatchInputTest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		PersonalAddressDTO ad = new PersonalAddressDTO();
		PersonalFullAddressDTO fad = new PersonalFullAddressDTO();
		PersonalIdentityDTO pid = new PersonalIdentityDTO();
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDataDTO personalData = new PersonalIdentityDataDTO();
		AuthRequestDTO authRequest = new AuthRequestDTO();
		demoDTO.setPersonalAddressDTO(ad);
		demoDTO.setPersonalFullAddressDTO(fad);
		demoDTO.setPersonalIdentityDTO(pid);
		personalData.setDemoDTO(demoDTO);
		authRequest.setPersonalDataDTO(personalData);
		Method constructInputMethod = DemoAuthServiceImpl.class.getDeclaredMethod("constructMatchInput",
				AuthRequestDTO.class);
		constructInputMethod.setAccessible(true);
		List<MatchInput> listMatchInputsExp = new ArrayList<>();
		List<MatchInput> listMatchInputsAct = (List<MatchInput>) constructInputMethod.invoke(demoAuthServiceImpl,
				authRequest);
		assertEquals(listMatchInputsExp, listMatchInputsAct);
	}

	@Test
	public void getDemoEntityTest() {
		// Mockito.when(demoRepository.findByUinRefIdAndLangCode("12345", "EN"));
		DemoEntity demoEntity = demoAuthServiceImpl.getDemoEntity("12345", "EN");
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

	@Ignore
	@Test
	public void TestValidgetDemoStatus() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = generateData();
		DemoEntity demoEntity = new DemoEntity();

		Mockito.when(demoAuthServiceImpl.getDemoEntity(Mockito.anyString(), Mockito.anyString()));
		demoAuthServiceImpl.getDemoStatus(authRequestDTO, "1323323");
	}

	private AuthRequestDTO generateData() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAd(false);
		authType.setBio(false);
		authType.setFad(true);
		authType.setOtp(false);
		authType.setPi(false);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setData("Value1");
		authRequestDTO.setHmac("HMAC");
		authRequestDTO.setId("234567890124");
		authRequestDTO.setIdType("D");
		AuthSecureDTO authSecureDTO = new AuthSecureDTO();
		authSecureDTO.setPublicKeyCert("12345");
		authSecureDTO.setSessionKey("SESSION");
		authRequestDTO.setKey(authSecureDTO);
		authRequestDTO.setMsaLicenseKey("LICENSE!@#$");
		authRequestDTO.setMuaCode("1234567890");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setLangPri("en");
		demoDTO.setLangSec("ar");
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setNamePri("dinesh karuppiah");
		personalIdentityDTO.setMsPri("P");
		personalIdentityDTO.setMtPri(50);
		personalIdentityDTO.setDob("2001-07-16");
		demoDTO.setPersonalIdentityDTO(personalIdentityDTO);
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		personalFullAddressDTO.setAddrPri("#12, Rajaji Avenue, Sathya Nagar, East Mambalam, 600017");
		personalFullAddressDTO.setMsPri("P");
		personalFullAddressDTO.setMtPri(60);
		demoDTO.setPersonalFullAddressDTO(personalFullAddressDTO);
		PersonalIdentityDataDTO personalDataDTO = new PersonalIdentityDataDTO();
		personalDataDTO.setDemoDTO(demoDTO);
		authRequestDTO.setPersonalDataDTO(personalDataDTO);
		authRequestDTO.setReqTime("2018-10-15T07:22:57.086+0000");
		authRequestDTO.setSignature("test");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
		return authRequestDTO;
	}

}
