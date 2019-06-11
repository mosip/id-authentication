package io.mosip.registration.test.integrationtest;

import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_DATA_HASH_FILE_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_META_JSON_NAME;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.ZipCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;

/**
 * This class tests the methods of ZipCreationService
 * 
 * 
 * @author Priya Soni
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ZipCreationServiceTest {

	@Autowired
	private ZipCreationServiceImpl zipCreationService;

	/**
	 * Test case to check whether RegBaseUncheckedException is thrown in case of
	 * empty input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test(expected = RegBaseUncheckedException.class)
	public void createPacketUncheckedExceptionEmptyInputsTest() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();
		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}

	/**
	 * Test case to check whether RegBaseUncheckedException is thrown in case of
	 * empty files input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test(expected = RegBaseUncheckedException.class)
	public void createPacketUncheckedExceptionEmptyFileInputTest() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();
		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}

	/**
	 * Test case to check whether RegBaseUncheckedException is thrown in case of
	 * null input
	 * 
	 * @throws RegBaseCheckedException
	 */

	@Test(expected = RegBaseUncheckedException.class)
	public void createPacketUncheckedExceptionNullInputTest() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = null;
		Map<String, byte[]> filesGeneratedForPacket = null;
		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}

	/**
	 * 
	 * 
	 * Test case to check whether RegBaseUncheckedException is thrown in case of
	 * partial input passed
	 * 
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test(expected=RegBaseUncheckedException.class)
	public void createPacketUncheckedExceptionIncompleteInputTest() throws RegBaseCheckedException {

		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		RegistrationDTO registrationDTO = new RegistrationDTO();

		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}

	/**
	 * This test checks if RegBaseCheckedException is thrown in case of invalid
	 * input
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test(expected=RegBaseCheckedException.class)
	public void createPacketCheckedExceptionTest() throws RegBaseCheckedException {

		Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

		// complete file input
		filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		filesGeneratedForPacket.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		filesGeneratedForPacket.put(PACKET_DATA_HASH_FILE_NAME, "HASHCode".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, "packet_osi_hash".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME,
				"applicant_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME,
				"introducer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, "officer_bio_cbeff".getBytes());
		filesGeneratedForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME,
				"supervisor_bio_cbeff".getBytes());

		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getFace().setPhotographName("exception photo.jpg");
		registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace().setPhotographName("exception photo.jpg");

		zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);
	}

}