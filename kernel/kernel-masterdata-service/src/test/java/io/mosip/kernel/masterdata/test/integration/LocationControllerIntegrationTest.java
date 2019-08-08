package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.repository.LocationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private LocationRepository repo;
	private Location location1;
	private Location location2;
	private Location location3;
	private LocationDto dto1;
	private LocationDto dto2;
	private LocationDto dto3;

	@Autowired
	private ObjectMapper mapper;

	private RequestWrapper<List<LocationDto>> request;

	@Before
	public void setup() {
		location1 = new Location("ABC", "LOCATION NAME", (short) 3, "City", "XYZ", "fra", null);
		location2 = new Location("ABC", "LOCATION NAME", (short) 3, "City", "XYZ", "ara", null);
		location3 = new Location("ABC", "LOCATION NAME", (short) 3, "City", "XYZ", "eng", null);
		dto1 = new LocationDto("ABC", "Location Name", (short) 3, "City", "XYZ", "fra", false);
		dto2 = new LocationDto("ABC", "Location Name", (short) 3, "City", "XYZ", "ara", false);
		dto3 = new LocationDto("ABC", "Location Name", (short) 3, "City", "XYZ", "eng", false);
		request = new RequestWrapper<>();
		request.setId("1.0");
		request.setRequesttime(LocalDateTime.now());
		request.setMetadata("masterdata.location.create");
		when(repo.saveAll(Mockito.any())).thenReturn(Arrays.asList(location1, location2, location3));
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void locationCreateSuccess() throws Exception {
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createActiveLocationSuccess() throws Exception {
		location1.setIsActive(true);
		location2.setIsActive(true);
		location3.setIsActive(true);
		when(repo.saveAll(Mockito.any())).thenReturn(Arrays.asList(location1, location2, location3));
		dto1.setIsActive(true);
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createDefaultlangMissing() throws Exception {
		request.setRequest(Arrays.asList(dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createActiveLocationFailure() throws Exception {
		dto1.setIsActive(true);
		request.setRequest(Arrays.asList(dto1, dto2));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createLocationHierarchyLevelAlreadtExist() throws Exception {
		when(repo.findByNameAndLevel(Mockito.anyString(), Mockito.anyShort())).thenReturn(Arrays.asList(location1));
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createInvalidLangCode() throws Exception {
		dto2.setLangCode("ABC");
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createInvalidHierarachyLevel() throws Exception {
		dto2.setHierarchyLevel((short) 1);
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createInvalidCode() throws Exception {
		dto2.setCode("MNB");
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createEmptyLocationCode() throws Exception {
		dto2.setCode("");
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createEmptyLocationCodePrimary() throws Exception {
		dto1.setCode("");
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void createSaveFailure() throws Exception {
		when(repo.saveAll(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
		request.setRequest(Arrays.asList(dto1, dto2, dto3));
		String requestJson = mapper.writeValueAsString(request);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isInternalServerError());
	}

}
