package io.mosip.authentication.service.impl.indauth.service.bio;

import static org.junit.Assert.assertFalse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
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
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.BioAuthServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(IDAMappingConfig.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioAuthServiceTest {

	@InjectMocks
	private BioAuthServiceImpl bioAuthServiceImpl;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@Autowired
	Environment environment;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Before
	public void before() {
		ReflectionTestUtils.setField(bioAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidateBioDetails() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Map<String, List<IdentityInfoDTO>> bioIdentity = null;
		String refId = null;
		bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity, refId);
	}

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
		authRequestDTO.setMuaCode("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
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
		identityInfoDTO.setLanguage("fr");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("fr");
		identityInfoDTO1.setValue(Base64.getEncoder().encodeToString(value.getBytes()));
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("sample", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity, refId);
		assertFalse(validateBioDetails.isStatus());
	}

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
		authRequestDTO.setMuaCode("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setVer("1.0");
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
		identityInfoDTO.setLanguage("AR");
		identityInfoDTO.setValue(value);
		leftIndexList.add(identityInfoDTO);
		identity.setLeftIndex(leftIndexList);
		requestDTO.setIdentity(identity);
		authRequestDTO.setRequest(requestDTO);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("AR");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftIndex", identityList);
		String refId = "274390482564";
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity, refId);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());

	}

}
