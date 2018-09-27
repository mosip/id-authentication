package org.mosip.registration.test;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.config.SpringConfiguration;
import org.mosip.registration.service.packet.creation.PacketCreationManager;
import org.mosip.registration.util.hmac.HMACGeneration;
import org.mosip.registration.util.zip.ZipCreationManager;
import org.springframework.beans.factory.annotation.Autowired;

public class PacketCreationManagerTest extends SpringConfiguration {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private ZipCreationManager zipCreationManager;
	@Mock
	private HMACGeneration hMACGeneration;
	@Autowired
	private PacketCreationManager packetCreationManager;

	@Test
	public void testCreatePacket() {
		/*byte[] packetData = "Welcome".getBytes();
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		// TODO
		//when(JSONUtil..convert(enrollmentDTO)).thenReturn(jsonMap);
		when(zipCreationManager.zipPacket(enrollmentDTO, jsonMap)).thenReturn(packetData);
		byte[] actualPacketData = packetCreationManager.create(enrollmentDTO);
		assertArrayEquals(packetData, actualPacketData);*/
	}

}
