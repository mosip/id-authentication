package io.mosip.kernel.masterdata.test.controller;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.service.LocationService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LocationControllerTest {

	private final String EXPECTED = "{\"locations\":[{\"locationCode\":\"KAR\",\"locationName\":\"KARNATAKA\",\"hierarchyLevel\":1,\"hierarchyName\":null,\"parentLocationCode\":\"IND\",\"languageCode\":\"KAN\",\"createdBy\":\"dfs\",\"updatedBy\":\"sdfsd\",\"isActive\":true},{\"locationCode\":\"KAR\",\"locationName\":\"KARNATAKA\",\"hierarchyLevel\":1,\"hierarchyName\":null,\"parentLocationCode\":\"IND\",\"languageCode\":\"KAN\",\"createdBy\":\"dfs\",\"updatedBy\":\"sdfsd\",\"isActive\":true}]}";

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private LocationService service;

	LocationDto locationHierarchyDto = null;
	LocationResponseDto locationHierarchyResponseDto = null;

	@Before
	public void Setup() {
		List<LocationDto> locationHierarchies = new ArrayList<>();
		locationHierarchyResponseDto = new LocationResponseDto();
		locationHierarchyDto = new LocationDto();
		//
		locationHierarchyDto.setLocationCode("IND");
		locationHierarchyDto.setLocationName("INDIA");
		locationHierarchyDto.setHierarchyLevel(0);
		locationHierarchyDto.setHierarchyName(null);
		locationHierarchyDto.setParentLocationCode(null);
		locationHierarchyDto.setLanguageCode("HIN");
		locationHierarchyDto.setCreatedBy("dfs");
		locationHierarchyDto.setUpdatedBy("sdfsd");
		locationHierarchyDto.setIsActive(true);
		locationHierarchies.add(locationHierarchyDto);
		locationHierarchyDto.setLocationCode("KAR");
		locationHierarchyDto.setLocationName("KARNATAKA");
		locationHierarchyDto.setHierarchyLevel(1);
		locationHierarchyDto.setHierarchyName(null);
		locationHierarchyDto.setParentLocationCode("IND");
		locationHierarchyDto.setLanguageCode("KAN");
		locationHierarchyDto.setCreatedBy("dfs");
		locationHierarchyDto.setUpdatedBy("sdfsd");
		locationHierarchyDto.setIsActive(true);
		locationHierarchies.add(locationHierarchyDto);
		locationHierarchyResponseDto.setLocations(locationHierarchies);

	}

	@Test
	public void testGetAllLocationHierarchy() throws Exception {

		Mockito.when(service.getLocationDetails()).thenReturn(locationHierarchyResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/locations"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetLocatonHierarchyByLocCodeAndLangCode() throws Exception {
		Mockito.doReturn(locationHierarchyResponseDto).when(service).getLocationHierarchyByLangCode(Mockito.anyString(),
				Mockito.anyString());

		mockMvc.perform(MockMvcRequestBuilders.get("/locations/KAR/KAN"))
				.andExpect(MockMvcResultMatchers.content().json(EXPECTED))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetAllLocationsNoRecordsFoundException() throws Exception {
		Mockito.when(service.getLocationDetails())
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.get("/locations"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testGetAllLocationsDataBaseException() throws Exception {
		Mockito.when(service.getLocationDetails())
				.thenThrow(new DataNotFoundException("3333333", "Location Hierarchy does not exist"));
		mockMvc.perform(MockMvcRequestBuilders.get("/locations"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetLocationsByLangCodeAndLocCodeDataBaseException() throws Exception {
		Mockito.when(service.getLocationHierarchyByLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new MasterDataServiceException("1111111", "Error from database"));
		mockMvc.perform(MockMvcRequestBuilders.get("/locations/KAR/KAN"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testGetLocationsByLangCodeAndLocCodeNoRecordsFoundException() throws Exception {
		Mockito.when(service.getLocationHierarchyByLangCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new DataNotFoundException("3333333", "Location Hierarchy does not exist"));
		mockMvc.perform(MockMvcRequestBuilders.get("/locations/KAR/KAN"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
