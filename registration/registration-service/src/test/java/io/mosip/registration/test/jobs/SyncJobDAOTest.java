package io.mosip.registration.test.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.SyncJobDAOImpl;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.repositories.SyncJobRepository;

public class SyncJobDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	SyncJobDAOImpl syncJobDAOImpl;
	
	@Mock
	SyncJobRepository syncJobRepository;
	
	@Test
	public void updateTest() {
		SyncControl syncControl = new SyncControl();
		syncControl.setId("1");
		Mockito.when(syncJobRepository.update(syncControl)).thenReturn(syncControl);
		assertThat(syncJobDAOImpl.update(syncControl), is(syncControl));
	}
	
	@Test
	public void saveTest() {
		SyncControl syncControl = new SyncControl();
		syncControl.setId("1");
		Mockito.when(syncJobRepository.save(syncControl)).thenReturn(syncControl);
		assertThat(syncJobDAOImpl.save(syncControl), is(syncControl));
	}
	
	@Test
	public void findByIdTest() {
		SyncControl syncControl = new SyncControl();
		syncControl.setId("1");
		Mockito.when(syncJobRepository.findBySyncJobId("1")).thenReturn(syncControl);
		assertThat(syncJobDAOImpl.findBySyncJobId("1"), is(syncControl));
	}
	
	
}
