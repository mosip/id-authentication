package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataSearchIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FilterTypeValidator filterTypeValidator;

	@MockBean
	private MasterdataSearchHelper masterdataSearchHelper;

	@MockBean
	private LocationService locationService;

	@Autowired
	private ObjectMapper objectMapper;

	private RegistrationCenterType centerTypeEntity;
	private RegistrationCenter centerEntity;
	private Location locationRegionEntity;
	private Location locationProvinceEntity;
	private Location locationCityEntity;
	private Location locationLaaEntity;
	private Location locationPostalCodeEntity;
	private SearchFilter filter1;
	private SearchFilter filter2;
	private SearchFilter filter3;
	private SearchFilter filter4;
	private SearchFilter filter5;
	private SearchFilter filter6;
	private SearchFilter filter7;
	private SearchSort sort;
	private SearchDto searchDto;
	private RequestWrapper<SearchDto> request;

	private DocumentType documentType;
	private List<DocumentType> documentTypes;

	@Before
	public void setup() throws JsonProcessingException {
		documentTypeSetUp();
		filter1 = new SearchFilter();
		filter1.setColumnName("name");
		filter1.setValue("*mosip*");
		filter1.setType("contains");

		filter2 = new SearchFilter();
		filter2.setColumnName("centertypename");
		filter2.setValue("*text*");
		filter2.setType("contains");

		filter3 = new SearchFilter();
		filter3.setColumnName("city");
		filter3.setValue("cityname");
		filter3.setType("contains");

		filter4 = new SearchFilter();
		filter4.setColumnName("postal code");
		filter4.setValue("12345");
		filter4.setType("equals");

		filter5 = new SearchFilter();
		filter5.setColumnName("region");
		filter5.setValue("12345");
		filter5.setType("equals");

		filter6 = new SearchFilter();
		filter6.setColumnName("laa");
		filter6.setValue("12345");
		filter6.setType("equals");

		filter7 = new SearchFilter();
		filter7.setColumnName("province");
		filter7.setValue("12345");
		filter7.setType("equals");

		sort = new SearchSort();
		sort.setSortField("updatedDateTime");
		sort.setSortType("desc");

		centerTypeEntity = new RegistrationCenterType("10001", "eng", "REG", "Center Type", null);
		centerEntity = new RegistrationCenter();
		centerEntity.setCenterTypeCode("10001");
		centerEntity.setId("10011");
		centerEntity.setLocationCode("100011");
		centerEntity.setName("Registration Center Name");
		centerEntity.setAddressLine1("address line1");
		centerEntity.setAddressLine2("address line2");
		centerEntity.setAddressLine3("address line3");

		locationRegionEntity = new Location("LOC01", "regionname", (short) 1, "region", "LOC00", "eng", null);
		locationProvinceEntity = new Location("LOC02", "provincename", (short) 2, "province", "LOC01", "eng", null);
		locationCityEntity = new Location("LOC03", "cityname", (short) 3, "city", "LOC02", "eng", null);
		locationLaaEntity = new Location("LOC04", "laa", (short) 4, "Local Administrative Authority", "LOC03", "eng",
				null);
		locationPostalCodeEntity = new Location("LOC05", "postalcode", (short) 5, "postalcode", "LOC04", "eng", null);

		request = new RequestWrapper<>();
		searchDto = new SearchDto();
		Pagination pagination = new Pagination(0, 10);
		searchDto.setLanguageCode("eng");
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList(sort));
		request.setRequest(searchDto);

		when(filterTypeValidator.validate(ArgumentMatchers.<Class<LocationExtnDto>>any(), Mockito.anyList()))
				.thenReturn(true);
		when(filterTypeValidator.validate(ArgumentMatchers.<Class<RegistrationCenterTypeExtnDto>>any(),
				Mockito.anyList())).thenReturn(true);
		when(filterTypeValidator.validate(ArgumentMatchers.<Class<RegistrationCenterExtnDto>>any(), Mockito.anyList()))
				.thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenter>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(centerEntity), PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));

	}

	private void documentTypeSetUp() {
		documentType = new DocumentType();
		documentType.setCode("DT001");
		documentTypes = new ArrayList<>();
		documentTypes.add(documentType);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithNameSuccess() throws Exception {
		searchDto.setFilters(Arrays.asList(filter1));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithCenterTypeNameSuccess() throws Exception {
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithCityNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationCityEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter3));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithPostalCodeNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationPostalCodeEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter4));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithRegionNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationRegionEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter5));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithLAANameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationLaaEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter6));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterWithProvinceNameSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(locationProvinceEntity), PageRequest.of(0, 10), 1));

		searchDto.setFilters(Arrays.asList(filter7));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterFilterTypeSuccess() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 0));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCityName() throws Exception {
		when(locationService.getChildList(Mockito.anyString())).thenReturn(Arrays.asList("10001"));
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<Location>>any(), Mockito.any(),
				Mockito.anyList())).thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		searchDto.setFilters(Arrays.asList(filter3));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterTypeName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(), Mockito.anyList()))
						.thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDocumentTypeTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("CONTAINS");
		searchFilter1.setValue("card");
		filters.add(searchFilter1);
		List<SearchSort> sort = new ArrayList<SearchSort>();
		SearchSort searchSort = new SearchSort();
		searchSort.setSortField("name");
		searchSort.setSortType("ASC");
		sort.add(searchSort);

		Pagination pagination = new Pagination();
		pagination.setPageFetch(0);
		pagination.setPageStart(10);

		searchDto.setFilters(filters);
		searchDto.setSort(sort);
		searchDto.setPagination(pagination);
		searchDto.setLanguageCode("eng");

		requestDto.setId("mosip.idtype.create");
		requestDto.setVersion("1.0");
		requestDto.setRequest(searchDto);

		String contentJson = objectMapper.writeValueAsString(requestDto);
		Page<DocumentType> page = new PageImpl<>(documentTypes, PageRequest.of(0, 10), 1);
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<DocumentType>>any(),
				Mockito.any(SearchDto.class), Mockito.anyList())).thenReturn(page);
		mockMvc.perform(post("/documenttypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

}
