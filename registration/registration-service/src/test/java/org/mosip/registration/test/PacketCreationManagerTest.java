package org.mosip.registration.test;

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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.registration.config.AuditFactory;
import org.mosip.registration.constants.AppModuleEnum;
import org.mosip.registration.constants.AuditEventEnum;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.PacketCreationService;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.test.util.datastub.DataProvider;
import org.mosip.registration.util.hmac.HMACGeneration;
import org.mosip.registration.util.zip.ZipCreationManager;
import org.springframework.test.util.ReflectionTestUtils;

public class PacketCreationManagerTest extends SpringConfiguration {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketCreationService packetCreationManager;
	@Mock
	private ZipCreationManager zipCreationManager;
	@Mock
	private HMACGeneration hMACGeneration;
	@Mock
	private MosipLogger logger;
	@Mock
	private AuditFactory auditFactory;
	private MosipRollingFileAppender mosipRollingFileAppender;
	private RegistrationDTO registrationDTO;

	@Before
	public void initialize() throws IOException, URISyntaxException {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		registrationDTO = DataProvider.getPacketDTO();
	}

	@Test
	public void testCreatePacket() throws RegBaseCheckedException, IOException, URISyntaxException {
		ReflectionTestUtils.setField(packetCreationManager, "LOGGER", logger);
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class),
				Mockito.any(AppModuleEnum.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Map<String, byte[]> jsonMap = new HashMap<>();
		jsonMap.put(RegConstants.DEMOGRPAHIC_JSON_NAME, "Demo".getBytes());
		jsonMap.put(RegConstants.PACKET_META_JSON_NAME, "Registration".getBytes());
		jsonMap.put(RegConstants.ENROLLMENT_META_JSON_NAME, "Enrollment".getBytes());
		jsonMap.put(RegConstants.HASHING_JSON_NAME, "HASHCode".getBytes());
		jsonMap.put(RegConstants.AUDIT_JSON_FILE, "audit".getBytes());
		Assert.assertNotNull(packetCreationManager.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(packetCreationManager, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(packetCreationManager, "initializeLogger", mosipRollingFileAppender);
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class),
				Mockito.any(AppModuleEnum.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		packetCreationManager.create(null);
	}

}
