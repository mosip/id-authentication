package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.BioMatcherUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.RegistryIDType;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import({ IDAMappingConfig.class, EnvUtil.class })
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioAuthServiceTest {

	private static final double THRESHOLD = IdAuthCommonConstants.DEFAULT_PARTIAL_MATCH_VALUE;

	@InjectMocks
	private BioAuthServiceImpl bioAuthServiceImpl;

	@Mock
	private BioMatcherUtil bioMatcherUtil;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@InjectMocks
	private MatchInputBuilder matchInputBuilder;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restBuilder;

	@Autowired
	EnvUtil environment;

	@Mock
	private CbeffUtil cbeffUtil;

	@Mock
	IBioApi fingerApi;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Before
	public void before() throws IDDataValidationException, RestServiceException {
		ReflectionTestUtils.setField(bioAuthServiceImpl, "matchInputBuilder", matchInputBuilder);
		ReflectionTestUtils.setField(bioAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(matchInputBuilder, "environment", environment);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "bioMatcherUtil", bioMatcherUtil);
		ReflectionTestUtils.setField(bioMatcherUtil, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoHelper, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new RestRequestDTO());
		when(restHelper.requestSync(Mockito.any())).thenReturn(null);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidateBioDetails() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Map<String, List<IdentityInfoDTO>> bioIdentity = null;
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestvalidateBioDetails() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Finger");
		dataDTO.setBioSubType("Left IndexFinger");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Finger_Left IndexFinger", individualBiometicsList);

		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_7", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_7", value);
		BIR bir = new BIR();
		bir.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FINGER));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.INDEX_FINGER.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("7");
		bdbInfo.setFormat(regIdType);
		bir.setBdbInfo(bdbInfo);
		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn(List.of(bir));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestvalidateBioDetails_Iris() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Iris");
		dataDTO.setBioSubType("Left");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Iris_Left", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("IRIS_Left_9", value);
		BIR bir = new BIR();
		bir.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.IRIS));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.LEFT.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("9");
		bdbInfo.setFormat(regIdType);
		bir.setBdbInfo(bdbInfo);

		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn(List.of(bir));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestvalidateBioDetails_Multi_Iris() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Iris");
		dataDTO.setBioSubType("Left");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);

		bioIdentityInfoDTO = new BioIdentityInfoDTO();
		dataDTO = new DataDTO();
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("");
		digitalId2.setMake("");
		digitalId2.setModel("");
		digitalId2.setType("");
		digitalId2.setDeviceProvider("");
		digitalId2.setDeviceProviderId("");
		digitalId2.setDateTime("");
		dataDTO.setDigitalId(digitalId2);
		dataDTO.setBioType("Iris");
		dataDTO.setBioSubType("Right");
		dataDTO.setBioValue(value);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);

		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Iris_UNKNOWN", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("IRIS_Left_9", value);
		cbeffValueMap.put("IRIS_Right_9", value);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.IRIS));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.LEFT.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("9");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);
		
		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestValidateBioAuthDetails() throws Exception {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setTransactionID("1234567890");
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue(
				"Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT");
		dataDTOFinger.setBioSubType("LEFT_INDEX");
		dataDTOFinger.setBioType("FMR");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTOFinger.setDigitalId(digitalId);
		fingerValue.setData(dataDTOFinger);
		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		DataDTO dataDTOFinger2 = new DataDTO();
		dataDTOFinger2.setBioValue(value);
		dataDTOFinger2.setBioSubType("LEFT_INDEX");
		dataDTOFinger2.setBioType("FMR");
		dataDTOFinger2.setPurpose("AUTH");
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("");
		digitalId2.setMake("");
		digitalId2.setModel("");
		digitalId2.setType("");
		digitalId2.setDeviceProvider("");
		digitalId2.setDeviceProviderId("");
		digitalId2.setDateTime("");
		dataDTOFinger2.setDigitalId(digitalId2);
		bioIdentityInfoDTO.setData(dataDTOFinger2);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		// Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestMatchImage() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		List<BioIdentityInfoDTO> bioIdentityInfoDTOList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO bioInfo = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		bioInfo.setDigitalId(digitalId);
		bioInfo.setBioType("FMR");
		// bioInfo.setDeviceInfo(deviceInfo);
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		bioInfo.setBioSubType("LEFT_INDEX");
		bioInfo.setBioValue(value);
		bioInfo.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(bioInfo);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(bioIdentityInfoDTOList);
		authRequestDTO.setRequest(request);
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		// bioIdentity.put("leftIndex", identityList);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		cbeffValueMap.put("FINGER_Left ThumbFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestMatchFingerPrintMantra() throws Exception {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		List<BioIdentityInfoDTO> bioIdentityInfoDTOList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO bioInfo = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		bioInfo.setDigitalId(digitalId);
		bioInfo.setBioType("FMR");
		bioInfo.setPurpose("AUTH");
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		bioInfo.setBioSubType("LEFT_INDEX");
		bioInfo.setBioValue(value);
		bioIdentityInfoDTO.setData(bioInfo);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		RequestDTO request = new RequestDTO();
		request.setBiometrics(bioIdentityInfoDTOList);
		authRequestDTO.setRequest(request);
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		cbeffValueMap.put("FINGER_Left ThumbFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestMatchFingerPrintCogent() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		// authRequestDTO.setReqHmac("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		List<BioIdentityInfoDTO> bioIdentityInfoDTOList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO bioInfo = new DataDTO();
		bioInfo.setPurpose("AUTH");
		bioInfo.setBioType("FMR");
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		bioInfo.setBioSubType("LEFT_INDEX");
		bioInfo.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		bioInfo.setDigitalId(digitalId);
		bioIdentityInfoDTO.setData(bioInfo);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(bioIdentityInfoDTOList);
		authRequestDTO.setRequest(request);
		// Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestvalidateBioDetailsMulti() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		List<BioIdentityInfoDTO> bioIdentityInfoDTOList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FMR");
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		bioInfo.setBioSubType("LEFT_INDEX");
		bioInfo.setBioValue(value);
		bioIdentityInfoDTO.setData(bioInfo);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		DataDTO dataDTO = new DataDTO();
		bioIdentityInfoDTO = new BioIdentityInfoDTO();
		dataDTO = new DataDTO();
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_THUMB");
		dataDTO.setBioValue(value);
		bioIdentityInfoDTO.setData(dataDTO);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(bioIdentityInfoDTOList);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		identityInfoDTOList1.setLanguage("ara");
		identityInfoDTOList1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		List<IdentityInfoDTO> identityLists = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityLists.add(identityInfoDTOList1);

		bioIdentity.put("individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_2", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertFalse(validateBioDetails.isStatus());
	}

	@Test
	public void TestIrisMultiMatch() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");
		List<BioIdentityInfoDTO> bioIdentityInfoDTOList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FMR");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		bioInfo.setDigitalId(digitalId);
		bioIdentityInfoDTO.setData(bioInfo);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		bioInfo.setBioSubType("LEFT_INDEX");
		bioInfo.setBioValue(value);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		String value1 = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		DataDTO dataDTO = new DataDTO();
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioValue(value);
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("");
		digitalId2.setMake("");
		digitalId2.setModel("");
		digitalId2.setType("");
		digitalId2.setDeviceProvider("");
		digitalId2.setDeviceProviderId("");
		digitalId2.setDateTime("");
		dataDTO.setDigitalId(digitalId2);
		bioIdentityInfoDTO.setData(dataDTO);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		dataDTO.setBioType("FMR");
		dataDTO.setBioSubType("LEFT_THUMB");
		dataDTO.setBioValue(value1);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		bioIdentityInfoDTOList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(bioIdentityInfoDTOList);

		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		bioIdentity.put("leftEye", identityList);
		bioIdentity.put("rightEye", identityList);
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, String> cbeffValueMap = new HashMap<>();
		cbeffValueMap.put("IRIS_Left_9", value);
		cbeffValueMap.put("IRIS_Right_9", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		authRequestDTO.setRequest(request);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestValidBioAuth() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		String individualId = "274390482564";
		authRequestDTO.setIndividualId(individualId);
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> biometricsList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO datadto = new DataDTO();
		datadto.setBioType("Finger");
		datadto.setBioValue(value);
		datadto.setBioSubType("Left IndexFinger");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		datadto.setDigitalId(digitalId);
		datadto.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(datadto);
		biometricsList.add(bioIdentityInfoDTO);
		request.setBiometrics(biometricsList);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		IdentityInfoDTO identityInfoDTOList1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		identityInfoDTOList1.setLanguage("ara");
		String value1 = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		identityInfoDTOList1.setValue(value1);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		List<IdentityInfoDTO> identityLists = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityLists.add(identityInfoDTOList1);
		bioIdentity.put("leftIndex", identityList);
		bioIdentity.put("rightIndex", identityLists);

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		;
		bioIdentity.put("Finger_Left IndexFinger", individualBiometicsList);

		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_7", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_7", value);
		cbeffValueMap.put("FINGER_Left IndexFinger_7", value1);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FINGER));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.INDEX_FINGER.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("7");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);

		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo authenticate = bioAuthServiceImpl.authenticate(authRequestDTO, individualId, bioIdentity,
				"1234567890");
		assertTrue(authenticate.isStatus());
	}

	@Test
	public void TestvalidateBioDetails2() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Finger");
		dataDTO.setBioSubType("LEFT_INDEX");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", identityList);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_7", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_7", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
	}

	@Test
	public void TestvalidateBioDetails_IrisUnknown() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Iris");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(value);
		dataDTO.setPurpose("AUTH");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Iris_UNKNOWN", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("IRIS_Left_9", value);

		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.IRIS));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.LEFT.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("9");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);

		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestvalidateBioDetails_FGRUnknown() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		// authRequestDTO.setVer("1.0");

		List<BioIdentityInfoDTO> leftIndexList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Finger");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		leftIndexList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(leftIndexList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("ara");
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Finger_Left IndexFinger", individualBiometicsList);

		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FINGER_Left IndexFinger_7", new SimpleEntry<>("leftIndex", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_7", value);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FINGER));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("7");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);

		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestValidFaceAuthentication() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> faceList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("FACE");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		faceList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(faceList);
		authRequestDTO.setRequest(request);

		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage(null);
		identityInfoDTO1.setValue(value);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Face", individualBiometicsList);

		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FACE__8", new SimpleEntry<>("face", identityList));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FACE__8", value);
		BIR bir = new BIR();
		bir.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FACE));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("8");
		bdbInfo.setFormat(regIdType);
		bir.setBdbInfo(bdbInfo);

		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn(List.of(bir));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		try {
			bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		}

		catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestInvalidFaceDetails() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> faceList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("Face");
		dataDTO.setBioValue(value);
		dataDTO.setPurpose("AUTH");
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		bioIdentityInfoDTO.setData(dataDTO);
		faceList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(faceList);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		bioIdentity.put("individualBiometrics", null);
		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FACE__8", new SimpleEntry<>("face", null));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FACE__8", value);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);
		try {
			bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		}

		catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestFaceDetailsMatched() throws Exception {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("2812936903");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> faceList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("FACE");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		faceList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(faceList);
		authRequestDTO.setRequest(request);
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage(null);
		identityInfoDTO1.setValue(value + "dGVzdA==");
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Face", individualBiometicsList);

		Map<String, Entry<String, List<IdentityInfoDTO>>> map = new HashMap<>();
		map.put("FACE__8", new SimpleEntry<>("face", null));
		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FACE__8", value);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FACE));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("8");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);
		
		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		try {
			bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		}

		catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), ex.getErrorCode());
		}
	}

	@Test
	public void TestMultiFingerPrint() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("2812936903");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> fingerList = new ArrayList<>();
		BioIdentityInfoDTO leftIndexdto = new BioIdentityInfoDTO();
		DataDTO leftIndexdatadto = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		leftIndexdatadto.setBioType(BioAuthType.FGR_MIN.getType());
		leftIndexdatadto.setBioSubType("Left IndexFinger");
		leftIndexdatadto.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		leftIndexdatadto.setDigitalId(digitalId);
		leftIndexdatadto.setPurpose("AUTH");
		leftIndexdto.setData(leftIndexdatadto);
		String rightValue = "Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNYCJAMS4Q4B7ALUBQ4BnAMK2ZICmAKrLZIBvAPAwXUBUAOa2ZIBbAPg5XYDOAOzCZEDSAJnOXYBfARghZICWAF31ZEBbAFIfZIDuARrEXUBCAE0eZEBsADygZICDAVkNG4DXAU+4L4AcAUEtB0B/AMmzPEB4AN4zXYCQAO+tXYC4ALVTXYBiAKQnZIBoAPguXYCUAQ6kZEB4AQ8oZEA8AOe0ZIBGAQszZIBPASEhZID3AIPVZIDkASq6ZEDKAEhpZEDtAFzdZEC1AVUlSUD0AFJgZEB0AAwHZICBAL/GQ4CoANBHZICJAJ7sZIB9APWmXUCGAIj7ZIDJAKzHXYBeAI+sZICLAHX9ZIDlANDKZEC1AGl0ZIDHAGNoZEA2ASQqUEB/AD8QZICHADeXZECfAVYQPEAxAT4cQ0A+AVIOQwAA";
		DataDTO rightIndexdto = new DataDTO();
		rightIndexdto.setPurpose("AUTH");
		rightIndexdto.setBioType(BioAuthType.FGR_MIN.getType());
		rightIndexdto.setBioSubType("Right IndexFinger");
		rightIndexdto.setBioValue(rightValue);
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("");
		digitalId2.setMake("");
		digitalId2.setModel("");
		digitalId2.setType("");
		digitalId2.setDeviceProvider("");
		digitalId2.setDeviceProviderId("");
		digitalId2.setDateTime("");
		rightIndexdto.setDigitalId(digitalId2);
		rightIndexdto.setPurpose("AUTH");

		BioIdentityInfoDTO rightIndexdtovalue = new BioIdentityInfoDTO();
		rightIndexdtovalue.setData(rightIndexdto);
		fingerList.add(leftIndexdto);
		fingerList.add(rightIndexdtovalue);
		request.setBiometrics(fingerList);
		authRequestDTO.setRequest(request);
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage(null);
		identityInfoDTO1.setValue(value);
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage(null);
		identityInfoDTO2.setValue(rightValue);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityList.add(identityInfoDTO2);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Finger_UNKNOWN", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		cbeffValueMap.put("FINGER_Right IndexFinger_2", rightValue);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FINGER));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("2");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);
		
		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn(List.of(birType));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestMultiFingerPrintwith3Fingers() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("2812936903");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> fingerList = new ArrayList<>();

		BioIdentityInfoDTO leftIndexdto = new BioIdentityInfoDTO();
		DataDTO leftIndexdatadto = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		leftIndexdatadto.setBioType(BioAuthType.FGR_MIN.getType());
		leftIndexdatadto.setBioSubType("LEFT_INDEX");
		leftIndexdatadto.setBioValue(value);
		leftIndexdto.setData(leftIndexdatadto);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		leftIndexdatadto.setDigitalId(digitalId);
		leftIndexdatadto.setPurpose("AUTH");

		DataDTO rightIndexdto = new DataDTO();
		String rightValue = "Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNYCJAMS4Q4B7ALUBQ4BnAMK2ZICmAKrLZIBvAPAwXUBUAOa2ZIBbAPg5XYDOAOzCZEDSAJnOXYBfARghZICWAF31ZEBbAFIfZIDuARrEXUBCAE0eZEBsADygZICDAVkNG4DXAU+4L4AcAUEtB0B/AMmzPEB4AN4zXYCQAO+tXYC4ALVTXYBiAKQnZIBoAPguXYCUAQ6kZEB4AQ8oZEA8AOe0ZIBGAQszZIBPASEhZID3AIPVZIDkASq6ZEDKAEhpZEDtAFzdZEC1AVUlSUD0AFJgZEB0AAwHZICBAL/GQ4CoANBHZICJAJ7sZIB9APWmXUCGAIj7ZIDJAKzHXYBeAI+sZICLAHX9ZIDlANDKZEC1AGl0ZIDHAGNoZEA2ASQqUEB/AD8QZICHADeXZECfAVYQPEAxAT4cQ0A+AVIOQwAA";
		rightIndexdto.setBioType(BioAuthType.FGR_MIN.getType());
		rightIndexdto.setBioSubType("Right IndexFinger");
		rightIndexdto.setBioValue(rightValue);
		DigitalId digitalId2 = new DigitalId();
		digitalId2.setSerialNo("");
		digitalId2.setMake("");
		digitalId2.setModel("");
		digitalId2.setType("");
		digitalId2.setDeviceProvider("");
		digitalId2.setDeviceProviderId("");
		digitalId2.setDateTime("");
		rightIndexdto.setDigitalId(digitalId2);
		rightIndexdto.setPurpose("AUTH");
		BioIdentityInfoDTO rightIndexdtovalue = new BioIdentityInfoDTO();
		rightIndexdtovalue.setData(rightIndexdto);

		DataDTO leftmiddledto = new DataDTO();
		String leftmiddle = "Rk1SACAyMAAAAAEyAAABPAFiAMUAxQEAAAAoLkC8AOaiZEC3AQEoXYCkAQ4aZECLAM80V0CAAP0iZEDXAQQmXUDkAN6pZEBvAOMyV4DqAQAmPEDwAPanV0CpATUFZEDVATYHUEB2AI0nZEDSAVV+PECGAGumZEDDAGDyV4DCAOkzXUCVANMnV0CoAMWkV0ChALoqV0CGAQccZECMARMVZIDGALe/V4DFASQNZEDtAOOrZICUASkJZECYAJQXXUD5AL/FUED0AJ7NZEDfAVYAB0BXAVJ6ZEDCAPKlXYCHAOgpXUDNAQEnXUDZAN06ZEC9ALy0XYCHAMGwV0B0ANeyUEDYARgXV0DWALe/ZEBnAQcXZEB+ATECZECuAIv2XYB4AUsAZEBZAUUFZECcAGMSQwAA";
		leftmiddledto.setBioType(BioAuthType.FGR_MIN.getType());
		leftmiddledto.setBioSubType("Left MiddleFinger");
		leftmiddledto.setBioValue(leftmiddle);
		DigitalId digitalId3 = new DigitalId();
		digitalId3.setSerialNo("");
		digitalId3.setMake("");
		digitalId3.setModel("");
		digitalId3.setType("");
		digitalId3.setDeviceProvider("");
		digitalId3.setDeviceProviderId("");
		digitalId3.setDateTime("");
		leftmiddledto.setDigitalId(digitalId3);
		BioIdentityInfoDTO leftMiddledtovalue = new BioIdentityInfoDTO();
		leftMiddledtovalue.setData(leftmiddledto);
		leftmiddledto.setPurpose("AUTH");

		fingerList.add(leftMiddledtovalue);
		fingerList.add(leftIndexdto);
		fingerList.add(rightIndexdtovalue);
		request.setBiometrics(fingerList);
		authRequestDTO.setRequest(request);
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage(null);
		identityInfoDTO1.setValue(value);
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage(null);
		identityInfoDTO2.setValue(rightValue);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);
		identityList.add(identityInfoDTO2);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("Finger_UNKNOWN", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FINGER_Left IndexFinger_2", value);
		cbeffValueMap.put("FINGER_Left MiddleFinger_2", leftmiddle);
		cbeffValueMap.put("FINGER_Right IndexFinger_2", rightValue);
		BIR birType = new BIR();
		birType.setBdb(individualBiometics.getBytes());
		BDBInfo bdbInfo = new BDBInfo();
		bdbInfo.setType(List.of(BiometricType.FINGER));
		bdbInfo.setSubtype(List.of(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()));
		RegistryIDType regIdType = new RegistryIDType();
		regIdType.setType("2");
		bdbInfo.setFormat(regIdType);
		birType.setBdbInfo(bdbInfo);
		
		Mockito.when(cbeffUtil.getBIRDataFromXMLType(Mockito.any(), Mockito.any()))
				.thenReturn((List.of(birType)));
		Mockito.when(bioMatcherUtil.match(Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(THRESHOLD);
		AuthStatusInfo validateBioDetails = bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		assertTrue(validateBioDetails.isStatus());
	}

	@Test
	public void TestconstructBioError() throws Exception {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdentityDTO identity = new IdentityDTO();
		RequestDTO request = new RequestDTO();
		request.setDemographics(identity);
		authRequestDTO.setIndividualId("2812936903");
		authRequestDTO.setId("mosip.identity.auth");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setRequestHMAC("1234567890");
		authRequestDTO.setTransactionID("1234567890");
		List<BioIdentityInfoDTO> faceList = new ArrayList<>();
		BioIdentityInfoDTO bioIdentityInfoDTO = new BioIdentityInfoDTO();
		DataDTO dataDTO = new DataDTO();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		dataDTO.setBioType("FID");
		dataDTO.setBioSubType("UNKNOWN");
		dataDTO.setBioValue(value);
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setType("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		digitalId.setDateTime("");
		dataDTO.setDigitalId(digitalId);
		dataDTO.setPurpose("AUTH");
		bioIdentityInfoDTO.setData(dataDTO);
		faceList.add(bioIdentityInfoDTO);
		request.setDemographics(identity);
		request.setBiometrics(faceList);
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> bioIdentity = new HashMap<>();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage(null);
		String entityValue = "Rk1SACAyMAAAAAFcAAABPAFiAMUAxQEAAAAoNYCJAMS4Q4B7ALUBQ4BnAMK2ZICmAKrLZIBvAPAwXUBUAOa2ZIBbAPg5XYDOAOzCZEDSAJnOXYBfARghZICWAF31ZEBbAFIfZIDuARrEXUBCAE0eZEBsADygZICDAVkNG4DXAU+4L4AcAUEtB0B/AMmzPEB4AN4zXYCQAO+tXYC4ALVTXYBiAKQnZIBoAPguXYCUAQ6kZEB4AQ8oZEA8AOe0ZIBGAQszZIBPASEhZID3AIPVZIDkASq6ZEDKAEhpZEDtAFzdZEC1AVUlSUD0AFJgZEB0AAwHZICBAL/GQ4CoANBHZICJAJ7sZIB9APWmXUCGAIj7ZIDJAKzHXYBeAI+sZICLAHX9ZIDlANDKZEC1AGl0ZIDHAGNoZEA2ASQqUEB/AD8QZICHADeXZECfAVYQPEAxAT4cQ0A+AVIOQwAA";
		identityInfoDTO1.setValue(entityValue);
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		identityList.add(identityInfoDTO1);

		String individualBiometics = "YWJjZGVmZ2g";
		List<IdentityInfoDTO> individualBiometicsList = new ArrayList<>();
		IdentityInfoDTO individualBiometicsListInfoDTO = new IdentityInfoDTO();
		individualBiometicsListInfoDTO.setValue(individualBiometics);
		individualBiometicsList.add(individualBiometicsListInfoDTO);
		bioIdentity.put("individualBiometrics", individualBiometicsList);

		Map<String, String> cbeffValueMap = new HashMap<String, String>();
		cbeffValueMap.put("FACE__8", entityValue);
		Mockito.when(cbeffUtil.getBDBBasedOnType(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(cbeffValueMap);

		try {
			bioAuthServiceImpl.authenticate(authRequestDTO, "", bioIdentity, "");
		}

		catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorCode(), ex.getErrorCode());
			assertEquals(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorMessage(), ex.getErrorText());
		}
	}

}