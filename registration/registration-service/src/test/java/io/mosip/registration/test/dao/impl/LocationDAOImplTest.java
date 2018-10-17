package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.*;

import java.time.OffsetDateTime;
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
import io.mosip.registration.dao.impl.LocationDAOImpl;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.entity.Location;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.LocationRepository;

public class LocationDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private LocationDAOImpl registrationLocationDAOImpl;
	@Mock
	private LocationRepository registrationLocationRepository;
	@Mock
	private MosipLogger logger;
	MosipRollingFileAppender mosipRollingFileAppender;

	@Before
	public void setUp() {
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
		ReflectionTestUtils.invokeMethod(registrationLocationDAOImpl, "initializeLogger", mosipRollingFileAppender);

	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(registrationLocationDAOImpl, "LOGGER", logger);
		List<Location> list = new ArrayList<>();
		Location location = new Location();
		location.setName("name");
		GenericId genericId = new GenericId();
		genericId.setCode("code");
		genericId.setActive(true);
		location.setLocationId(genericId);

		location.setLocationId(genericId);
		location.setCreatedBy("createdBy");
		location.setCreatedDate(OffsetDateTime.now());
		location.setDeleted(true);
		location.setDeletedTimesZone(OffsetDateTime.now());
		location.setUpdatedBy("updatedBy");
		location.setUpdatedTimesZone(OffsetDateTime.now());
		location.setLanguageCode("languageCode");
		location.setHeirarchyLevel(0);
		location.setHeirarchyLevelName("heirarchyLevelName");
		location.setParentLocationCode("parentLocationCode");
		list.add(location);
		Mockito.when(registrationLocationRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationLocationDAOImpl.getLocations());

	}

}
