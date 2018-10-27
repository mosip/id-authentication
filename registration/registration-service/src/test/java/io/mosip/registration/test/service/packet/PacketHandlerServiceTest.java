package io.mosip.registration.test.service.packet;

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
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.PacketCreationService;
import io.mosip.registration.service.PacketEncryptionService;
import io.mosip.registration.service.packet.PacketHandlerServiceImpl;

public class PacketHandlerServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketHandlerServiceImpl packetHandlerServiceImpl;
	@Mock
	private PacketCreationService packetCreationService;
	@Mock
	private PacketEncryptionService packetEncryptionService;
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	ResponseDTO mockedSuccessResponse;

	@Before
	public void initialize() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(packetHandlerServiceImpl, "initializeLogger", mosipRollingFileAppender);
		mockedSuccessResponse = new ResponseDTO();
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class),
				Mockito.any(AppModule.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(packetHandlerServiceImpl, "logger", logger);
	}

	@Test
	public void testHandle() throws RegBaseCheckedException {
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class))).thenReturn("Packet Creation".getBytes());
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		Assert.assertSame(mockedSuccessResponse, packetHandlerServiceImpl.handle(new RegistrationDTO()));
	}

	@Test
	public void testCreationException() throws RegBaseCheckedException {
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class))).thenReturn(null);
		ResponseDTO actualResponse = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertEquals(RegistrationExceptions.REG_PACKET_CREATION_ERROR_CODE.getErrorCode(),
				actualResponse.getErrorResponseDTOs().get(0).getCode());
	}

	@Test
	public void testHandlerException() throws RegBaseCheckedException {
		RegBaseUncheckedException exception = new RegBaseUncheckedException("errorCode", "errorMsg");
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class)))
				.thenThrow(exception);
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		ResponseDTO dto = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertNotNull(dto.getErrorResponseDTOs());
	}

	@Test
	public void testHandlerChkException() throws RegBaseCheckedException {
		RegBaseCheckedException exception = new RegBaseCheckedException("errorCode", "errorMsg");
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class)))
				.thenThrow(exception);
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		ResponseDTO dto = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertNotNull(dto.getErrorResponseDTOs());
	}

}
