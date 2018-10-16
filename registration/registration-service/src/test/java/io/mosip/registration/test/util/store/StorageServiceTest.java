package io.mosip.registration.test.util.store;

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
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.store.StorageService;

import static org.mockito.Mockito.when;

public class StorageServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private StorageService storageService;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	@Mock
	private Environment environment;
	
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

		ReflectionTestUtils.invokeMethod(storageService, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(storageService, "logger", logger);
		ReflectionTestUtils.setField(storageService, "environment", environment);
		
		when(environment.getProperty(RegConstants.PACKET_STORE_LOCATION)).thenReturn("PacketStore");
		when(environment.getProperty(RegConstants.PACKET_STORE_DATE_FORMAT)).thenReturn("dd-MMM-yyyy");
	}
	
	@Test
	public void testLocalStorage() throws RegBaseCheckedException {
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(RegConstants.DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(RegConstants.PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(RegConstants.ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(RegConstants.HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		Assert.assertNotNull(storageService.storeToDisk("1234567890123", "demo".getBytes(), "Image".getBytes()));		
	}
	
	@Test(expected=RegBaseUncheckedException.class)
	public void testIOException() throws RegBaseCheckedException {
		storageService.storeToDisk(null, "packet.zip".getBytes(), "ackReceipt".getBytes());
	}
}
