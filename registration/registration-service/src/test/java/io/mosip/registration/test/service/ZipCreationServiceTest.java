package io.mosip.registration.test.service;

import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ENROLLMENT_META_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.HASHING_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_META_JSON_NAME;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.ZipCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;

public class ZipCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ZipCreationServiceImpl zipCreationService;
	private static RegistrationDTO registrationDTO;
	
	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		registrationDTO = DataProvider.getPacketDTO();
	}
	
	@Test
	public void testPacketZipCreator() throws RegBaseCheckedException {
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		byte[] packetZipInBytes = zipCreationService.createPacket(registrationDTO, jsonMap);
		Assert.assertNotNull(packetZipInBytes);
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		zipCreationService.createPacket(registrationDTO, new HashMap<String, byte[]>());
	}
	
}
