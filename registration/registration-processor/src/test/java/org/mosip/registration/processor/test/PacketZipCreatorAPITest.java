package org.mosip.registration.processor.test;

import static org.mosip.registration.processor.consts.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.ENROLLMENT_META_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.HASHING_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_META_JSON_NAME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.test.util.datastub.DataProvider;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.util.zip.ZipCreationManager;

public class PacketZipCreatorAPITest {

	private EnrollmentDTO enrollmentDTO; 
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		enrollmentDTO = DataProvider.getEnrollmentDTO();
	}
	
	@Test
	public void testPacketZipCreator() throws BaseUncheckedException, IOException, RegBaseCheckedException {
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		byte[] packetZipInBytes = ZipCreationManager.createPacket(enrollmentDTO, jsonMap);
		File file = new File("D:\\packet.zip");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(packetZipInBytes);
		fileOutputStream.flush();
		fileOutputStream.close();
		file.delete();
	}
	
}
