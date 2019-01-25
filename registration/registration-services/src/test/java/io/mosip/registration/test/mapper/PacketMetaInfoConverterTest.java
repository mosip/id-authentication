package io.mosip.registration.test.mapper;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;

import ma.glasnost.orika.MapperFacade;

public class PacketMetaInfoConverterTest {
	
	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	
	@Test
	public void convertTest() throws JsonProcessingException, RegBaseCheckedException {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("officerLeftThumb", "yyqtyt-1gyu1210-1uu13hgziu1");
		hashMap.put("supervisorLeftThumb", "13113-1gyu1210-1uu13hgziu1");
		SessionContext.getInstance().setMapObject(new HashMap<>());
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, hashMap);
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightEye());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightIndex());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftIndex());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightThumb());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerIris());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerImage());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
		
		SessionContext.destroySession();
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void runtimeExceptionTest() {
		mapperFacade.convert(null, PacketMetaInfo.class, "packetMetaInfo");
	}

	@Test
	public void emptyObjectTest() {
		SessionContext.getInstance().setMapObject(new HashMap<>());
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME,
				new HashMap<>());
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setApplicantDocumentDTO(new ApplicantDocumentDTO());
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		demographicInfoDTO.setIdentity(new Identity());
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		registrationDTO.setDemographicDTO(demographicDTO);
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(new BiometricInfoDTO());
		biometricDTO.setIntroducerBiometricDTO(new BiometricInfoDTO());
		biometricDTO.setOperatorBiometricDTO(new BiometricInfoDTO());
		biometricDTO.setSupervisorBiometricDTO(new BiometricInfoDTO());
		registrationDTO.setBiometricDTO(biometricDTO);
		registrationDTO.setOsiDataDTO(new OSIDataDTO());
		registrationDTO.setRegistrationMetaDataDTO(new RegistrationMetaDataDTO());
		registrationDTO.setRegistrationId("2018782130000128122018103836");

		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightIndex());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightMiddle());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightRing());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightLittle());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftIndex());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftMiddle());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftRing());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftLittle());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftThumb());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightThumb());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerIris());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerImage());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getDocuments());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}

}
