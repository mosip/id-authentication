package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.registration.dao.impl.AuditDAOImpl;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegAuditRepository;

public class AuditDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AuditDAOImpl auditDAO;
	@Mock
	private RegAuditRepository auditRepository;
	private static List<Audit> audits;

	@BeforeClass
	public static void initialize() {
		new ArrayList<>();
		audits = new LinkedList<>();
		Audit audit = new Audit();
		audit.setUuid(UUID.randomUUID().toString());
		audit.setCreatedAt(LocalDateTime.now());
		audits.add(audit);
		audit = new Audit();
		audit.setUuid(UUID.randomUUID().toString());
		audit.setCreatedAt(LocalDateTime.now());
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
		List<String> auditUUIDs = new LinkedList<>();
		audits.parallelStream().map(audit -> audit.getUuid()).forEach(auditUUIDs :: add);
		when(auditRepository.updateSyncAudits(Mockito.anyListOf(String.class))).thenReturn(auditUUIDs.size());
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
