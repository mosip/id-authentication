package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.utils.AuditUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AuditUtil auditUtil;
	
	@MockBean
	private LocationRepository repo;
	private Location parentLoc;
	private Location location1;
	private Location location2;
	private Location location3;
	private LocationDto dto1;
	private LocationDto dto2;
	private List<Location> parentLocList;

	@Autowired
	private ObjectMapper mapper;

	private RequestWrapper<LocationDto> request;

	@Before
	public void setup() {
		parentLoc = new Location("XYZ", "LOCATION NAME", (short) 3, "City", "test", "eng", null);
		parentLoc.setIsActive(true);
		location1 = new Location("MDDR", "LOCATION NAME", (short) 3, "City", "XYZ", "eng", null);
		location2 = new Location("", "LOCATION NAME", (short) 3, "City", "XYZ", "ara", null);
		location3 = new Location("", "LOCATION NAME", (short) 3, "City", "XYZ", "eng", null);
		dto1 = new LocationDto("MMDR", "Location Name", (short) 3, "City", "XYZ", "eng", false);
		dto2 = new LocationDto("", "Location Name", (short) 3, "City", "XYZ", "ara", false);
		request = new RequestWrapper<>();
		request.setId("1.0");
		request.setRequesttime(LocalDateTime.now());
		request.setMetadata("masterdata.location.create");
		when(repo.save(Mockito.any())).thenReturn(location1);
		when(repo.save(Mockito.any())).thenReturn(parentLoc);
		parentLocList = new ArrayList<>();
		parentLocList.add(parentLoc);
		doNothing().when(auditUtil).auditRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	@WithUserDetails("global-admin")
	public void locationCreateSuccess() throws Exception {
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		//when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void locationParentNotFoundSuccess() throws Exception {
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		//when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenThrow(new MasterDataServiceException("","Parent location not found"));
		//when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		//when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().is5xxServerError());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createActiveLocationSuccess() throws Exception {
		location1.setIsActive(true);
		when(repo.save(Mockito.any())).thenReturn(Arrays.asList(location1));
		dto1.setIsActive(true);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void updateActiveLocationSuccess() throws Exception {
		location1.setIsActive(true);
		dto1.setIsActive(true);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.update(Mockito.any())).thenReturn(location1);
		mockMvc.perform(put("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void updateIllegalExceptionTest() throws Exception {
		location1.setIsActive(true);
		dto1.setIsActive(true);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.update(Mockito.any())).thenThrow(new IllegalArgumentException());
		mockMvc.perform(put("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().is5xxServerError());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createDefaultlangMissing() throws Exception {
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createActiveLocationFailure() throws Exception {
		//dto1.setIsActive(true);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createLocationHierarchyLevelAlreadtExist() throws Exception {
		when(repo.findByNameAndLevel(Mockito.anyString(), Mockito.anyShort())).thenReturn(Arrays.asList(location1));
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void createLocatioParentLocationnoExist() throws Exception {
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenThrow(new MasterDataServiceException("","Location not Exist"));
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().is5xxServerError());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createInvalidLangCode() throws Exception {
		dto2.setLangCode("ABC");
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void createEmptyLangCode() throws Exception {
		dto2.setLangCode("");
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createInvalidHierarachyLevel() throws Exception {
		dto2.setHierarchyLevel((short) 1);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createInvalidCode() throws Exception {
		dto2.setCode("MNB");
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createEmptyLocationCode() throws Exception {
		dto2.setCode("");
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createEmptyLocationCodePrimary() throws Exception {
		dto1.setCode("");
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void createSaveFailure() throws Exception {
		when(repo.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.findByNameAndLevelLangCode(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(parentLocList);
		when(repo.create(Mockito.any())).thenReturn(location1);
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void createLocationException() throws Exception {
		when(repo.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().is5xxServerError());
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void createLocationIllegalException() throws Exception {
		when(repo.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
		request.setRequest(dto1);
		String requestJson = mapper.writeValueAsString(request);
		when(repo.findLocationHierarchyByCodeAndLanguageCode(Mockito.any(),Mockito.any())).thenThrow(new IllegalArgumentException());
		mockMvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().is5xxServerError());
	}


}