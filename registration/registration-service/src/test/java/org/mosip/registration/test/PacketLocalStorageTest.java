package org.mosip.registration.test;

import static org.mosip.registration.constants.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static org.mosip.registration.constants.RegConstants.ENROLLMENT_META_JSON_NAME;
import static org.mosip.registration.constants.RegConstants.HASHING_JSON_NAME;
import static org.mosip.registration.constants.RegConstants.PACKET_META_JSON_NAME;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.test.util.datastub.DataProvider;
import org.mosip.registration.util.store.StorageManager;
import org.mosip.registration.util.zip.ZipCreationManager;

public class PacketLocalStorageTest {

	@Test
	public void testLocalStorage() throws RegBaseCheckedException, IOException, URISyntaxException {
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		byte[] packetZipInBytes = ZipCreationManager.createPacket(DataProvider.getEnrollmentDTO(), jsonMap);
		StorageManager.storeToDisk("1234567890123", packetZipInBytes);
		File file = new File("1234567890123");
		file.delete();
	}
}
