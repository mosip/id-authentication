package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.DocumentFormatDAOImpl;
import io.mosip.registration.entity.DocumentFormat;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.DocumentFormatRepository;

@RunWith(SpringRunner.class)
public class DocumentFormatDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private DocumentFormatDAOImpl registationDocumentFormatDAOImpl;
	@Mock
	private DocumentFormatRepository registrationDocumentFormatRepository;
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
		ReflectionTestUtils.invokeMethod(registationDocumentFormatDAOImpl, "initializeLogger",
				mosipRollingFileAppender);

	}

	@Test
	public void test() {
		ReflectionTestUtils.setField(registationDocumentFormatDAOImpl, "LOGGER", logger);

		DocumentFormat documentFormat = new DocumentFormat();
		documentFormat.setDescription("description");
		documentFormat.setName("name");
		documentFormat.setCreatedTimesZone(OffsetDateTime.now());
		documentFormat.setCreatedBy("createdBy");
		documentFormat.setDeletedTimesZone(OffsetDateTime.now());
		documentFormat.setLanguageCode("languageCode");
		GenericId genericId = new GenericId();
		genericId.setActive(true);
		genericId.setCode("code");
		documentFormat.setGenericId(genericId);
		List<DocumentFormat> list = new ArrayList<>();
		list.add(documentFormat);

		registationDocumentFormatDAOImpl.initializeLogger(mosipRollingFileAppender);
		Mockito.when(registrationDocumentFormatRepository.findAll()).thenReturn(list);

		assertEquals(list, registationDocumentFormatDAOImpl.getDocumentFormats());

	}

}
