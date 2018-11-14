package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistoryId;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationCenterUserMachineMappingIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	RegistrationCenterUserMachineHistoryRepository repository;

	RegistrationCenterUserMachineHistory centerUserMachine;

	RegistrationCenterUserMachineHistoryId id;

	List<RegistrationCenterUserMachineHistory> centers = new ArrayList<>();;

	@Before
	public void setInitials() {
		id = new RegistrationCenterUserMachineHistoryId("1", "1", "1");
		centerUserMachine = new RegistrationCenterUserMachineHistory();
		centerUserMachine.setId(id);
		centerUserMachine.setEffectivetimes(LocalDateTime.now().minusDays(1));
		centers.add(centerUserMachine);
	}

	@Test
	public void getRegistrationCentersMachineUserMappingTest()
			throws Exception {
		when(repository.findByIdAndEffectivetimesLessThanEqual(id,
				LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenReturn(centers);
		MvcResult result = mockMvc.perform(get(
				"/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterUserMachineMappingHistoryResponseDto returnResponse = mapper
				.readValue(result.getResponse().getContentAsString(),
						RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getCntrId(),
				is("1"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getUsrId(),
				is("1"));
		assertThat(
				returnResponse.getRegistrationCenters().get(0).getMachineId(),
				is("1"));
	}
}
