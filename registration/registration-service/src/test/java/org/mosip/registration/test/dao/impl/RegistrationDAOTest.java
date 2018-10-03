package org.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.mosip.registration.constants.RegClientStatusCode;
import org.mosip.registration.dao.RegTransactionDAO;
import org.mosip.registration.dao.impl.RegistrationDAOImpl;
import org.mosip.registration.entity.Registration;
import org.mosip.registration.entity.RegistrationTransaction;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.repositories.RegTransactionRepository;
import org.mosip.registration.repositories.RegistrationRepository;
import org.springframework.test.util.ReflectionTestUtils;

public class RegistrationDAOTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private RegistrationDAOImpl registrationDAOImpl;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	@Mock
	private RegTransactionDAO regTransactionDAO;
	private RegistrationTransaction regTransaction;
	
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
		
		OffsetDateTime time = OffsetDateTime.now();
		regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId("11111");
		regTransaction.setTrnTypeCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setStatusCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setCrBy("Officer");
		regTransaction.setCrDtime(time);
		
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(registrationDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(registrationDAOImpl, "LOGGER", logger);

		when(regTransactionDAO.save(Mockito.anyString())).thenReturn(regTransaction);
		when(registrationRepository.create(Mockito.any(Registration.class))).thenReturn(new Registration());
		registrationDAOImpl.save("D:/Packet Store/28-Sep-2018/111111", "Applicant");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(registrationDAOImpl, "LOGGER", logger);
		when(registrationRepository.create(Mockito.any(Registration.class))).thenThrow(RegBaseUncheckedException.class);
		registrationDAOImpl.save("file", "Invalid");
	}
	
	@Test
	public void getRegistrationByIdTest() {
		ReflectionTestUtils.setField(registrationDAOImpl, "LOGGER", logger);
		
		List<Registration> packetLists = new ArrayList<>();
		packetLists.add(new Registration());
		when(registrationRepository.findByIdIn(Mockito.anyListOf(String.class))).thenReturn(packetLists);
		List<String> packetNames=new ArrayList<>();
		registrationDAOImpl.getRegistrationById(packetNames);
	}
	
	@Test
	public void updateRegStatusTest() {
		ReflectionTestUtils.setField(registrationDAOImpl, "LOGGER", logger);
		Registration updatedPacket=new Registration();
		Mockito.when(registrationRepository.getOne(Mockito.anyString())).thenReturn(updatedPacket);
		registrationDAOImpl.updateRegStatus("111111");
		assertEquals("P", updatedPacket.getClientStatusCode());
	}
}
