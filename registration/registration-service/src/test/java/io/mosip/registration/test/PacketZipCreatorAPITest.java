package io.mosip.registration.test;

import static io.mosip.registration.constants.RegConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegConstants.ENROLLMENT_META_JSON_NAME;
import static io.mosip.registration.constants.RegConstants.HASHING_JSON_NAME;
import static io.mosip.registration.constants.RegConstants.PACKET_META_JSON_NAME;

import java.io.IOException;
import java.net.URISyntaxException;
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
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.test.util.datastub.DataProvider;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.zip.ZipCreationManager;

public class PacketZipCreatorAPITest extends SpringConfiguration {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ZipCreationManager zipCreationManager;
	private RegistrationDTO registrationDTO;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	
	@Before
	public void initialize() throws IOException, URISyntaxException, RegBaseCheckedException {
		registrationDTO = DataProvider.getPacketDTO();
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
	public void testPacketZipCreator() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(ZipCreationManager.class, "LOGGER", logger);
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		byte[] packetZipInBytes = ZipCreationManager.createPacket(registrationDTO, jsonMap);
		Assert.assertNotNull(packetZipInBytes);
	}
	
	@SuppressWarnings("static-access")
	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(zipCreationManager, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(zipCreationManager, "initializeLogger", mosipRollingFileAppender);
		zipCreationManager.createPacket(registrationDTO, new HashMap<String, byte[]>());
	}
	
}
