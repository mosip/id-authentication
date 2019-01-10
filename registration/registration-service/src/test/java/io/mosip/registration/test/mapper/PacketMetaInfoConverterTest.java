package io.mosip.registration.test.mapper;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
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
	private static RegistrationDTO registrationDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		// RegistrationDTO
		registrationDTO = DataProvider.getPacketDTO();
	}
	
	//@Test
	public void convertTest() throws JsonProcessingException {
		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightEye());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
//		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightSlap());
//		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftSlap());
//		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getThumbs());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerIris());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerImage());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void runtimeExceptionTest() {
		mapperFacade.convert(null, PacketMetaInfo.class, "packetMetaInfo");
	}

	@Test
	public void emptyObjectTest() {
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
//		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightSlap());
//		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftSlap());
//		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getThumbs());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerIris());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerImage());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getDocuments());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}

}
