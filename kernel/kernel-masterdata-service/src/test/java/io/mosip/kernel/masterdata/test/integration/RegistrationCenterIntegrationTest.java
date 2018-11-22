package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationCenterIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	RegistrationCenterRepository repository;

	RegistrationCenter center;

	RegistrationCenter centerBangaloreCentral;

	List<RegistrationCenter> centers = new ArrayList<>();

	@Before
	public void setInitials() {
		center = new RegistrationCenter();
		center.setId("1");
		center.setName("bangalore");
		center.setLatitude("12.9180722");
		center.setLongitude("77.5028792");
		center.setLanguageCode("ENG");
		center.setLocationCode("BLR");
		centerBangaloreCentral = new RegistrationCenter();
		centerBangaloreCentral.setId("2");
		centerBangaloreCentral.setName("Bangalore Central");
		centerBangaloreCentral.setLanguageCode("ENG");
		centerBangaloreCentral.setLocationCode("BLR");
	}

	@Test
	public void getSpecificRegistrationCenterByIdTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("1", "ENG")).thenReturn(center);

		MvcResult result = mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ObjectMapper mapper = new ObjectMapper();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);

		assertThat(returnResponse.getRegistrationCenters().get(0).getId(), is("1"));
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersTest() throws Exception {
		centers.add(center);
		when(repository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG")).thenReturn(centers);
		MvcResult result = mockMvc
				.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getLatitude(), is("12.9180722"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificRegistrationCentersTest() throws Exception {
		centers.add(center);
		when(repository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("BLR", "ENG"))
				.thenReturn(centers);
		MvcResult result = mockMvc
				.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificMultipleRegistrationCentersTest() throws Exception {
		centers.add(center);
		centers.add(centerBangaloreCentral);
		when(repository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("BLR", "ENG"))
				.thenReturn(centers);
		MvcResult result = mockMvc
				.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("Bangalore Central"));
	}

	@Test
	public void getAllRegistrationCenterTest() throws Exception {
		centers.add(center);
		centers.add(centerBangaloreCentral);
		when(repository.findAllByIsActiveTrueAndIsDeletedFalse(RegistrationCenter.class)).thenReturn(centers);
		MvcResult result = mockMvc.perform(get("/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("Bangalore Central"));
	}

	@Test
	public void getRegistrationCenterByHierarchylevelAndTextAndLanguageCodeTest() throws Exception {
		centers.add(center);
		when(repository.findRegistrationCenterHierarchyLevelName("CITY", "BANGALORE", "ENG")).thenReturn(centers);
		MvcResult result = mockMvc
				.perform(get("/registrationcenters/COUNTRY/INDIA/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterHierarchyLevelResponseDto returnResponse = mapper.readValue(
				result.getResponse().getContentAsString(), RegistrationCenterHierarchyLevelResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("Bangalore Central"));
	}
}
