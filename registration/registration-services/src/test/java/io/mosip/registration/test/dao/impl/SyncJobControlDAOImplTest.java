package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.dao.SyncJobControlDAO.SyncJobInfo;
import io.mosip.registration.dao.impl.SyncJobControlDAOImpl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.repositories.SyncJobControlRepository;

public class SyncJobControlDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private SyncJobControlDAOImpl syncJobDAOImpl;
	@Mock
	private SyncJobControlRepository syncStatusRepository;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private SyncJobInfo syncJobnfo;
	@Mock
	private AuditFactoryImpl auditFactory;
	
	private static final List<String> REG_STATUS_CODES = Arrays.asList(RegistrationClientStatusCode.CREATED.getCode(),
			RegistrationClientStatusCode.REJECTED.getCode(), RegistrationClientStatusCode.APPROVED.getCode(),
			RegistrationClientStatusCode.CORRECTION.getCode(), RegistrationClientStatusCode.UIN_UPDATE.getCode(),
			RegistrationClientStatusCode.UIN_LOST.getCode(),
			RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), RegistrationClientStatusCode.ON_HOLD.getCode());

	@Test
	public void testGetSyncStatus() {
		List<SyncControl> comparableList = new ArrayList<>();

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
		Mockito.when(registrationRepository.findByClientStatusCodeIn(REG_STATUS_CODES)).thenReturn(registrationsList);

		syncJobnfo = syncJobDAOImpl.getSyncStatus();
		assertEquals("MDS_J00001", syncJobnfo.getSyncControlList().get(0).getSyncJobId());
		assertEquals("LER_J00009", syncJobnfo.getSyncControlList().get(1).getSyncJobId());
		assertEquals(timestamp, syncJobnfo.getSyncControlList().get(0).getLastSyncDtimes());
		assertEquals(timestamp, syncJobnfo.getSyncControlList().get(1).getLastSyncDtimes());
		assertTrue(syncJobnfo.getYetToExportCount() == registrationsList.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when(registrationRepository.findByClientStatusCodeIn(Mockito.anyList())).thenThrow(RegBaseUncheckedException.class);
		syncJobDAOImpl.getSyncStatus();
	}
	
	
	@Test
	public void updateAndSaveTest() {
		
		SyncControl syncControl=new SyncControl();
		
		when(syncStatusRepository.update(Mockito.any())).thenReturn(syncControl);
		when(syncStatusRepository.save(Mockito.any())).thenReturn(syncControl);
		
		assertEquals(syncJobDAOImpl.update(syncControl), syncControl);
		assertEquals(syncJobDAOImpl.save(syncControl), syncControl);
		
		
	}
	
	@Test
	public void findBySyncJobIdTest() {
		
		SyncControl syncControl=new SyncControl();
		
		when(syncStatusRepository.findBySyncJobId(Mockito.anyString())).thenReturn(syncControl);
		
		assertEquals(syncJobDAOImpl.findBySyncJobId(Mockito.anyString()), syncControl);
		
		
	}
	
	@Test
	public void findAllTest() {
		
		List<SyncControl> controls=new LinkedList<>();
		SyncControl syncControl=new SyncControl();
		controls.add(syncControl);
		
		
		when(syncStatusRepository.findAll()).thenReturn(controls);
		
		assertEquals(syncJobDAOImpl.findAll(), controls);
		
		
	}
	
	@Test
	public void getRegistrationDetailsTest() {

		List<Registration> registrations = new LinkedList<>();
		Registration registration = new Registration();
		registrations.add(registration);		

		when(registrationRepository.findByclientStatusCodeOrderByCrDtimeAsc("REGISTERED")).thenReturn(registrations);

		assertEquals(syncJobDAOImpl.getRegistrationDetails(), registrations);
	}


}
