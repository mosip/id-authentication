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
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.DocumentTypeDAOImpl;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.DocumentTypeRepository;

public class DocumentTypeDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentTypeDAOImpl registrationDocumentTypeDAOImpl;
	@Mock
	private DocumentTypeRepository registrationDocumentTypeRepository;
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
		ReflectionTestUtils.invokeMethod(registrationDocumentTypeDAOImpl, "initializeLogger", mosipRollingFileAppender);

	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(registrationDocumentTypeDAOImpl, "LOGGER", logger);
		List<DocumentType> list = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setName("name");
		GenericId genericId = new GenericId();
		genericId.setCode("code");
		genericId.setActive(true);
		documentType.setGenericId(genericId);

		documentType.setCreatedBy("createdBy");
		documentType.setCreatedTimesZone(OffsetDateTime.now());

		documentType.setDeleted(true);
		documentType.setDeletedTimesZone(OffsetDateTime.now());
		documentType.setUpdatedBy("updatedBy");
		documentType.setUpdatedTimesZone(OffsetDateTime.now());
		documentType.setLanguageCode("languageCode");

		list.add(documentType);
		Mockito.when(registrationDocumentTypeRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationDocumentTypeDAOImpl.getDocumentTypes());

	}
}
