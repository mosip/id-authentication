package io.mosip.authentication.service.impl.indauth.service.bio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import io.mosip.kernel.cbeffutil.service.CbeffI;

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

	@Mock
	private CbeffI cbeffUtil;

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

	@Test
	public void TestvalidateBioDetails() throws Exception {
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

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
//		identityInfoDTO1.setValue(
//				"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI_Pgo8QklSIHhtbG5zPSJodHRwOi8vZG9jcy5vYXNpcy1vcGVuLm9yZy9iaWFzL25zL2JpYXNwYXRyb25mb3JtYXQtMS4wLyI - CiAgICA8VmVyc2lvbj4KICAgICAgICA8TWFqb3I - MTwvTWFqb3I - CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9WZXJzaW9uPgogICAgPENCRUZGVmVyc2lvbj4KICAgICAgICA8TWFqb3I - MTwvTWFqb3I - CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9DQkVGRlZlcnNpb24 - CiAgICA8QklSSW5mbz4KICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk - CiAgICA8L0JJUkluZm8 - CiAgICA8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk - CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI - MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU - MjwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzMSswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5GaW5nZXI8L1R5cGU - CiAgICAgICAgICAgIDxTdWJ0eXBlPlJpZ2h0IEluZGV4RmluZ2VyPC9TdWJ0eXBlPgogICAgICAgICAgICA8TGV2ZWw - UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U - RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8 - CiAgICAgICAgPEJEQj5SazFTQUNBeU1BQUFBQUZjQUFBQlBBRmlBTVVBeFFFQUFBQW9OVUN0QU12bFpJQ1JBT2xYWFlEQkFQQnFaRUNrQUtQV1hZRFpBTjlvWkVCekFNWFJTWUM2QUpOY1Y0RE1BSmRaWFVEcEFPOXpaSUJ5QVFKbVpFQ0RBSkM3WklEZkFRNTZYVUJlQVBaZ1YwQmhBSnd5VjBEVkFTc0JYVUVFQUovUlpFQlpBSXE2VUVDK0FWVjdTVUNqQU9oZVhVQ0hBTm5jVjBERkFLNWNYVURaQU5KZlhVQjNBTkJSU1VDZEFKcktYVUNKQUp2RVhVQ3hBUTkxWFVDakFSajdYWUN1QUlYZVY0Q1VBSUM0WklEUkFScjNYVURGQUg1alYwQm9BSkcxWkVEY0FTV0NVRURXQUhGZlpFQm5BU3A0U1VEaEFWZDFGRUNNQUx6V1YwREtBTHhZWFVDd0FLYmZWMERTQU9wc1pFQ0FBSzg5VjRDU0FRZHBaSUNwQUpQV1YwRG9BT1JpWFVDbUFJYXpWMEN4QVJ0N1hVQ0RBUnAwWkVEOUFOTlRaSURoQUl6U1pJQ1NBSFNqWklCWEFRcHhVRUNnQUdhSVhZQ0JBVUovWkFBQTwvQkRCPgogICAgPC9CSVI - CiAgICA8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk - CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI - MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU - MjwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzNCswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5GaW5nZXI8L1R5cGU - CiAgICAgICAgICAgIDxTdWJ0eXBlPkxlZnQgSW5kZXhGaW5nZXI8L1N1YnR5cGU - CiAgICAgICAgICAgIDxMZXZlbD5SYXc8L0xldmVsPgogICAgICAgICAgICA8UHVycG9zZT5FbnJvbGw8L1B1cnBvc2U - CiAgICAgICAgICAgIDxRdWFsaXR5Pjk1PC9RdWFsaXR5PgogICAgICAgIDwvQkRCSW5mbz4KICAgICAgICA8QkRCPlJrMVNBQ0F5TUFBQUFBRmNBQUFCUEFGaUFNVUF4UUVBQUFBb05VQjlBTUYwVjRDQkFLQkJQRUMwQUw2OFpJQzRBS2pOWkVCaUFKdldYVUJQQU5QV05VRFNBSzdSVUlDMkFRSWZaRURKQVBNeFBFQnlBR3dQWFlDcEFSWVBaRUNmQUZqb1pFQ0dBRXY5WkVCRUFGbXRWMEJwQVVHTlhVQy9BVUVFU1VDVUFWSUVQRUMyQVZOeFBJQ2NBTFd1WklDdUFMbTNaRUNOQUpxeFEwQ1VBSTNHUTBDWEFQZ2hWMEJWQUtET1pFQmZBUHFIWFVCREFLZS9aSUI5QUczeFhVRFBBSWJaVUVCY0FHWWhaRUNJQVNnSFhZQkpBR0FuVjBEakFSNGpHMERLQVRxSklVQ0dBREdTWkVEU0FVWUdJVUF4QUQrblYwQ1hBSytvU1VCb0FMcjZRNENTQU91S1hVQ2lBSXZOWkVDOUFKelFaSUJOQUxiVFhVQkJBTDY4VjBDZUFIRFpaRUN3QUhQYVpFQlJBUHdIVUlCSEFIVzJYVURYQVJBVURVQzRBUzRIWkVEWEFTMENRMENZQURMNFpFQ3NBVXp1UEVCa0FDZ1JaQUFBPC9CREI - CiAgICA8L0JJUj4KICAgIDxCSVI - CiAgICAgICAgPEJJUkluZm8 - CiAgICAgICAgICAgIDxJbnRlZ3JpdHk - ZmFsc2U8L0ludGVncml0eT4KICAgICAgICA8L0JJUkluZm8 - CiAgICAgICAgPEJEQkluZm8 - CiAgICAgICAgICAgIDxGb3JtYXRPd25lcj4yNTc8L0Zvcm1hdE93bmVyPgogICAgICAgICAgICA8Rm9ybWF0VHlwZT45PC9Gb3JtYXRUeXBlPgogICAgICAgICAgICA8Q3JlYXRpb25EYXRlPjIwMTktMDEtMjlUMTk6MTE6MzMuNDM0KzA1OjMwPC9DcmVhdGlvbkRhdGU - CiAgICAgICAgICAgIDxUeXBlPklyaXM8L1R5cGU - CiAgICAgICAgICAgIDxTdWJ0eXBlPlJpZ2h0PC9TdWJ0eXBlPgogICAgICAgICAgICA8TGV2ZWw - UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U - RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8 - CiAgICAgICAgPEJEQj5SazFTQUNBeU1BQUFBQUZjQUFBQlBBRmlBTVVBeFFFQUFBQW9OVUN0QU12bFpJQ1JBT2xYWFlEQkFQQnFaRUNrQUtQV1hZRFpBTjlvWkVCekFNWFJTWUM2QUpOY1Y0RE1BSmRaWFVEcEFPOXpaSUJ5QVFKbVpFQ0RBSkM3WklEZkFRNTZYVUJlQVBaZ1YwQmhBSnd5VjBEVkFTc0JYVUVFQUovUlpFQlpBSXE2VUVDK0FWVjdTVUNqQU9oZVhVQ0hBTm5jVjBERkFLNWNYVURaQU5KZlhVQjNBTkJSU1VDZEFKcktYVUNKQUp2RVhVQ3hBUTkxWFVDakFSajdYWUN1QUlYZVY0Q1VBSUM0WklEUkFScjNYVURGQUg1alYwQm9BSkcxWkVEY0FTV0NVRURXQUhGZlpFQm5BU3A0U1VEaEFWZDFGRUNNQUx6V1YwREtBTHhZWFVDd0FLYmZWMERTQU9wc1pFQ0FBSzg5VjRDU0FRZHBaSUNwQUpQV1YwRG9BT1JpWFVDbUFJYXpWMEN4QVJ0N1hVQ0RBUnAwWkVEOUFOTlRaSURoQUl6U1pJQ1NBSFNqWklCWEFRcHhVRUNnQUdhSVhZQ0JBVUovWkFBQTwvQkRCPgogICAgPC9CSVI - CiAgICA8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk - CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI - MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU - OTwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzNCswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5JcmlzPC9UeXBlPgogICAgICAgICAgICA8U3VidHlwZT5MZWZ0PC9TdWJ0eXBlPgogICAgICAgICAgICA8TGV2ZWw - UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U - RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8 - CiAgICAgICAgPEJEQj5SazFTQUNBeU1BQUFBQUZjQUFBQlBBRmlBTVVBeFFFQUFBQW9OVUI5QU1GMFY0Q0JBS0JCUEVDMEFMNjhaSUM0QUtqTlpFQmlBSnZXWFVCUEFOUFdOVURTQUs3UlVJQzJBUUlmWkVESkFQTXhQRUJ5QUd3UFhZQ3BBUllQWkVDZkFGam9aRUNHQUV2OVpFQkVBRm10VjBCcEFVR05YVUMvQVVFRVNVQ1VBVklFUEVDMkFWTnhQSUNjQUxXdVpJQ3VBTG0zWkVDTkFKcXhRMENVQUkzR1EwQ1hBUGdoVjBCVkFLRE9aRUJmQVBxSFhVQkRBS2UvWklCOUFHM3hYVURQQUliWlVFQmNBR1loWkVDSUFTZ0hYWUJKQUdBblYwRGpBUjRqRzBES0FUcUpJVUNHQURHU1pFRFNBVVlHSVVBeEFEK25WMENYQUsrb1NVQm9BTHI2UTRDU0FPdUtYVUNpQUl2TlpFQzlBSnpRWklCTkFMYlRYVUJCQUw2OFYwQ2VBSERaWkVDd0FIUGFaRUJSQVB3SFVJQkhBSFcyWFVEWEFSQVVEVUM0QVM0SFpFRFhBUzBDUTBDWUFETDRaRUNzQVV6dVBFQmtBQ2dSWkFBQTwvQkRCPgogICAgPC9CSVI - CjwvQklSPgo");
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestValidateBioAuthDetails() throws Exception {
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
//		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestMatchImage() throws Exception {
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
//		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
//		bioIdentity.put("leftIndex", identityList);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);

		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestMatchFingerPrintMantra() throws Exception {
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
//		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
//		bioIdentity.put("leftIndex", identityList);
		String refId = "274390482564";
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestMatchFingerPrintCogent() throws Exception {
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
//		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
//		bioIdentity.put("leftIndex", identityList);
		String refId = "274390482564";

		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

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

	@Test
	public void TestvalidateBioMultiImage() throws Exception {
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
		bioIdentity.put("documents.individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		cbeffValueMap.put("FINGER_Right IndexFinger_2", value1);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		assertTrue(validateBioDetails.isStatus());

	}

	@Test
	public void TestIrisMatchCogent() throws Exception {
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
		bioIdentity.put("documents.individualBiometrics", identityList);
		String refId = "274390482564";
		Map<String, String> cbeffValueMap = new HashMap<>();
		cbeffValueMap.put("IRIS_Left_9", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);

		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestIrisMatchMorpho() throws Exception {
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
		bioIdentity.put("documents.individualBiometrics", identityList);
		String refId = "274390482564";
		Map<String, String> cbeffValueMap = new HashMap<>();
		cbeffValueMap.put("IRIS_Left_9", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

	@Test
	public void TestIrisMultiMatch() throws Exception {
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
		bioIdentity.put("documents.individualBiometrics", identityList);
		String refId = "274390482564";
		Map<String, String> cbeffValueMap = new HashMap<>();
		cbeffValueMap.put("IRIS_Left_9", value);
		cbeffValueMap.put("IRIS_Right_9", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.validateBioDetails(authRequestDTO, bioIdentity);
		System.err.println(validateBioDetails.isStatus());
		System.err.println(validateBioDetails.getErr());
	}

}
