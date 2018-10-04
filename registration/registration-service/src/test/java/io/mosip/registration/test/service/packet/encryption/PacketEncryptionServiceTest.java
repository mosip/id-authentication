package io.mosip.registration.test.service.packet.encryption;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.test.util.datastub.DataProvider;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.PacketEncryptionService;
import io.mosip.registration.service.packet.encryption.aes.AESEncryptionManager;
import io.mosip.registration.util.store.StorageManager;

public class PacketEncryptionServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private PacketEncryptionService packetEncryptionService;
	@Mock
	private AESEncryptionManager aesEncryptionManager;
	@Mock
	private StorageManager storageManager;
	@Mock
	private RegistrationDAO registrationDAO;
	@Mock
	private AuditFactory auditFactory;
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
		
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(packetEncryptionService, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(packetEncryptionService, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testEncryption() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("Encrypted_Data".getBytes());
		when(storageManager.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		ResponseDTO responseDTO = packetEncryptionService.encrypt(registrationDTO, "PacketZip".getBytes());
		Assert.assertEquals("0000", responseDTO.getSuccessResponseDTO().getCode());
		Assert.assertEquals("Success", responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testCheckedException() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenThrow(RegBaseCheckedException.class);
		when(storageManager.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		packetEncryptionService.encrypt(registrationDTO, "PacketZip".getBytes());
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("Encrypted_Data".getBytes());
		when(storageManager.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		packetEncryptionService.encrypt(new RegistrationDTO(), "PacketZip".getBytes());
	}

}
