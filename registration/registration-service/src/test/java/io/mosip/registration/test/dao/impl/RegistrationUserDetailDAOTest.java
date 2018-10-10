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
import io.mosip.registration.dao.impl.RegistrationUserDetailDAOImpl;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;

public class RegistrationUserDetailDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	
	@InjectMocks
	private RegistrationUserDetailDAOImpl registrationUserDetailDAOImpl;

	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	
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
		ReflectionTestUtils.invokeMethod(registrationUserDetailDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void getUserDetailSuccessTest() {

		ReflectionTestUtils.setField(registrationUserDetailDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		registrationUserDetail.setCntrId("000567");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);

		registrationUserDetailDAOImpl.getUserDetail("Sravya");
	}

	@Test
	public void getUserDetailFailureTest() {

		ReflectionTestUtils.setField(registrationUserDetailDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);

		registrationUserDetailDAOImpl.getUserDetail("Sravya");
	}

	@Test
	public void getUserStatusTest() {

		ReflectionTestUtils.setField(registrationUserDetailDAOImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setUserStatus("Sravya");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);

		registrationUserDetailDAOImpl.getUserStatus("Sravya");
	}
}
