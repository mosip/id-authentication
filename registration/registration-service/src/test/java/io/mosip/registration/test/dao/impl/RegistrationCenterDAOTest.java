package io.mosip.registration.test.dao.impl;

import static org.mockito.Mockito.doNothing;

import java.util.Optional;

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
import io.mosip.registration.dao.impl.RegistrationCenterDAOImpl;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationCenterId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationCenterRepository;

public class RegistrationCenterDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	
	@InjectMocks
	private RegistrationCenterDAOImpl registrationCenterDAOImpl;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;
	
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
		ReflectionTestUtils.invokeMethod(registrationCenterDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void getRegistrationCenterDetailsSuccessTest() {
		
		ReflectionTestUtils.setField(registrationCenterDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationCenter registrationCenter = new RegistrationCenter();
		RegistrationCenterId registrationCenterId = new RegistrationCenterId();
		registrationCenter.setRegistrationCenterId(registrationCenterId);
		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByRegistrationCenterIdCenterIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationCenterList);

		registrationCenterDAOImpl.getRegistrationCenterDetails("Sravya");
	}

}
