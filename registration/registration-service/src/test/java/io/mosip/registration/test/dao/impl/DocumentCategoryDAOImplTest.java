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
import io.mosip.registration.dao.impl.DocumentCategoryDAOImpl;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.DocumentCategoryRepository;

public class DocumentCategoryDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentCategoryDAOImpl registrationDocumentCategoryDAOImpl;
	@Mock
	private DocumentCategoryRepository registrationDocumentCategoryRepository;
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
		ReflectionTestUtils.invokeMethod(registrationDocumentCategoryDAOImpl, "initializeLogger",
				mosipRollingFileAppender);

	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(registrationDocumentCategoryDAOImpl, "LOGGER", logger);

		DocumentCategory documentCategory = new DocumentCategory();
		documentCategory.setDescription("description");
		documentCategory.setName("name");
		documentCategory.setCreatedTimesZone(OffsetDateTime.now());
		documentCategory.setCreatedBy("createdBy");
		documentCategory.setDeletedTimesZone(OffsetDateTime.now());
		documentCategory.setLanguageCode("languageCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		documentCategory.setGenericId(genericId);
		List<DocumentCategory> list = new ArrayList<>();
		list.add(documentCategory);

		Mockito.when(registrationDocumentCategoryRepository.findAll()).thenReturn(list);

		assertEquals(list, registrationDocumentCategoryDAOImpl.getDocumentCategories());

	}

}
