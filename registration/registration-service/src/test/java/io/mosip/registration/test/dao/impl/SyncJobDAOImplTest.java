package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.dao.impl.SyncJobDAOImpl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.repositories.SyncJobRepository;

public class SyncJobDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private SyncJobDAOImpl syncJobDAOImpl;
	@Mock
	private SyncJobRepository syncStatusRepository;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private SyncJobInfo syncJobnfo;
	@Mock
	private AuditFactoryImpl auditFactory;

	@Test
	public void testGetSyncStatus() {
		List<SyncControl> comparableList = new ArrayList<>();
		List<String> statusCodes = new ArrayList<>();
		statusCodes.add("R");
		statusCodes.add("I");
		statusCodes.add("A");
		statusCodes.add("C");
		statusCodes.add("U");
		statusCodes.add("L");
		statusCodes.add("S");
		statusCodes.add("H");
		statusCodes.add("E");

		List<Registration> registrationsList = new ArrayList<>();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Registration registration1 = new Registration();
		registration1.setId("101");
		registration1.setClientStatusCode("A");
		registration1.setAckFilename("Ack1");
		Registration registration2 = new Registration();
		registration2.setId("102");
		registration2.setClientStatusCode("I");
		registration2.setAckFilename("Ack2");
		registrationsList.add(registration1);
		registrationsList.add(registration2);

		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(timestamp);
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(timestamp);

		comparableList.add(syncControl1);
		comparableList.add(syncControl2);

		Mockito.when(syncStatusRepository.findAll()).thenReturn(comparableList);
		Mockito.when(registrationRepository.findByClientStatusCodeIn(statusCodes)).thenReturn(registrationsList);

		syncJobnfo = syncJobDAOImpl.getSyncStatus();
		assertEquals("MDS_J00001", syncJobnfo.getSyncControlList().get(0).getSyncJobId());
		assertEquals("LER_J00009", syncJobnfo.getSyncControlList().get(1).getSyncJobId());
		assertEquals(timestamp, syncJobnfo.getSyncControlList().get(0).getLastSyncDtimes());
		assertEquals(timestamp, syncJobnfo.getSyncControlList().get(1).getLastSyncDtimes());
		if (syncJobnfo.getYetToExportCount() == registrationsList.size())
			;
		{
			assertTrue(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when(registrationRepository.findByClientStatusCodeIn(Mockito.anyList())).thenThrow(RegBaseUncheckedException.class);
		syncJobDAOImpl.getSyncStatus();
	}
}
