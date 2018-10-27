package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

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
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.ValidDocumentDAOImpl;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.ValidDocumentRepository;

public class ValidDocumentDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private ValidDocumentDAOImpl validDocumentDAOImpl;
	@Mock
	private ValidDocumentRepository validDocumentRepository;
	@Mock
	private MosipLogger logger;

	private MosipRollingFileAppender mosipRollingFileAppender;

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
		ReflectionTestUtils.invokeMethod(validDocumentDAOImpl, "initializeLogger", mosipRollingFileAppender);

	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(validDocumentDAOImpl, "LOGGER", logger);

		ValidDocument validDocument = new ValidDocument();
		validDocument.setDescription("description");
		validDocument.setName("name");
		validDocument.setCreatedTimesZone(OffsetDateTime.now());
		validDocument.setCreatedBy("createdBy");
		validDocument.setDeletedTimesZone(OffsetDateTime.now());
		validDocument.setLanguageCode("languageCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		validDocument.setGenericId(genericId);
		List<ValidDocument> list = new ArrayList<>();
		list.add(validDocument);

		Mockito.when(validDocumentRepository.findAll()).thenReturn(list);

		assertEquals(list, validDocumentDAOImpl.getValidDocuments());

	}

}
