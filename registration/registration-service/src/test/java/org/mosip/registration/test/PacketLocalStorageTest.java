package org.mosip.registration.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.util.store.StorageManager;
import org.springframework.test.util.ReflectionTestUtils;

public class PacketLocalStorageTest extends SpringConfiguration {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private StorageManager storageManager;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	
	@Before
	public void initialize() {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
	}
	
	@Test
	public void testLocalStorage() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(storageManager, "LOGGER", logger);
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(RegConstants.DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(RegConstants.PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(RegConstants.ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(RegConstants.HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		//byte[] packetZipInBytes = ZipCreationManager.createPacket(DataProvider.getPacketDTO(), jsonMap);
		Assert.assertNotNull(storageManager.storeToDisk("1234567890123", "demo".getBytes(), "Image".getBytes()));		
	}
	
	@Test(expected=RegBaseUncheckedException.class)
	public void testIOException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(storageManager, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(storageManager, "initializeLogger", mosipRollingFileAppender);
		storageManager.storeToDisk(null, "packet.zip".getBytes(), "ackReceipt".getBytes());
	}
}
