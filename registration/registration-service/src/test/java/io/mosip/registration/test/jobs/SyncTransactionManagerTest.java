package io.mosip.registration.test.jobs;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobTransactionDAO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.impl.SyncManagerImpl;
import io.mosip.registration.repositories.SyncTransactionRepository;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

public class SyncTransactionManagerTest {

	
	@Mock
	private SyncTransactionRepository syncTranscRepository;

	@Mock
	private JobExecutionContext jobExecutionContext;

	@Mock
	private JobDetail jobDetail;

	@Mock
	private Trigger trigger;

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	JobDataMap jobDataMap = new JobDataMap();

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private SyncManagerImpl syncTransactionManagerImpl;

	SyncJobDef syncJob = new SyncJobDef();

	List<SyncJobDef> syncJobList;

	HashMap<String, SyncJobDef> jobMap = new HashMap<>();

	@Mock
	SyncJobTransactionDAO jobTransactionDAO;

	@Mock
	SyncJobDAO syncJobDAO;
	
	@Mock
	private MachineMappingDAO machineMappingDAO;

	@Before
	public void initializeSyncJob() {
		syncJob.setId("1");
		syncJob.setName("Name");
		syncJob.setApiName("API");
		syncJob.setCrBy("Yaswanth");
		syncJob.setCrDtime(new Timestamp(System.currentTimeMillis()));
		syncJob.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
		syncJob.setIsActive(true);
		syncJob.setIsDeleted(false);
		syncJob.setLangCode("EN");
		syncJob.setLockDuration("20");
		syncJob.setParentSyncJobId("ParentSyncJobId");
		syncJob.setSyncFrequency("25");
		syncJob.setUpdBy("Yaswanth");
		syncJob.setUpdDtimes(new Timestamp(System.currentTimeMillis()));

		syncJobList = new LinkedList<>();
		syncJobList.add(syncJob);

		syncJobList.forEach(job -> {
			jobMap.put(job.getId(), job);
		});
		//JobConfigurationServiceImpl.SYNC_JOB_MAP = jobMap;
	}

	

	
	
	private SyncTransaction prepareSyncTransaction() {
		SyncTransaction syncTransaction=new SyncTransaction();

		String transactionId = Integer.toString(new Random().nextInt(10000));
		syncTransaction.setId(transactionId);

		syncTransaction.setSyncJobId(syncJob.getId());

		syncTransaction.setSyncDateTime(new Timestamp(System.currentTimeMillis()));
		syncTransaction.setStatusCode("Completed");
		syncTransaction.setStatusComment("Completed");

		// TODO
		syncTransaction.setTriggerPoint("User");

		syncTransaction.setSyncFrom(RegistrationSystemPropertiesChecker.getMachineId());

		// TODO
		syncTransaction.setSyncTo("SERVER???");

		syncTransaction.setMachmId(RegistrationSystemPropertiesChecker.getMachineId());
		
		// TODO
		syncTransaction.setLangCode("EN");

		
		syncTransaction.setCrBy(SessionContext.getInstance().getUserContext().getUserId());

		syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));
		return syncTransaction;

	}
	@Test
	public void createSyncTest() throws RegBaseCheckedException {
		SyncTransaction syncTransaction = prepareSyncTransaction();
		SyncControl syncControl=null;
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.anyString())).thenReturn(syncControl);
		
		Mockito.when(jobTransactionDAO.save(Mockito.any(SyncTransaction.class))).thenReturn(syncTransaction);
		Mockito.when(machineMappingDAO.getStationID(RegistrationSystemPropertiesChecker.getMachineId())).thenReturn(Mockito.anyString());
		syncTransactionManagerImpl.createSyncTransaction("Completed", "Completed", "USER", "1");
	}
	
	@Test
	public void createSyncControlUpdateTest() {
		SyncTransaction syncTransaction = prepareSyncTransaction();
		SyncControl syncControl=new SyncControl();
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.anyString())).thenReturn(syncControl);
		Mockito.when(syncJobDAO.update(Mockito.any(SyncControl.class))).thenReturn(syncControl);
		syncTransactionManagerImpl.createSyncControlTransaction(syncTransaction);
		
		
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void createSyncTransactionExceptionTest() {
		SyncTransaction syncTransaction = null;
		Mockito.when(jobTransactionDAO.save(Mockito.any(SyncTransaction.class))).thenThrow(NullPointerException.class);
		syncTransactionManagerImpl.createSyncTransaction("Completed", "Completed", "USER", "1");
		
		
	}
	
	@Test
	public void createSyncControlNullTest() {
		SyncTransaction syncTransaction=new SyncTransaction();

		String transactionId = Integer.toString(new Random().nextInt(10000));
		syncTransaction.setId(transactionId);

		syncTransaction.setSyncJobId(syncJob.getId());

		syncTransaction.setSyncDateTime(new Timestamp(System.currentTimeMillis()));
		syncTransaction.setStatusCode("Completed");
		syncTransaction.setStatusComment("Completed");

		// TODO
		syncTransaction.setTriggerPoint("User");

		syncTransaction.setSyncFrom(RegistrationSystemPropertiesChecker.getMachineId());

		// TODO
		syncTransaction.setSyncTo("SERVER???");

		syncTransaction.setMachmId(RegistrationSystemPropertiesChecker.getMachineId());
		
		syncTransaction.setCntrId("CNTR123");
		// TODO
		syncTransaction.setLangCode("EN");

		syncTransaction.setCrBy(SessionContext.getInstance().getUserContext().getUserId());

		syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));
		
		SyncControl syncControl=null;
		Mockito.when(syncJobDAO.findBySyncJobId(Mockito.any())).thenReturn(syncControl);
		syncTransactionManagerImpl.createSyncControlTransaction(syncTransaction);
		
		
	}

}
