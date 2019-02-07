package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.dao.impl.SyncTransactionDAOImpl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.repositories.SyncTransactionRepository;

public class SyncTransactionDAOTest {
	@Mock
	private Logger logger;

	@Mock
	private SyncTransactionRepository syncTranscRepository;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private SyncTransactionDAOImpl jobTransactionDAOImpl;

	@Test
	public void saveTest() {
		SyncTransaction syncTransaction = new SyncTransaction();
		Mockito.when(syncTranscRepository.save(Mockito.any())).thenReturn(new SyncTransaction());
		jobTransactionDAOImpl.save(syncTransaction);
	}

	@Test
	public void getAllTest() {
		SyncTransaction syncTransaction = new SyncTransaction();
		LinkedList<SyncTransaction> syncTransactions = new LinkedList<>();
		syncTransactions.add(syncTransaction);
		Mockito.when(syncTranscRepository.findAll()).thenReturn(syncTransactions);

		assertEquals(jobTransactionDAOImpl.getAll(), syncTransactions);
	}

	@Test
	public void getSyncTransactionsTest() {
		SyncTransaction syncTransaction = new SyncTransaction();
		LinkedList<SyncTransaction> syncTransactions = new LinkedList<>();
		syncTransactions.add(syncTransaction);

		Mockito.when(syncTranscRepository.findByCrDtimeAfterAndSyncJobIdNotOrderByCrDtimeDesc(new Timestamp(Mockito.anyLong()),
				Mockito.anyString())).thenReturn(syncTransactions);

		assertEquals(jobTransactionDAOImpl.getSyncTransactions(new Timestamp(Mockito.anyLong()), Mockito.anyString()),
				syncTransactions);

	}

}
