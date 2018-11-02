package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.GenderDAOImpl;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.GenderRepository;

public class GenderDaoImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private GenderDAOImpl registrationGenderDAOImpl;
	@Mock
	private GenderRepository registrationGenderRepository;
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;

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
		ReflectionTestUtils.invokeMethod(registrationGenderDAOImpl, "initializeLogger", mosipRollingFileAppender);
	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(registrationGenderDAOImpl, "LOGGER", logger);

		List<Gender> list = new ArrayList<>();
		GenericId genderId = new GenericId();
		genderId.setCode("code");
		genderId.setActive(true);
		Gender gender = new Gender();
		gender.setGenericId(genderId);
		gender.setCreatedTimesZone(new Timestamp(new Date().getTime()));
		gender.setCreatedBy("createdBy");
		gender.setDeleted(true);
		gender.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		gender.setLanguageCode("languageCode");
		gender.setName("name");
		gender.setUpdatedBy("updatedBy");
		gender.setUpdatedTimesZone(new Timestamp(new Date().getTime()));
		list.add(gender);
		Mockito.when(registrationGenderRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationGenderDAOImpl.getGenders());

	}

}
