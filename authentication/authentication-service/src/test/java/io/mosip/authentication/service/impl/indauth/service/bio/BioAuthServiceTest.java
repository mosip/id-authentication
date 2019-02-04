package io.mosip.authentication.service.impl.indauth.service.bio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.BiometricProviderFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.indauth.service.BioAuthServiceImpl;
import io.mosip.authentication.service.impl.iris.CogentIrisProvider;
import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(IDAMappingConfig.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioAuthServiceTest {

	@InjectMocks
	private BioAuthServiceImpl bioAuthServiceImpl;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private BiometricProviderFactory biometricProviderFactory;

	@InjectMocks
	private MantraFingerprintProvider mantraFingerprintProvider;

	@InjectMocks
	private CogentFingerprintProvider cogentFingerprintProvider;

	@InjectMocks
	private CogentIrisProvider cogentIrisProvider;

	@InjectMocks
	private MorphoIrisProvider morphoIrisProvider;

	@Autowired
	Environment environment;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Before
	public void before() {
		ReflectionTestUtils.setField(bioAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoHelper, "biometricProviderFactory", biometricProviderFactory);
		ReflectionTestUtils.setField(biometricProviderFactory, "mantraFingerprintProvider", mantraFingerprintProvider);
		ReflectionTestUtils.setField(biometricProviderFactory, "cogentFingerProvider", cogentFingerprintProvider);
		ReflectionTestUtils.setField(biometricProviderFactory, "cogentIrisProvider", cogentIrisProvider);
		ReflectionTestUtils.setField(biometricProviderFactory, "morphoIrisProvider", morphoIrisProvider);
		ReflectionTestUtils.setField(biometricProviderFactory, "environment", environment);
		ReflectionTestUtils.setField(cogentIrisProvider, "environment", environment);
		ReflectionTestUtils.setField(morphoIrisProvider, "environment", environment);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidateBioDetails() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Map<String, List<IdentityInfoDTO>> bioIdentity = null;

		bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
	}

	@Ignore
	@Test
	public void TestvalidateBioDetails() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("274390482564");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfo.setMatchingThreshold(60);
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftIndexList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);

		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		assertTrue(validateBioDetails.isStatus());
	}

	@Ignore
	@Test
	public void TestValidateBioAuthDetails() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("274390482564");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfo.setMatchingThreshold(60);
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftIndexList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);

		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Ignore
	@Test
	public void TestMatchImage() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("274390482564");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfo.setMatchingThreshold(60);
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrImg");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftIndexList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);

		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Ignore
	@Test
	public void TestMatchFingerPrintMantra() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("516283648960");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftIndexList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Ignore
	@Test
	public void TestMatchFingerPrintCogent() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("516283648960");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("cogent");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftIndexList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Ignore
	@Test
	public void TestvalidateBioDetailsMulti() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("274390482564");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfo.setMatchingThreshold(60);
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> fingerPrintList = new ArrayList<>();
		List<IdentityInfoDTO> fingerPrintLists = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		String value1 = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		identityInfoDTOList.setLanguage("ara");
		identityInfoDTOList.setValue(value1);
		fingerPrintList.add(identityInfoDTO);
		fingerPrintLists.add(identityInfoDTOList);
		identity.setLeftIndex(fingerPrintList);
		identity.setRightIndex(fingerPrintLists);
		identity.setLeftLittle(fingerPrintLists);
		identity.setRightRing(fingerPrintLists);
		identity.setRightMiddle(fingerPrintLists);
		identity.setLeftMiddle(fingerPrintLists);
		identity.setLeftRing(fingerPrintList);
		identity.setRightLittle(fingerPrintLists);
		identity.setRightThumb(fingerPrintList);
		identity.setLeftThumb(fingerPrintList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		identityInfoDTOList1.setLanguage("ara");
		identityInfoDTOList1.setValue(value1);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		List<IdentityInfoDTO> identityLists = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityLists.add(identityInfoDTOList1);
		bioIdentity.put("leftIndex", identityList);
		bioIdentity.put("rightIndex", identityLists);
		bioIdentity.put("leftLittle", identityList);
		bioIdentity.put("rightLittle", identityList);
		bioIdentity.put("leftMiddle", identityList);
		bioIdentity.put("rightMiddle", identityList);
		bioIdentity.put("leftRing", identityList);
		bioIdentity.put("leftTumb", identityList);
		bioIdentity.put("rightRing", identityList);
		bioIdentity.put("ringThumb", identityList);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		assertFalse(validateBioDetails.isStatus());

	}

	@Ignore
	@Test
	public void TestvalidateBioMultiImage() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("274390482564");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("fgrMin");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("mantra");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> fingerPrintList = new ArrayList<>();
		List<IdentityInfoDTO> fingerPrintLists = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		String value1 = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		identityInfoDTOList.setLanguage("ara");
		identityInfoDTOList.setValue(value1);
		fingerPrintList.add(identityInfoDTO);
		fingerPrintLists.add(identityInfoDTOList);
		identity.setLeftIndex(fingerPrintList);
		identity.setRightIndex(fingerPrintLists);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		identityInfoDTOList1.setLanguage("ara");
		identityInfoDTOList1.setValue(value1);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		List<IdentityInfoDTO> identityLists = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityLists.add(identityInfoDTOList1);
		bioIdentity.put("leftIndex", identityList);
		bioIdentity.put("rightIndex", identityLists);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		assertTrue(validateBioDetails.isStatus());

	}

	@Test
	public void TestIrisMatchCogent() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("516283648960");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("irisImg");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("morpho");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftEyeList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftEyeList.add(identityInfoDTO);
		identity.setLeftEye(leftEyeList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftEye", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestIrisMatchMorpho() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("516283648960");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("irisImg");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("morpho");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftEyeList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftEyeList.add(identityInfoDTO);
		identity.setLeftEye(leftEyeList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftEye", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestIrisMultiMatch() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(true);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIdvId("516283648960");
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setKey(new AuthSecureDTO());
		List<MatchInfo> matchInfoList = new ArrayList<>();
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setAuthType("bio");
		matchInfo.setMatchingStrategy(MatchingStrategyType.PARTIAL.getType());
		matchInfoList.add(matchInfo);
		authRequestDTO.setMatchInfo(matchInfoList);
		authRequestDTO.setTspID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		// authRequestDTO.setVer("1.0");
		List<BioInfo> bioInfoList = new ArrayList<>();
		BioInfo bioInfo = new BioInfo();
		bioInfo.setBioType("irisImg");
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setDeviceId("Test1");
		deviceInfo.setMake("cogent");
		deviceInfo.setModel("1.0");
		bioInfo.setDeviceInfo(deviceInfo);
		bioInfoList.add(bioInfo);
		authRequestDTO.setBioInfo(bioInfoList);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		List<IdentityInfoDTO> leftEyeList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTO.setLanguage("ara");
		identityInfoDTO.setValue(value);
		leftEyeList.add(identityInfoDTO);
		List<IdentityInfoDTO> rightEyeList = new ArrayList<>();
		rightEyeList.add(identityInfoDTO);
		identity.setLeftEye(leftEyeList);
		identity.setRightEye(rightEyeList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftEye", identityList);
		bioIdentity.put("rightEye", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

}
