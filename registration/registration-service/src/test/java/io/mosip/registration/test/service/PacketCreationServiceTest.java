package io.mosip.registration.test.service;

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

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.test.util.datastub.DataProvider;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.service.impl.PacketCreationServiceImpl;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.service.ZipCreationService;

public class PacketCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketCreationServiceImpl packetCreationServiceImpl;
	@Mock
	private ZipCreationService zipCreationService;
	@Mock
	private HMACGeneration hMACGeneration;
	@Mock
	private MosipLogger logger;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	private AuditDAO auditDAO;
	private MosipRollingFileAppender mosipRollingFileAppender;
	private RegistrationDTO registrationDTO;

	@Before
	public void initialize() throws IOException, URISyntaxException, RegBaseCheckedException {
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

		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacket() throws RegBaseCheckedException, IOException, URISyntaxException {
		ReflectionTestUtils.setField(packetCreationServiceImpl, "logger", logger);
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(auditDAO.getAllUnsyncAudits())
				.thenReturn(CustomObjectMapper.MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class));
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(packetCreationServiceImpl, "logger", logger);
		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "initializeLogger", mosipRollingFileAppender);
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		packetCreationServiceImpl.create(null);
	}

}
