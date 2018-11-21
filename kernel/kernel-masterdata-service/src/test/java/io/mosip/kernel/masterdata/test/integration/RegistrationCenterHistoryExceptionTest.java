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
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationCenterHistoryExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ModelMapper modelMapper;

	@MockBean
	RegistrationCenterHistoryRepository repository;

	RegistrationCenterHistory center;

	List<RegistrationCenterHistory> centers = new ArrayList<>();

	@Before
	public void setInitials() {
		center = new RegistrationCenterHistory();
		center.setId("1");
		center.setName("bangalore");
		center.setLatitude("12.9180722");
		center.setLongitude("77.5028792");
		center.setLanguageCode("ENG");
		center.setLocationCode("BLR");

	}

	@Test
	public void getRegistrationCentersHistoryNotFoundExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(null);
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryEmptyExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(centers);
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryFetchExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andReturn();
	}

}
