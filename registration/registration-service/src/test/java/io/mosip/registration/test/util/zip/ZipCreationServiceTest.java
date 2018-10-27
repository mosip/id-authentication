package io.mosip.registration.test.util.zip;

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
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.test.util.datastub.DataProvider;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.zip.ZipCreationService;

import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ENROLLMENT_META_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.HASHING_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_META_JSON_NAME;

public class ZipCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ZipCreationService zipCreationService;
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
		
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
	}
	
	@Test
	public void testPacketZipCreator() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(ZipCreationService.class, "logger", logger);
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegistrationConstants.AUDIT_JSON_FILE, "Audit Events".getBytes());
		byte[] packetZipInBytes = ZipCreationService.createPacket(registrationDTO, jsonMap);
		Assert.assertNotNull(packetZipInBytes);
	}
	
	@SuppressWarnings("static-access")
	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(zipCreationService, "logger", logger);
		ReflectionTestUtils.invokeMethod(zipCreationService, "initializeLogger", mosipRollingFileAppender);
		zipCreationService.createPacket(registrationDTO, new HashMap<String, byte[]>());
	}
	
}
