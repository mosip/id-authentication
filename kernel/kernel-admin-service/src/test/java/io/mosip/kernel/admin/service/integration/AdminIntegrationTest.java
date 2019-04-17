package io.mosip.kernel.admin.service.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.admin.entity.SyncJobDef;
import io.mosip.kernel.admin.repository.SyncJobDefRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AdminIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SyncJobDefRepository syncJobDefRepository;

	private List<SyncJobDef> syncJobDefs = null;

	private static final String URL = "/syncjobdef?lastupdatedtimestamp=2019-09-09T09:09:09.000Z";

	private static final String EXCEPTION_URL = "/syncjobdef?lastupdatedtimestamp=2019-02-09T09:09:09.000Z";

	private static final String EMPTY_TIMESTAMP_URL = "/syncjobdef";

	@Before
	public void setup() {

		SyncJobDef syncJobDef = new SyncJobDef();
		syncJobDef.setApiName("sync");
		syncJobDef.setId("REGISRATION");
		syncJobDef.setLangCode("eng");
		syncJobDef.setLockDuration("10000");
		syncJobDefs = new ArrayList<>();
		syncJobDefs.add(syncJobDef);
	}

	@Test
	public void syncJobDefsuccessTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(syncJobDefs);
		mockMvc.perform(get(URL)).andExpect(status().isOk());
	}

	@Test
	public void syncJobDefEmptyTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(new ArrayList<SyncJobDef>());
		mockMvc.perform(get(URL)).andExpect(status().isOk());
	}

	@Test
	public void syncJobDefNullTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(null);
		mockMvc.perform(get(URL)).andExpect(status().isOk());
	}

	@Test
	public void syncJobDefExceptionTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get(EXCEPTION_URL)).andExpect(status().isInternalServerError());
	}

	@Test
	public void syncJobDefEmptyTimeStampTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(syncJobDefs);
		mockMvc.perform(get(EMPTY_TIMESTAMP_URL)).andExpect(status().isOk());
	}

}
