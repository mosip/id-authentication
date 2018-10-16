package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.RegistrationUserPasswordDAOImpl;
import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationUserPasswordRepository;

public class RegistrationUserPasswordDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;

	@InjectMocks
	private RegistrationUserPasswordDAOImpl registrationUserPassworDAOImpl;

	@Mock
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;

	
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
		ReflectionTestUtils.invokeMethod(registrationUserPassworDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}
	
	@Test
	public void validateUserPasswordSuccessTest() {

		ReflectionTestUtils.setField(registrationUserPassworDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();
		registrationUserPassword.setPwd("mosip");
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);
		assertFalse(registrationUserPasswordList.isEmpty());
		String userData = registrationUserPasswordList.get(0).getPwd();
		assertNotNull(userData);
		assertEquals("mosip", userData);
		assertTrue(registrationUserPassworDAOImpl.getPassword("mosip", "mosip"));
	}

	@Test
	public void validateUserPasswordTest() {

		ReflectionTestUtils.setField(registrationUserPassworDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();
		registrationUserPassword.setPwd(null);
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);
		assertFalse(registrationUserPasswordList.isEmpty());
		String userData = registrationUserPasswordList.get(0).getPwd();
		assertNull(userData);
		assertEquals(null, userData);
		assertFalse(registrationUserPassworDAOImpl.getPassword("mosip", null));
	}

	@Test
	public void validateUserPasswordFailureTest() {

		ReflectionTestUtils.setField(registrationUserPassworDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();

		// registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);

		assertTrue(registrationUserPasswordList.isEmpty());
		assertFalse(registrationUserPassworDAOImpl.getPassword("mosip", "mosip"));
	}

}
