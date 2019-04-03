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
	MockMvc mockMvc;

	@MockBean
	SyncJobDefRepository syncJobDefRepository;

	List<SyncJobDef> syncJobDefs = null;

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
		mockMvc.perform(get("/syncjobdef/{lastupdatedtimestamp}", "2019-09-09T09:09:09.000Z"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void syncJobDefEmptyTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(new ArrayList<SyncJobDef>());
		mockMvc.perform(get("/syncjobdef/{lastupdatedtimestamp}", "2019-09-09T09:09:09.000Z"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void syncJobDefNullTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenReturn(null);
		mockMvc.perform(get("/syncjobdef/{lastupdatedtimestamp}", "2019-09-09T09:09:09.000Z"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void syncJobDefExceptionTest() throws Exception {
		when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncjobdef/{lastupdatedtimestamp}", "2019-09-09T09:09:09.000Z"))
				.andExpect(status().isInternalServerError());
	}

}
