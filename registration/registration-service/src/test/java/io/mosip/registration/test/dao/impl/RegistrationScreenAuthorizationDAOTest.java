package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertFalse;
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
import io.mosip.registration.dao.impl.RegistrationScreenAuthorizationDAOImpl;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.entity.RegistrationScreenAuthorizationId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;

public class RegistrationScreenAuthorizationDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	
	@InjectMocks
	private RegistrationScreenAuthorizationDAOImpl registrationScreenAuthorizationDAOImpl;

	@Mock
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;
	
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
		ReflectionTestUtils.invokeMethod(registrationScreenAuthorizationDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void getScreenAuthorizationDetailsTest() {

		ReflectionTestUtils.setField(registrationScreenAuthorizationDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationScreenAuthorization registrationScreenAuthorization = new RegistrationScreenAuthorization();
		RegistrationScreenAuthorizationId registrationScreenAuthorizationId = new RegistrationScreenAuthorizationId();

		registrationScreenAuthorizationId.setRoleCode("OFFICER");
		registrationScreenAuthorizationId.setAppId("REGISTRATION");
		registrationScreenAuthorization.setRegistrationScreenAuthorizationId(registrationScreenAuthorizationId);
		registrationScreenAuthorization.setPermitted(true);

		List<RegistrationScreenAuthorization> authorizationList = new ArrayList<>();
		authorizationList.add(registrationScreenAuthorization);
		assertFalse(authorizationList.isEmpty());
		Mockito.when(registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(authorizationList);
		registrationScreenAuthorizationDAOImpl.getScreenAuthorizationDetails("Sravya");

	}
}
