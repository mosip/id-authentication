package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.LocationSearchDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.utils.UBtree;

/**
 * @author Sidhant Agarwal
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LocationSearchFilterIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LocationRepository locationRepository;

	@MockBean
	private UBtree<Location> locationTree;

	@MockBean
	private PageUtils pageUtils;

	@MockBean
	private MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private ObjectMapper objectMapper;

	private SearchSort sort;

	private SearchDto searchDto;

	private SearchFilter filter;

	private RequestWrapper<SearchDto> request;

	@Before
	public void setup() throws JsonProcessingException {
		sort = new SearchSort();
		sort.setSortType("ASC");
		sort.setSortField("postalCode");
		request = new RequestWrapper<>();
		searchDto = new SearchDto();
		Pagination pagination = new Pagination(0, 10);
		filter = new SearchFilter();
		filter.setColumnName("city");
		filter.setType("equals");
		filter.setValue("Rabta");
		searchDto.setFilters(Arrays.asList(filter));
		searchDto.setLanguageCode("eng");
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList(sort));
		request.setRequest(searchDto);

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchLocationTest() throws Exception {
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		String json = objectMapper.writeValueAsString(request);
		when(locationRepository.findAllByLangCode(Mockito.anyString())).thenReturn(locations);
		when(locationRepository.findLocationByHierarchyLevel(Mockito.anyShort(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(location);
		mockMvc.perform(post("/locations/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchLocationContainsTest() throws Exception {
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		filter.setType("contains");
		searchDto.setFilters(Arrays.asList(filter));
		String json = objectMapper.writeValueAsString(request);
		when(locationRepository.findAllByLangCode(Mockito.anyString())).thenReturn(locations);
		when(locationRepository.findLocationByHierarchyLevelContains(Mockito.anyShort(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(Arrays.asList(location));
		mockMvc.perform(post("/locations/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchLocationStartsWithTest() throws Exception {
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		filter.setType("startSWith");
		searchDto.setFilters(Arrays.asList(filter));
		String json = objectMapper.writeValueAsString(request);
		when(locationRepository.findAllByLangCode(Mockito.anyString())).thenReturn(locations);
		when(locationRepository.findLocationByHierarchyLevelContains(Mockito.anyShort(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(Arrays.asList(location));
		mockMvc.perform(post("/locations/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchLocationExceptionTest() throws Exception {
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		filter.setType("error-type");
		searchDto.setFilters(Arrays.asList(filter));
		String json = objectMapper.writeValueAsString(request);
		when(locationRepository.findAllByLangCode(Mockito.anyString())).thenReturn(locations);
		when(locationRepository.findLocationByHierarchyLevelContains(Mockito.anyShort(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(Arrays.asList(location));
		MvcResult response = mockMvc
				.perform(post("/locations/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
		String errorResponse = response.getResponse().getContentAsString();
		ResponseWrapper<LocationSearchDto> responseWrapper = objectMapper.readValue(errorResponse,
				ResponseWrapper.class);

		System.out.println();

		assertThat(responseWrapper.getErrors().get(0).getMessage(),
				is("Column city doesn't support filter type error-type"));
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterAllEmptyTextLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("Zone");
		filterDto.setType("all");
		filterDto.setText("");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		List<String> hierarchyNames = new ArrayList<>();
		hierarchyNames.add("Zone");
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		when(locationRepository.findLocationAllHierarchyNames()).thenReturn(hierarchyNames);
		when(locationRepository.findAllHierarchyNameAndNameValueForEmptyTextFilter(Mockito.anyString(),
				Mockito.anyString())).thenReturn(locations);

		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	
	@Test
	@WithUserDetails("zonal-admin")
	public void filterAllWithTextLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("Zone");
		filterDto.setType("all");
		filterDto.setText("abc");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		List<String> hierarchyNames = new ArrayList<>();
		hierarchyNames.add("Zone");
		List<Location> locations = new ArrayList<>();
		Location location = new Location();
		location.setCode("1001");
		location.setHierarchyName("postalCode");
		location.setLangCode("eng");
		location.setParentLocCode("PAR");
		location.setHierarchyLevel((short) 2);
		location.setName("10045");
		locations.add(location);
		when(locationRepository.findLocationAllHierarchyNames()).thenReturn(hierarchyNames);
		when(locationRepository.findAllHierarchyNameAndNameValueForEmptyTextFilter(Mockito.anyString(),
				Mockito.anyString())).thenReturn(locations);

		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void filterUniqueEmptyTextLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("Zone");
		filterDto.setType("unique");
		filterDto.setText("");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		List<String> hierarchyNames = new ArrayList<>();
		hierarchyNames.add("Zone");
		
		when(locationRepository.findLocationAllHierarchyNames()).thenReturn(hierarchyNames);
		when(locationRepository.findDistinctHierarchyNameAndNameValueForEmptyTextFilter(Mockito.anyString(),
				Mockito.anyString())).thenReturn(hierarchyNames);

		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	@Test
	@WithUserDetails("zonal-admin")
	public void filterUniqueWithTextLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("Zone");
		filterDto.setType("unique");
		filterDto.setText("abc");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		List<String> hierarchyNames = new ArrayList<>();
		hierarchyNames.add("Zone");
		
		when(locationRepository.findLocationAllHierarchyNames()).thenReturn(hierarchyNames);
		when(locationRepository.findDistinctHierarchyNameAndNameValueForTextFilter(Mockito.anyString(),
				Mockito.anyString(),Mockito.anyString())).thenReturn(hierarchyNames);

		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void filterInvalidTypeExceptionLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("Zone");
		filterDto.setType("invalidType");
		filterDto.setText("abc");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
	
		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	@Test
	@WithUserDetails("zonal-admin")
	public void filterInvalidColumnNameExceptionLocationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("InvalidColumn");
		filterDto.setType("all");
		filterDto.setText("abc");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
	
		mockMvc.perform(post("/locations/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
}
