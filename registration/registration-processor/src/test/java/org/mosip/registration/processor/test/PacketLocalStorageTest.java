package org.mosip.registration.processor.test;

import static org.mosip.registration.processor.consts.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.ENROLLMENT_META_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.HASHING_JSON_NAME;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_META_JSON_NAME;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.test.util.datastub.DataProvider;
import org.mosip.registration.processor.util.store.StorageManager;
import org.mosip.registration.processor.util.zip.ZipCreationManager;

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
