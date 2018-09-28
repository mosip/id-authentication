package org.mosip.registration.test.dao.impl;

import static org.mockito.Mockito.when;

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
import org.mosip.registration.dao.impl.RegTransactionDAOImpl;
import org.mosip.registration.entity.RegistrationTransaction;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.repositories.RegTransactionRepository;
import org.springframework.test.util.ReflectionTestUtils;

public class RegTransactionDAOTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private RegTransactionDAOImpl regTransactionDAOImpl;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	
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
		
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(regTransactionDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(regTransactionDAOImpl, "LOGGER", logger);

		when(regTransactionRepository.create(Mockito.any(RegistrationTransaction.class))).thenReturn(new RegistrationTransaction());
		regTransactionDAOImpl.save("11111");
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(regTransactionDAOImpl, "LOGGER", logger);
		when(regTransactionRepository.create(Mockito.any(RegistrationTransaction.class))).thenThrow(new RuntimeException());
		regTransactionDAOImpl.save("file");
	}

}
