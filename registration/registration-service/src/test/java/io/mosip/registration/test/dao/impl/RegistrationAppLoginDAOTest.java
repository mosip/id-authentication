package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dao.impl.RegistrationAppLoginDAOImpl;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationAppLoginRepository;

public class RegistrationAppLoginDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	
	@InjectMocks
	private RegistrationAppLoginDAOImpl registrationAppLoginDAOImpl;

	@Mock
	private RegistrationAppLoginRepository registrationAppLoginRepository;
	
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
		ReflectionTestUtils.invokeMethod(registrationAppLoginDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void getModesOfLoginSuccessTest() throws RegBaseCheckedException {

		ReflectionTestUtils.setField(registrationAppLoginDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationAppLoginMethod registrationAppLoginMethod = new RegistrationAppLoginMethod();
		RegistrationAppLoginMethodId registrationAppLoginMethodId = new RegistrationAppLoginMethodId();
		registrationAppLoginMethodId.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setRegistrationAppLoginMethodId(registrationAppLoginMethodId);
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		loginList.add(registrationAppLoginMethod);
		
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);
		
		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(p -> modes.put(String.valueOf(p.getMethodSeq()), p.getRegistrationAppLoginMethodId().getLoginMethod()));
		modes.put(RegConstants.LOGIN_SEQUENCE, RegConstants.INITIAL_LOGIN_SEQUENCE);
		assertEquals(modes,registrationAppLoginDAOImpl.getModesOfLogin());
	}

	@Test
	public void getModesOfLoginFailureTest() throws RegBaseCheckedException {

		ReflectionTestUtils.setField(registrationAppLoginDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);
		
		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(p -> modes.put(String.valueOf(p.getMethodSeq()), p.getRegistrationAppLoginMethodId().getLoginMethod()));
		modes.put(RegConstants.LOGIN_SEQUENCE, RegConstants.INITIAL_LOGIN_SEQUENCE);
		assertEquals(modes,registrationAppLoginDAOImpl.getModesOfLogin());
	}

}
