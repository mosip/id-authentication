package io.mosip.registration.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.ZipCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;

import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_DATA_HASH_FILE_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_META_JSON_NAME;

public class ZipCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ZipCreationServiceImpl zipCreationService;
	private RegistrationDTO registrationDTO;
	private Map<String, byte[]> filesGeneratedForPacket;

	@Before
	public void initialize() throws RegBaseCheckedException {
		registrationDTO = DataProvider.getPacketDTO();
		filesGeneratedForPacket = new HashMap<>();
		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		filesGeneratedForPacket.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		filesGeneratedForPacket.put(PACKET_DATA_HASH_FILE_NAME, "HASHCode".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, "packet_osi_hash".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME, "applicant_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME, "introducer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, "officer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME, "supervisor_bio_cbeff".getBytes());
	}

	@Test
	public void testPacketZipCreator() throws RegBaseCheckedException {
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris("capturedImage".getBytes());
		irisDetailsDTO.setIrisImageName("officerIris.jpg");
		irisDetailsDTOs.add(irisDetailsDTO);
		registrationDTO.getBiometricDTO().getOperatorBiometricDTO().setIrisDetailsDTO(irisDetailsDTOs);

		irisDetailsDTOs = new ArrayList<>();
		irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris("capturedImage".getBytes());
		irisDetailsDTO.setIrisImageName("supervisorIris.jpg");
		irisDetailsDTOs.add(irisDetailsDTO);
		registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().setIrisDetailsDTO(irisDetailsDTOs);
		registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().setFingerprintDetailsDTO(null);
		byte[] packetZipInBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		Assert.assertNotNull(packetZipInBytes);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		zipCreationService.createPacket(registrationDTO, new HashMap<String, byte[]>());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testIOException() throws RegBaseCheckedException {
		DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsResidenceDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsResidenceDTO.setCategory("PoA");
		documentDetailsResidenceDTO.setFormat("passport");
		documentDetailsResidenceDTO.setValue("aaa");
		documentDetailsResidenceDTO.setOwner("hof");
		registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
				.setProofOfAddress(documentDetailsResidenceDTO);

		documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsResidenceDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsResidenceDTO.setCategory("PoA");
		documentDetailsResidenceDTO.setFormat("passport");
		documentDetailsResidenceDTO.setValue("aaa");
		documentDetailsResidenceDTO.setOwner("hof");
		registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
				.setProofOfIdentity(documentDetailsResidenceDTO);

		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}
	
	@Test
	public void emptyDataTest() throws RegBaseCheckedException {
		filesGeneratedForPacket = new HashMap<>();
		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		filesGeneratedForPacket.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		filesGeneratedForPacket.put(PACKET_DATA_HASH_FILE_NAME, "HASHCode".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, "packet_osi_hash".getBytes());
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		demographicInfoDTO.setIdentity(new Identity());
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		registrationDTO.setDemographicDTO(demographicDTO);
		BiometricDTO biometricDTO = new BiometricDTO();
		registrationDTO.setBiometricDTO(biometricDTO);
		registrationDTO.setOsiDataDTO(new OSIDataDTO());
		registrationDTO.setRegistrationMetaDataDTO(new RegistrationMetaDataDTO());
		registrationDTO.setRegistrationId("2018782130000128122018103836");
		
		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
		
		zipCreationService.createPacket(new RegistrationDTO(), filesGeneratedForPacket);
	}

}
