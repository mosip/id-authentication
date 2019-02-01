package io.mosip.registration.test.util.hmac;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.hmac.HMACGeneration;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class HMACGenerationTest {

	@Test
	public void generatePacketDTOTest() throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		byte[] hashArray = HMACGeneration.generatePacketDTOHash(registrationDTO, new HashMap<>(),
				new HashSequence(new BiometricSequence(new LinkedList<>(), new LinkedList<>()),
						new DemographicSequence(new LinkedList<>()), new LinkedList<>()));
		Assert.assertNotNull(hashArray);
	}

	@Test
	public void generatePacketHashWithEmptyRegistrationTest()
			throws IOException, URISyntaxException, RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		BiometricDTO biometricDTO = new BiometricDTO();
		registrationDTO.setBiometricDTO(biometricDTO);
		DemographicDTO demographicDTO = new DemographicDTO();
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		Identity identity = new Identity();
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		registrationDTO.setDemographicDTO(demographicDTO);
		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();
		filesGeneratedForPacket.put(RegistrationConstants.DEMOGRPAHIC_JSON_NAME,
				RegistrationConstants.DEMOGRPAHIC_JSON_NAME.getBytes());
		byte[] hashArray = HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket,
				new HashSequence(new BiometricSequence(new LinkedList<>(), new LinkedList<>()),
						new DemographicSequence(new LinkedList<>()), new LinkedList<>()));
		Assert.assertNotNull(hashArray);
	}

	@Test
	public void generatePacketOSIHashTest() {
		Map<String, byte[]> generatedFilesForPacket = new HashMap<>();
		generatedFilesForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME,
				RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME.getBytes());
		generatedFilesForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME,
				RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME.getBytes());
		generatedFilesForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
				RegistrationConstants.AUDIT_JSON_FILE.getBytes());

		List<String> osiDataHashSequence = new LinkedList<>();

		byte[] hashData = HMACGeneration.generatePacketOSIHash(generatedFilesForPacket, osiDataHashSequence);
		Assert.assertNotNull(hashData);
		Assert.assertThat(osiDataHashSequence, contains("officer_bio_CBEFF", "supervisor_bio_CBEFF", "audit"));
	}

	@Test
	public void generatePacketOSIHashWithoutCBEFFTest() {
		Map<String, byte[]> generatedFilesForPacket = new HashMap<>();
		generatedFilesForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
				RegistrationConstants.AUDIT_JSON_FILE.getBytes());

		List<String> osiDataHashSequence = new LinkedList<>();

		byte[] hashData = HMACGeneration.generatePacketOSIHash(generatedFilesForPacket, osiDataHashSequence);
		Assert.assertNotNull(hashData);
		Assert.assertThat(osiDataHashSequence, contains("audit"));
	}

}
