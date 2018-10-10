package io.mosip.registration.test.dao.impl;

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
import io.mosip.registration.dao.impl.RegistrationUserRoleDAOImpl;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationUserRoleRepository;

public class RegistrationUserRoleDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;

	@InjectMocks
	private RegistrationUserRoleDAOImpl registrationUserRoleDAOImpl;

	@Mock
	private RegistrationUserRoleRepository registrationUserRoleRepository;

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
		ReflectionTestUtils.invokeMethod(registrationUserRoleDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void getRolesSuccessTest() {

		ReflectionTestUtils.setField(registrationUserRoleDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleId registrationUserRoleId = new RegistrationUserRoleId();
		registrationUserRoleId.setUsrId("Shravya");
		registrationUserRole.setRegistrationUserRoleId(registrationUserRoleId);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(
				registrationUserRoleRepository.findByRegistrationUserRoleIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserRoles);

		registrationUserRoleDAOImpl.getRoles("Sravya");
	}

	@Test
	public void getRolesFailureTest() {

		ReflectionTestUtils.setField(registrationUserRoleDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleId registrationUserRoleId = new RegistrationUserRoleId();
		registrationUserRole.setRegistrationUserRoleId(registrationUserRoleId);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(
				registrationUserRoleRepository.findByRegistrationUserRoleIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserRoles);

		registrationUserRoleDAOImpl.getRoles("Sravya");

	}

}
