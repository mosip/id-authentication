package io.mosip.registration.test.mapper;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.SelectionListDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;
import ma.glasnost.orika.MapperFacade;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
public class PacketMetaInfoConverterTest {

	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;

	@Test
	public void convertTest() throws Exception {
		HashMap<String, String> cbeffBIRS = new HashMap<>();
		cbeffBIRS.put("officerLeftThumb", "yyqtyt-1gyu1210-1uu13hgziu1");
		cbeffBIRS.put("supervisorLeftThumb", "13113-1gyu1210-1uu13hgziu1");
		cbeffBIRS.put("individualface", "eweew-1gyu1210-1uu13hgziu1");
		cbeffBIRS.put("individualexceptionface", "fgfgf-1gyu1210-1uu13hgziu1");
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, cbeffBIRS);
		sessionMap.put(RegistrationConstants.IS_Child, true);

		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		SelectionListDTO selectionListDTO = new SelectionListDTO();
		selectionListDTO.setAge(true);
		selectionListDTO.setGender(true);
		registrationDTO.setSelectionListDTO(selectionListDTO);
		registrationDTO.getRegistrationMetaDataDTO().setParentOrGuardianUINOrRID("10011100110015720190305142048");

		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(sessionMap).when(SessionContext.class, "map");

		mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
		registrationDTO.getRegistrationMetaDataDTO().setParentOrGuardianUINOrRID("5819320961");

		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightEye());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightIndex());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftIndex());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightThumb());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void runtimeExceptionTest() {
		mapperFacade.convert(null, PacketMetaInfo.class, "packetMetaInfo");
	}

	@Test
	public void emptyObjectTest() throws Exception {
		HashMap<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, new HashMap<>());
		sessionMap.put(RegistrationConstants.IS_Child, true);

		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocuments(new HashMap<>());
		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		demographicInfoDTO.setIdentity(new Identity());
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		registrationDTO.setDemographicDTO(demographicDTO);
		BiometricDTO biometricDTO = new BiometricDTO();
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();		
		faceDetailsDTO.setNumOfRetries(1);
		biometricInfoDTO.setFace(faceDetailsDTO);
		biometricInfoDTO.setExceptionFace(new FaceDetailsDTO());
		biometricDTO.setApplicantBiometricDTO(biometricInfoDTO);
		biometricDTO.setIntroducerBiometricDTO(biometricInfoDTO);
		biometricDTO.setOperatorBiometricDTO(new BiometricInfoDTO());
		biometricDTO.setSupervisorBiometricDTO(new BiometricInfoDTO());
		registrationDTO.setBiometricDTO(biometricDTO);
		registrationDTO.setOsiDataDTO(new OSIDataDTO());
		registrationDTO.setRegistrationMetaDataDTO(new RegistrationMetaDataDTO());
		registrationDTO.setRegistrationId("2018782130000128122018103836");
		SelectionListDTO selectionListDTO = new SelectionListDTO();
		registrationDTO.setSelectionListDTO(selectionListDTO);

		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(sessionMap).when(SessionContext.class, "map");

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
		Assert.assertNotNull(packetMetaInfo.getIdentity().getDocuments());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}

}
