package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistoryId;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationCenterUserMachineMappingIntegrationExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ModelMapper modelMapper;

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

	}

	@Test
	public void getRegistrationCentersMachineUserMappingNotFoundExceptionTest() throws Exception {
		when(repository.findByIdAndEffectivetimesLessThanEqual(id, LocalDateTime.parse("2018-10-30T19:20:30.45")))
				.thenReturn(centers);
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersMachineUserMappingFetchExceptionTest() throws Exception {
		when(repository.findByIdAndEffectivetimesLessThanEqual(id, LocalDateTime.parse("2018-10-30T19:20:30.45")))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	// @Test
	// public void getRegistrationCentersMachineUserMappingExceptionTest()
	// throws Exception {
	// centers.add(centerUserMachine);
	// when(repository.findByIdAndEffectivetimesLessThanEqual(id,
	// LocalDateTime.parse("2018-10-30T19:20:30.45")))
	// .thenReturn(centers);
	// when(modelMapper.map(Mockito.any(), Mockito
	// .eq(new TypeToken<List<RegistrationCenterUserMachineMappingHistoryDto>>() {
	// }.getType()))).thenThrow(IllegalArgumentException.class);
	// mockMvc.perform(get(
	// "/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
	// .contentType(MediaType.APPLICATION_JSON))
	// .andExpect(status().isNotAcceptable()).andReturn();
	// }

	@Test
	public void getCoordinateSpecificRegistrationCentersDateTimeParseExceptionTest() throws Exception {
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45+5:30/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
	}

}
