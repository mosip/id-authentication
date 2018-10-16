package io.mosip.registration.test.dao.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.dao.impl.AuditDAOImpl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegAuditRepository;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class AuditDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AuditDAOImpl auditDAO;
	@Mock
	private RegAuditRepository auditRepository;
	@Mock
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	List<Audit> audits;

	@Before
	public void initialize() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(auditDAO, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		
		new ArrayList<>();
		audits = new LinkedList<>();
		Audit audit = new Audit();
		audit.setUuid(UUID.randomUUID().toString());
		audit.setCreatedAt(OffsetDateTime.now());
		audits.add(audit);
		audit = new Audit();
		audit.setUuid(UUID.randomUUID().toString());
		audit.setCreatedAt(OffsetDateTime.now());
		audits.add(audit);
	}
	
	@Test
	public void findAllUnsyncAuditsTest() {		
		when(auditRepository.findAllUnsyncAudits()).thenReturn(audits);
		List<Audit> unsyncAudits = auditDAO.getAllUnsyncAudits();
		assertEquals(audits.size(), unsyncAudits.size());
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void getUnsyncAuditsExceptionTest() {
		when(auditRepository.findAllUnsyncAudits()).thenThrow(new NullPointerException("Input is null"));
		auditDAO.getAllUnsyncAudits();
	}
	
	@Test
	public void updateSyncAuditsTest() {
		when(auditRepository.updateSyncAudits(Mockito.anyListOf(String.class))).thenReturn(audits.size());
		List<String> auditUUIDs = new LinkedList<>();
		audits.parallelStream().map(audit -> audit.getUuid()).forEach(auditUUIDs :: add);
		int updatedCount = auditDAO.updateSyncAudits(auditUUIDs);
		Assert.assertEquals(auditUUIDs.size(), updatedCount);
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void updateSyncAuditsExceptionTest() {
		when(auditRepository.updateSyncAudits(Mockito.anyListOf(String.class))).thenThrow(new NullPointerException("list is null"));
		List<String> auditUUIDs = new LinkedList<>();
		audits.parallelStream().map(audit -> audit.getUuid()).forEach(auditUUIDs :: add);
		auditDAO.updateSyncAudits(auditUUIDs);
	}
}
