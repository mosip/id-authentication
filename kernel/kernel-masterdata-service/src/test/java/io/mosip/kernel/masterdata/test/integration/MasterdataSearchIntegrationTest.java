package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceSpecificationExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TemplateExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TitleExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.MachineSearchDto;
import io.mosip.kernel.masterdata.dto.response.RegistrationCenterSearchDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.IndividualType;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.repository.ZoneRepository;
import io.mosip.kernel.masterdata.repository.ZoneUserRepository;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.utils.DeviceUtils;
import io.mosip.kernel.masterdata.utils.MachineUtil;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataSearchIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FilterTypeValidator filterTypeValidator;

	@MockBean
	private FilterColumnValidator filterColumnValidator;

	@MockBean
	private MasterdataSearchHelper masterdataSearchHelper;

	@MockBean
	private MasterDataFilterHelper masterDataFilterHelper;

	@MockBean
	private LocationService locationService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MachineRepository machineRepository;

	@MockBean
	private DeviceRepository deviceRepository;

	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	@MockBean
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;

	@MockBean
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	@MockBean
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private ZoneRepository zoneRepository;

	@MockBean
	private ZoneUserRepository zoneUserRepository;

	@MockBean
	private LocationRepository locationRepository;

	@MockBean
	private GenderTypeRepository genderTypeRepository;

	@MockBean
	private HolidayRepository holidayRepository;

	@MockBean
	private MachineUtil machineUtil;

	@MockBean
	private DeviceUtils deviceUtil;
	

	private List<Zone> zones;
	private ZoneUser zoneUser;
	private RegistrationCenterType centerTypeEntity;
	private RegistrationCenter centerEntity;
	private Template template;
	private Title title;
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
	private SearchFilter machineSearchFilter;
	private SearchFilter deviceSearchFilter;
	private SearchFilter deviceTypeSearchFilter;
	private SearchFilter machineSpecificationSearchFilter;
	private SearchFilter zoneFilter;
	private SearchFilter templateSearchFilter;
	private SearchFilter titleSearchFilter;
	private SearchDto docCatTypeDto;
	private SearchFilter docCatTypeFilter;
	private SearchFilter holidayFilter;
	private RequestWrapper<SearchDto> docCatTypeRequestDto;
	private SearchSort sort;
	private SearchDto searchDto;
	private SearchDto machineSearchDto;
	private SearchDto deviceSearchDto;
	private SearchDto deviceTypeSearchDto;
	private SearchDto machineSpecificationSearchDto;
	private SearchDto templateSearchDto;
	private SearchDto titleSearchDto;
	private SearchDto holidaySearchDto;
	private RequestWrapper<SearchDto> request;
	private RequestWrapper<SearchDto> machineRequestDto;
	private RequestWrapper<SearchDto> deviceRequestDto;
	private RequestWrapper<SearchDto> deviceTypeRequestDto;
	private RequestWrapper<SearchDto> machineSpecificationRequestDto;
	private RequestWrapper<SearchDto> templateRequestDto;
	private RequestWrapper<SearchDto> titleRequestDto;
	private RequestWrapper<SearchDto> holidayRequestDto;

	private DocumentType documentType;
	private List<DocumentType> documentTypes;
	private List<Machine> machines;
	private List<Device> devices;
	private RegistrationCenterType registrationCenterType;
	private List<RegistrationCenterType> registrationCenterTypes;

	@Before
	public void setup() throws JsonProcessingException {
		documentTypeSetUp();
		registrationCenterTypeSetUp();
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
		centerEntity.setLocationCode("LOC01");
		centerEntity.setName("Registration Center Name");
		centerEntity.setAddressLine1("address line1");
		centerEntity.setAddressLine2("address line2");
		centerEntity.setAddressLine3("address line3");
		centerEntity.setHolidayLocationCode("LOC03");
		centerEntity.setZoneCode("ZONE1");
		centerEntity.setIsActive(true);
		centerEntity.setLangCode("eng");

		locationRegionEntity = new Location("LOC01", "regionname", (short) 1, "region", "LOC00", "eng", null);
		locationProvinceEntity = new Location("LOC02", "provincename", (short) 2, "province", "LOC01", "eng", null);
		locationCityEntity = new Location("LOC03", "cityname", (short) 3, "city", "LOC02", "eng", null);
		locationLaaEntity = new Location("LOC04", "laa", (short) 4, "Local Administrative Authority", "LOC03", "eng",
				null);
		locationPostalCodeEntity = new Location("LOC05", "postalcode", (short) 5, "postal code", "LOC04", "eng", null);

		template = new Template();
		template.setDescription("aaaaa");
		template.setModuleId("10004");
		template.setLangCode("eng");
		title = new Title();
		title.setCode("MIR");
		title.setLangCode("eng");
		title.setTitleDescription("Male Title");
		title.setTitleName("Mr");

		request = new RequestWrapper<>();
		searchDto = new SearchDto();
		Pagination pagination = new Pagination(0, 10);
		searchDto.setLanguageCode("eng");
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList(sort));
		request.setRequest(searchDto);

		machineRequestDto = new RequestWrapper<>();
		machineSearchFilter = new SearchFilter();
		machineSearchFilter.setColumnName("name");
		machineSearchFilter.setType("equals");
		machineSearchFilter.setValue("Dekstop");
		machineSearchDto = new SearchDto();
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineSearchDto.setLanguageCode("eng");
		machineSearchDto.setSort(Arrays.asList());
		machineSearchDto.setPagination(pagination);
		machineRequestDto.setRequest(machineSearchDto);

		deviceRequestDto = new RequestWrapper<>();
		deviceSearchFilter = new SearchFilter();
		deviceSearchFilter.setColumnName("name");
		deviceSearchFilter.setType("equals");
		deviceSearchFilter.setValue("Dekstop");
		deviceSearchDto = new SearchDto();
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceSearchDto.setLanguageCode("eng");
		deviceSearchDto.setSort(Arrays.asList());
		deviceSearchDto.setPagination(pagination);
		deviceRequestDto.setRequest(deviceSearchDto);

		deviceTypeRequestDto = new RequestWrapper<>();
		deviceTypeSearchFilter = new SearchFilter();
		deviceTypeSearchFilter.setColumnName("name");
		deviceTypeSearchFilter.setType("equals");
		deviceTypeSearchFilter.setValue("Camera");
		deviceTypeSearchDto = new SearchDto();
		deviceTypeSearchDto.setFilters(Arrays.asList(deviceTypeSearchFilter));
		deviceTypeSearchDto.setLanguageCode("eng");
		deviceTypeSearchDto.setSort(Arrays.asList());
		deviceTypeSearchDto.setPagination(pagination);
		deviceTypeRequestDto.setRequest(deviceTypeSearchDto);

		machineSpecificationRequestDto = new RequestWrapper<>();
		machineSpecificationSearchFilter = new SearchFilter();
		machineSpecificationSearchFilter.setColumnName("name");
		machineSpecificationSearchFilter.setType("equals");
		machineSpecificationSearchFilter.setValue("Vostro");
		machineSpecificationSearchDto = new SearchDto();
		machineSpecificationSearchDto.setFilters(Arrays.asList(machineSpecificationSearchFilter));
		machineSpecificationSearchDto.setLanguageCode("eng");
		machineSpecificationSearchDto.setSort(Arrays.asList());
		machineSpecificationSearchDto.setPagination(pagination);
		machineSpecificationRequestDto.setRequest(machineSpecificationSearchDto);

		templateRequestDto = new RequestWrapper<>();
		templateSearchFilter = new SearchFilter();
		templateSearchFilter.setColumnName("moduleId");
		templateSearchFilter.setType("equals");
		templateSearchFilter.setValue("10004");
		templateSearchDto = new SearchDto();
		templateSearchDto.setFilters(Arrays.asList(templateSearchFilter));
		templateSearchDto.setLanguageCode("eng");
		templateSearchDto.setSort(Arrays.asList());
		templateSearchDto.setPagination(pagination);
		templateRequestDto.setRequest(templateSearchDto);

		titleRequestDto = new RequestWrapper<>();
		titleSearchFilter = new SearchFilter();
		titleSearchFilter.setColumnName("titleDescription");
		titleSearchFilter.setType("equals");
		titleSearchFilter.setValue("Male Title");
		titleSearchDto = new SearchDto();
		titleSearchDto.setFilters(Arrays.asList(titleSearchFilter));
		titleSearchDto.setLanguageCode("eng");
		titleSearchDto.setSort(Arrays.asList());
		titleSearchDto.setPagination(pagination);
		titleRequestDto.setRequest(titleSearchDto);

		docCatTypeRequestDto = new RequestWrapper<>();
		docCatTypeFilter = new SearchFilter();
		docCatTypeFilter.setColumnName("docCategoryCode");
		docCatTypeFilter.setType("equals");
		docCatTypeFilter.setValue("POI");
		docCatTypeDto = new SearchDto();
		docCatTypeDto.setFilters(Arrays.asList(docCatTypeFilter));
		docCatTypeDto.setLanguageCode("ara");
		docCatTypeDto.setSort(Arrays.asList());
		docCatTypeDto.setPagination(pagination);
		docCatTypeRequestDto.setRequest(docCatTypeDto);
		// TODO:
		holidayRequestDto = new RequestWrapper<>();
		holidayFilter = new SearchFilter();
		holidayFilter.setColumnName("hoidayId");
		holidayFilter.setType("equals");
		holidayFilter.setValue("2000070");
		holidaySearchDto = new SearchDto();
		holidaySearchDto.setFilters(Arrays.asList(docCatTypeFilter));
		holidaySearchDto.setLanguageCode("ara");
		holidaySearchDto.setSort(Arrays.asList());
		holidaySearchDto.setPagination(pagination);
		docCatTypeRequestDto.setRequest(holidaySearchDto);
		
		machines=new ArrayList<>();
		Machine machine=new Machine();
		machine.setId("10001");
		machine.setZoneCode("CST");
		machine.setMachineSpecId("1001");
		machine.setMapStatus("assigned");
		machine.setLangCode("eng");
		machines.add(machine);
				

		when(filterTypeValidator.validate(Mockito.eq(LocationExtnDto.class), Mockito.anyList())).thenReturn(true);
		when(filterTypeValidator.validate(Mockito.eq(RegistrationCenterTypeExtnDto.class), Mockito.anyList()))
				.thenReturn(true);
		when(filterTypeValidator.validate(Mockito.eq(RegistrationCenterSearchDto.class), Mockito.anyList()))
				.thenReturn(true);
		mockFilterValidator(TemplateExtnDto.class);
		mockFilterValidator(TitleExtnDto.class);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenter.class), Mockito.any(),
				Mockito.any())).thenReturn(new PageImpl<>(Arrays.asList(centerEntity), PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenterType.class), Mockito.any(),
				Mockito.any())).thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Template.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(template), PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Title.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(title), PageRequest.of(0, 10), 1));
		when(registrationCenterUserRepository.countCenterUsers(Mockito.any())).thenReturn(10l);
		when(registrationCenterMachineRepository.countCenterMachines(Mockito.any())).thenReturn(10l);
		when(registrationCenterDeviceRepository.countCenterDevices(Mockito.any())).thenReturn(10l);

		doReturn(new RegistrationCenterType("10001", "ENG", "Center Name", "Description", null))
				.when(registrationCenterTypeRepository).findByCodeAndLangCode(Mockito.any(), Mockito.any());
		when(locationRepository.findAllByLangCodeNonDeleted(Mockito.anyString()))
				.thenReturn(Arrays.asList(locationCityEntity, locationLaaEntity, locationPostalCodeEntity,
						locationProvinceEntity, locationRegionEntity));
		zoneUser = new ZoneUser("ZONE1", "zonal-admin", "eng");
		zones = new ArrayList<>();
		Zone zone1 = new Zone("ZONE1", "eng", "ZONE1 Name", (short) 1, "ZONENAME", null, "ZONE1");
		Zone zone2 = new Zone("ZONE2", "eng", "ZONE1 Name", (short) 1, "ZONENAME", "ZONE1", "ZONE1/ZONE2");
		Zone zone3 = new Zone("ZONE3", "eng", "ZONE1 Name", (short) 1, "ZONENAME", "ZONE2", "ZONE1/ZONE2/ZONE3");
		Zone zone4 = new Zone("ZONE4", "eng", "ZONE1 Name", (short) 1, "ZONENAME", "ZONE3", "ZONE1/ZONE2/ZONE3/ZONE4");
		zones.add(zone1);
		zones.add(zone2);
		zones.add(zone3);
		zones.add(zone4);
		when(zoneUserRepository.findByUserIdNonDeleted(Mockito.any())).thenReturn(Arrays.asList(zoneUser));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Zone.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(zone1), PageRequest.of(0, 10), 1));
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		zoneFilter = new SearchFilter();
		zoneFilter.setColumnName("zone");
		zoneFilter.setType("equals");
		zoneFilter.setValue("ZONE1");

	}

	private void documentTypeSetUp() {
		documentType = new DocumentType();
		documentType.setCode("DT001");
		documentTypes = new ArrayList<>();
		documentTypes.add(documentType);
	}

	private void registrationCenterTypeSetUp() {
		registrationCenterType = new RegistrationCenterType();
		registrationCenterTypes = new ArrayList<>();
		registrationCenterType.setCode("1001");
		registrationCenterType.setName("Ben");
		registrationCenterTypes.add(registrationCenterType);

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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenterType.class), Mockito.any(),
				Mockito.any())).thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 0));
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));

		searchDto.setFilters(Arrays.asList(filter3));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenterType.class), Mockito.any(),
				Mockito.any())).thenReturn(new PageImpl<>(Arrays.asList(centerTypeEntity), PageRequest.of(0, 10), 1));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchInvalidCenterTypeName() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenterType.class), Mockito.any(),
				Mockito.any())).thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterUserCountFailure() throws Exception {
		when(registrationCenterUserRepository.countCenterUsers(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterMachineCountFailure() throws Exception {
		when(registrationCenterMachineRepository.countCenterMachines(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterDevicesCountFailure() throws Exception {
		when(registrationCenterDeviceRepository.countCenterDevices(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegCenterTypeFailure() throws Exception {
		doThrow(DataRetrievalFailureException.class).when(registrationCenterTypeRepository)
				.findByCodeAndLangCode(Mockito.any(), Mockito.any());
		searchDto.setFilters(Arrays.asList(filter2));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchCenterByZoneName() throws Exception {
		searchDto.setFilters(Arrays.asList(zoneFilter));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchCenterByZoneNameFailure() throws Exception {
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Zone.class), Mockito.any(), Mockito.any()))
				.thenReturn(new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0));
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		searchDto.setFilters(Arrays.asList(zoneFilter));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(
				post("/registrationcenters/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isInternalServerError());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByMappedStatusFieldTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("assigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		when(machineRepository.findMappedMachineId(Mockito.anyString())).thenReturn(machineIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByMappedStatusFieldNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("assigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByNotMappedStatusFieldTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("unassigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList(machine));
		when(machineRepository.findNotMappedMachineId(Mockito.anyString())).thenReturn(machineIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByNotMappedStatusFieldNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("unassigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<String> machineIdList = new ArrayList<>();
		machineIdList.add("1001");
		Machine machine = new Machine();
		machine.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		Page<Machine> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineRequestExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("mapStatus");
		machineSearchFilter.setValue("invalidValue");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByMachineTypeNameTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList(type));
		MachineSpecification specification = new MachineSpecification();
		specification.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByMachineTypeNameNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList());
		MachineSpecification specification = new MachineSpecification();
		specification.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineTypeDto.class), Mockito.anyList())).thenReturn(true);
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	
	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineByMachineTypeNameWithCorrespondingSpecificationIdNotFoundExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Object[] object= {"1001"};
		List<Object[]> machineSpec= new ArrayList<>();
		machineSpec.add(object);
		when(filterTypeValidator.validate(Mockito.eq(MachineTypeDto.class), Mockito.anyList())).thenReturn(true);
		when(machineRepository.findMachineSpecByMachineTypeNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machineSpec);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
		.thenReturn(new PageImpl<>(machines,  PageRequest.of(0, 10), 1));
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void searchMapStatusAndDeviceTypeName() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		SearchFilter searchFilter= new SearchFilter();
		searchFilter.setColumnName("mapStatus");
		searchFilter.setValue("unAssigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter,searchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Object[] object= {"1001"};
		List<Object[]> machineSpec= new ArrayList<>();
		machineSpec.add(object);
		when(machineRepository.findNotMappedMachineId(Mockito.anyString())).thenReturn(Arrays.asList("1001","10002"));
		when(filterTypeValidator.validate(Mockito.eq(MachineTypeDto.class), Mockito.anyList())).thenReturn(true);
		when(machineRepository.findMachineSpecByMachineTypeNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machineSpec);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
		.thenReturn(new PageImpl<>(machines,  PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.nativeMachineQuerySearch(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.anyBoolean())).thenReturn(new PageImpl<>(machines, PageRequest.of(0, 10), 1));
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void zoneNullTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		SearchFilter searchFilter= new SearchFilter();
		searchFilter.setColumnName("mapStatus");
		searchFilter.setValue("unAssigned");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter,searchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Object[] object= {"1001"};
		List<Object[]> machineSpec= new ArrayList<>();
		machineSpec.add(object);
		when(machineRepository.findNotMappedMachineId(Mockito.anyString())).thenReturn(Arrays.asList("1001","10002"));
		when(filterTypeValidator.validate(Mockito.eq(MachineTypeDto.class), Mockito.anyList())).thenReturn(true);
		when(machineRepository.findMachineSpecByMachineTypeNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machineSpec);
		when(zoneUserRepository.findByUserIdNonDeleted(Mockito.any())).thenReturn(Arrays.asList(zoneUser));
		doReturn(null).when(zoneRepository).findAllNonDeleted();
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void MachineSpecExceptionTest() throws Exception {
		machineSearchFilter.setColumnName("machineTypeName");
		machineSearchFilter.setValue("Desktop");
		SearchFilter searchFilter= new SearchFilter();
		searchFilter.setColumnName("mapStatus");
		searchFilter.setValue("unAssigne");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter,searchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Object[] object= {"1001"};
		List<Object[]> machineSpec= new ArrayList<>();
		machineSpec.add(object);
		when(filterTypeValidator.validate(Mockito.eq(MachineTypeDto.class), Mockito.anyList())).thenReturn(true);
		when(machineRepository.findMachineSpecByMachineTypeNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machineSpec);
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void searchByZone() throws Exception{
		machineSearchFilter.setColumnName("zone");
		machineSearchFilter.setValue("CST");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Zone.class),
				Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(zones, PageRequest.of(0, 10), 1));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
		.thenReturn(new PageImpl<>(machines,  PageRequest.of(0, 10), 1));
		mockMvc.perform(post("/machines/search").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}
	
	

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceTest() throws Exception {
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceTestMapStatusException() throws Exception {
		SearchFilter search=new SearchFilter();
		search.setColumnName("mapStatus");
		search.setValue("unAssi");
		search.setType("equals");
		deviceRequestDto.getRequest().setFilters(Arrays.asList(search));
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void ZoneDeviceNullTest() throws Exception {
		
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(zoneUserRepository.findByUserIdNonDeleted(Mockito.any())).thenReturn(Arrays.asList(zoneUser));
		doReturn(null).when(zoneRepository).findAllNonDeleted();
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void UnAssignedAndDeviceTypeNameCheck() throws Exception {
		
		SearchFilter deviceSearchFilter= new SearchFilter();
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Printer");
		deviceSearchFilter.setType("equals");
		SearchFilter deviceSearchFilter2= new SearchFilter();
		deviceSearchFilter2.setColumnName("mapStatus");
		deviceSearchFilter2.setValue("UnAssigned");
		deviceSearchFilter2.setType("equals");
		deviceRequestDto.getRequest().setFilters(Arrays.asList(deviceSearchFilter,deviceSearchFilter2));
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1001");
		device.setDeviceSpecId("10002");
		device.setMacAddress("12:r1:6t:as:7u");
		device.setLangCode("eng");
		device.setSerialNum("sdsddsfsf");
		Object[] object= {"10002"};
		List<Object[]> devspecs=new ArrayList<>();
		devspecs.add(object);
		List<Device> devices= Arrays.asList(device);
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(deviceRepository.findNotMappedDeviceId(Mockito.anyString())).thenReturn(Arrays.asList("1001"));
		when(deviceRepository
							.findDeviceSpecByDeviceTypeNameAndLangCode(Mockito.anyString(),Mockito.anyString())).thenReturn(devspecs);
		when(masterdataSearchHelper.nativeDeviceQuerySearch(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.anyBoolean())).thenReturn(new PageImpl<>(devices, PageRequest.of(0, 10), 1));
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByMappedStatusFieldTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(deviceRepository.findMappedDeviceId(deviceSearchDto.getLanguageCode())).thenReturn(deviceIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByMappedStatusFieldNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("assigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByNotMappedStatusFieldTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("unassigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(deviceRepository.findNotMappedDeviceId(deviceSearchDto.getLanguageCode())).thenReturn(deviceIdList);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByNotMappedStatusFieldNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("unassigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		List<String> deviceIdList = new ArrayList<>();
		deviceIdList.add("1001");
		Device device = new Device();
		device.setId("1001");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceRequestExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("mapStatus");
		deviceSearchFilter.setValue("unassigned");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByDeviceTypeNameTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		DeviceType type = new DeviceType();
		type.setCode("deviceCode");
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList(type));
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByDeviceTypeNameNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList());
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		/*
		 * when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.
		 * class), Mockito.any(),
		 * Mockito.any())).thenReturn(pageContentSpecificationData);
		 */
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceByDeviceTypeNameWithCorrespondingSpecificationIdNotFoundExceptionTest() throws Exception {
		deviceSearchFilter.setColumnName("deviceTypeName");
		deviceSearchFilter.setValue("Desktop");
		deviceSearchDto.setFilters(Arrays.asList(deviceSearchFilter));
		deviceRequestDto.setRequest(deviceSearchDto);
		String json = objectMapper.writeValueAsString(deviceRequestDto);
		Device device = new Device();
		device.setId("1100022");
		Page<Device> pageContentData = new PageImpl<>(Arrays.asList(device));
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList());
		/*
		 * when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class),
		 * Mockito.any(), Mockito.any())) .thenReturn(pageContentData);
		 * when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.
		 * class), Mockito.any(),
		 * Mockito.any())).thenReturn(pageContentSpecificationData);
		 */
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devices/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchBlackListedWordsTest() throws Exception {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setColumnName("word");
		searchFilter.setType("equals");
		searchFilter.setValue("damn");
		SearchDto searchDto = new SearchDto();
		searchDto.setFilters(Arrays.asList(searchFilter));
		searchDto.setLanguageCode("eng");
		Pagination pagination = new Pagination();
		pagination.setPageFetch(5);
		pagination.setPageStart(0);
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList());
		request.setRequest(searchDto);
		String json = objectMapper.writeValueAsString(request);
		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("BlackListedWord");
		Page<BlacklistedWords> pageContentData = new PageImpl<>(Arrays.asList(blacklistedWords));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/blacklistedwords/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterMachineTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("text");
		filterDto.setText("re");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Machine.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("machineName", "secondMachineName"));
		mockMvc.perform(post("/machines/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterBlackListedWordsTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("word");
		filterDto.setType("all");
		// filterDto.setText("re");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterBlackListedWordsTestForArabicLanguage() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("ÙƒÙ„Ù…Ø© ÙÙŠ Ø§Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„Ø³ÙˆØ¯Ø§Ø¡");
		filterDto.setType("all");
		// filterDto.setText("السوداء");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("ara");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterBlackListedWordsTestForInvalidLanguageCode() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("invalid");
		filterDto.setType("all");
		// filterDto.setText("rd");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("inv");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(BlacklistedWords.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/blacklistedwords/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterDeviceTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("rd");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Device.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("deviceName", "secondDeviceName"));
		mockMvc.perform(post("/devices/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterDocumentTypeTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("rd");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DocumentType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("Birth Certificate", "Canteen card of the Army", "Certificate of residence"));
		mockMvc.perform(post("/documenttypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
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
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DocumentType.class), Mockito.any(SearchDto.class),
				Mockito.any())).thenReturn(page);
		mockMvc.perform(post("/documenttypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDocumentTypeFailureTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("qwx");
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
		List<ServiceError> errors = new ArrayList<ServiceError>();
		ServiceError error = new ServiceError();
		errors.add(error);

		String contentJson = objectMapper.writeValueAsString(requestDto);
		Page<DocumentType> page = new PageImpl<>(documentTypes, PageRequest.of(0, 10), 1);

		when(filterTypeValidator.validate(Mockito.eq(DocumentTypeExtnDto.class), Mockito.anyList()))
				.thenThrow(new ValidationException(errors));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DocumentType.class), Mockito.any(SearchDto.class),
				Mockito.any())).thenReturn(page);
		mockMvc.perform(post("/documenttypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineTypesTest() throws Exception {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setColumnName("name");
		searchFilter.setType("equals");
		searchFilter.setValue("Dekstop");
		SearchDto searchDto = new SearchDto();
		searchDto.setFilters(Arrays.asList(searchFilter));
		searchDto.setLanguageCode("eng");
		Pagination pagination = new Pagination();
		pagination.setPageFetch(5);
		pagination.setPageStart(0);
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList());
		request.setRequest(searchDto);
		String json = objectMapper.writeValueAsString(request);
		MachineType machineTypes = new MachineType();
		machineTypes.setCode("1001");
		machineTypes.setName("Dekstop");
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList(machineTypes));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machinetypes/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceTypeTest() throws Exception {
		String json = objectMapper.writeValueAsString(deviceTypeRequestDto);
		DeviceType deviceType = new DeviceType();
		deviceType.setCode("1001");
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList(deviceType));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/devicetypes/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterDeviceTypeTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("deviceTypeName", "secondDeviceTypeName"));
		mockMvc.perform(post("/devicetypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchtemplate() throws Exception {

		String validRequest = objectMapper.writeValueAsString(templateRequestDto);
		mockMvc.perform(post("/templates/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchTitles() throws Exception {
		String validRequest = objectMapper.writeValueAsString(titleRequestDto);
		mockMvc.perform(post("/title/search").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterTemplate() throws Exception {
		RequestWrapper<FilterValueDto> request = new RequestWrapper<>();
		FilterValueDto filterValueDto = new FilterValueDto();
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("moduleId");
		filterDto.setText("10004");
		filterDto.setType("all");
		filterValueDto.setLanguageCode("eng");
		filterValueDto.setFilters(Arrays.asList(filterDto));
		request.setRequest(filterValueDto);
		Template temp = new Template();
		temp.setModuleId("10004");
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(ArgumentMatchers.<Class<Template>>any(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(template));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(post("/templates/filtervalues").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterTitle() throws Exception {
		RequestWrapper<FilterValueDto> request = new RequestWrapper<>();
		FilterValueDto filterValueDto = new FilterValueDto();
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("code");
		filterDto.setText("MIR");
		filterDto.setType("all");
		filterValueDto.setLanguageCode("eng");
		filterValueDto.setFilters(Arrays.asList(filterDto));
		request.setRequest(filterValueDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(ArgumentMatchers.<Class<Title>>any(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(template));
		String validRequest = objectMapper.writeValueAsString(request);
		mockMvc.perform(post("/title/filtervalues").contentType(MediaType.APPLICATION_JSON).content(validRequest))
				.andExpect(status().isOk());
	}

	public <T, clazz> void mockFilterValidator(Class<T> clazz) {
		when(filterTypeValidator.validate(ArgumentMatchers.<Class<clazz>>any(), Mockito.anyList())).thenReturn(true);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterMachineTypeTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("Dekk");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("name", "Dekstop"));
		mockMvc.perform(post("/machinetypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDevicespecTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		DeviceSpecification spec = new DeviceSpecification();
		spec.setId("1001");
		Page<DeviceSpecification> pageContentData = new PageImpl<>(Arrays.asList(spec));
		when(filterTypeValidator.validate(Mockito.eq(DeviceSpecificationExtnDto.class), Mockito.anyList()))
				.thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentData);
		mockMvc.perform(post("/devicespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDeviceSpecByDeviceTypeNameTest() throws Exception {
		machineSearchFilter.setColumnName("deviceTypeName");
		machineSearchFilter.setValue("Desktop");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		DeviceType type = new DeviceType();
		type.setCode("machineCode");
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList(type));
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(MachineSearchDto.class), Mockito.anyList())).thenReturn(true);
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devicespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void deadads() throws Exception {
		machineSearchFilter.setColumnName("deviceTypeName");
		machineSearchFilter.setValue("Camera");
		machineSearchDto.setFilters(Arrays.asList(machineSearchFilter));
		machineRequestDto.setRequest(machineSearchDto);
		String json = objectMapper.writeValueAsString(machineRequestDto);
		Page<DeviceType> pageContentData = new PageImpl<>(Arrays.asList());
		DeviceSpecification specification = new DeviceSpecification();
		specification.setId("1001");
		when(filterTypeValidator.validate(Mockito.eq(DeviceTypeDto.class), Mockito.anyList())).thenReturn(true);
		Page<DeviceSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DeviceSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/devicespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void deviceSpecFilterTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("re");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DeviceType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("name", "Regular"));
		mockMvc.perform(
				post("/devicespecifications/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterDeviceSpecTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("text");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DeviceSpecification.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("machineName", "secondMachineName"));
		mockMvc.perform(
				post("/devicespecifications/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json));
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterGenderTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("genderName");
		filterDto.setText("MALE");
		filterDto.setType("all");
		filterDto.setText("rd");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(DocumentCategory.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("DC001", "DC002", "DC003"));
		mockMvc.perform(post("/documentcategories/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterGenderTestForArabicLanguage() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("genderName");
		filterDto.setText("ÙƒÙ„Ù…Ø© ÙÙŠ Ø§Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„Ø³ÙˆØ¯Ø§Ø¡");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("ara");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Gender.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("ÙƒÙ„Ù…Ø© ÙÙŠ Ø§Ù„Ù‚Ø§Ø¦Ù…Ø© Ø§Ù„Ø³ÙˆØ¯Ø§Ø¡"));
		mockMvc.perform(post("/gendertypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterGenderTestForInvalidLanguageCode() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setText("genderName");
		filterDto.setColumnName("invalid");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("inv");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		mockMvc.perform(post("/gendertypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchGenderTest() throws Exception {
		SearchDto searchDto = new SearchDto();
		searchDto.setFilters(Arrays.asList());
		searchDto.setLanguageCode("eng");
		Pagination pagination = new Pagination();
		pagination.setPageFetch(5);
		pagination.setPageStart(0);
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList());
		request.setRequest(searchDto);
		String json = objectMapper.writeValueAsString(request);
		Gender gender = new Gender();
		gender.setCode("MLE");
		gender.setGenderName("MALE");
		gender.setLangCode("eng");
		;
		Page<Gender> pageContentData = new PageImpl<>(Arrays.asList(gender));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Gender.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/gendertypes/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchDocCatTypeMappingTest() throws Exception {
		docCatTypeFilter.setColumnName("docCategoryCode");
		docCatTypeFilter.setValue("POI");
		docCatTypeDto.setFilters(Arrays.asList(docCatTypeFilter));
		docCatTypeRequestDto.setRequest(docCatTypeDto);
		String json = objectMapper.writeValueAsString(docCatTypeRequestDto);
		ValidDocument validDoc = new ValidDocument();
		validDoc.setDocCategoryCode("POI");

		// MachineType type = new MachineType();
		// type.setCode("machineCode");
		Page<ValidDocument> pageContentData = new PageImpl<>(Arrays.asList(validDoc));
		DocumentCategory docCategory = new DocumentCategory();
		docCategory.setCode("POI");
		DocumentType docType = new DocumentType();
		docType.setCode("CIN");
		Page<DocumentCategory> docCategoryData = new PageImpl<>(Arrays.asList(docCategory));
		Page<DocumentType> docCatData = new PageImpl<>(Arrays.asList(docType));

		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(ValidDocument.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DocumentCategory.class), Mockito.any(), Mockito.any()))
				.thenReturn(docCategoryData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(DocumentType.class), Mockito.any(), Mockito.any()))
				.thenReturn(docCatData);
		mockMvc.perform(post("/validdocuments/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterDocCatTypeMappingTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("docCategoryCode");
		filterDto.setType("all");
		filterDto.setText("POI");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(ValidDocument.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("docCategoryCode", "POI"));
		mockMvc.perform(post("/validdocuments/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterCenterById() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("id");
		filterDto.setType("all");
		filterDto.setText("100");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(RegistrationCenter.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("10001", "10002", "10003"));
		mockMvc.perform(post("/registrationcenters/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterCenterByName() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("unique");
		filterDto.setText("Center");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(RegistrationCenter.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("10001", "10002", "10003"));
		mockMvc.perform(post("/registrationcenters/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegistrationCenterTypeTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("qxt");
		searchFilter1.setValue("Ben");
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
		Page<RegistrationCenterType> page = new PageImpl<>(registrationCenterTypes, PageRequest.of(0, 10), 1);
		when(masterdataSearchHelper.searchMasterdata(ArgumentMatchers.<Class<RegistrationCenterType>>any(),
				Mockito.any(SearchDto.class), Mockito.any())).thenReturn(page);
		mockMvc.perform(
				post("/registrationcentertypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchRegistrationCenterTypeFailureTest() throws Exception {
		RequestWrapper<SearchDto> requestDto = new RequestWrapper<>();
		SearchDto searchDto = new SearchDto();
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		SearchFilter searchFilter1 = new SearchFilter();
		searchFilter1.setColumnName("name");
		searchFilter1.setType("qwx");
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
		List<ServiceError> errors = new ArrayList<ServiceError>();
		ServiceError error = new ServiceError();
		errors.add(error);

		String contentJson = objectMapper.writeValueAsString(requestDto);
		Page<RegistrationCenterType> page = new PageImpl<>(registrationCenterTypes, PageRequest.of(0, 10), 1);

		when(filterTypeValidator.validate(Mockito.eq(RegistrationCenterType.class), Mockito.anyList()))
				.thenThrow(new ValidationException(errors));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(RegistrationCenterType.class),
				Mockito.any(SearchDto.class), Mockito.any())).thenReturn(page);
		mockMvc.perform(
				post("/registrationcentertypes/search").contentType(MediaType.APPLICATION_JSON).content(contentJson))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void registrationCenterTypeFilterTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		filterDto.setText("re");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(RegistrationCenterType.class), Mockito.any(),
				Mockito.any())).thenReturn(Arrays.asList("name", "Regular"));
		mockMvc.perform(
				post("/registrationcentertypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchIndividualTypeTest() throws Exception {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.setColumnName("code");
		searchFilter.setType("equals");
		searchFilter.setValue("FR");
		SearchDto searchDto = new SearchDto();
		searchDto.setFilters(Arrays.asList(searchFilter));
		searchDto.setLanguageCode("eng");
		Pagination pagination = new Pagination();
		pagination.setPageFetch(5);
		pagination.setPageStart(0);
		searchDto.setPagination(pagination);
		searchDto.setSort(Arrays.asList());
		request.setRequest(searchDto);
		String json = objectMapper.writeValueAsString(request);
		// BlacklistedWords blacklistedWords = new BlacklistedWords();
		IndividualType individual = new IndividualType();
		individual.setCode("code");
		// blacklistedWords.setWord("BlackListedWord");
		Page<IndividualType> pageContentData = new PageImpl<>(Arrays.asList(individual));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(IndividualType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/individualtypes/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterIndividualTypeTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("code");
		filterDto.setType("all");
		filterDto.setText("F");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(IndividualType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("FR", "NFR"));
		mockMvc.perform(post("/individualtypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterIndividualTestForInvalidLanguageCode() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("invalid");
		filterDto.setType("all");
		filterDto.setText("F");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("inv");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(IndividualType.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("damn", "dammit"));
		mockMvc.perform(post("/individualtypes/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineSpecificationTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineSpecificationRequestDto);
		MachineSpecification machineSpecification = new MachineSpecification();
		machineSpecification.setId("1001");
		Page<MachineSpecification> pageContentData = new PageImpl<>(Arrays.asList(machineSpecification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentData);
		mockMvc.perform(post("/machinespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineSpecificationByMachineTypeNameTest() throws Exception {
		machineSpecificationSearchFilter.setColumnName("machineTypeName");
		machineSpecificationSearchFilter.setValue("Vostro");
		machineSpecificationSearchDto.setFilters(Arrays.asList(machineSpecificationSearchFilter));
		machineSpecificationRequestDto.setRequest(machineSpecificationSearchDto);
		String json = objectMapper.writeValueAsString(machineSpecificationRequestDto);
		MachineType type = new MachineType();
		type.setCode("machineCode");
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList(type));
		MachineSpecification specification = new MachineSpecification();
		specification.setId("1001");
		Page<MachineSpecification> pageContentSpecificationData = new PageImpl<>(Arrays.asList(specification));
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineSpecification.class), Mockito.any(),
				Mockito.any())).thenReturn(pageContentSpecificationData);
		mockMvc.perform(post("/machinespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchMachineSpecificationByMachineTypeNameNotFoundExceptionTest() throws Exception {
		machineSpecificationSearchFilter.setColumnName("machineTypeName");
		machineSpecificationSearchFilter.setValue("Vostro");
		machineSpecificationSearchDto.setFilters(Arrays.asList(machineSpecificationSearchFilter));
		machineSpecificationRequestDto.setRequest(machineSpecificationSearchDto);
		String json = objectMapper.writeValueAsString(machineSpecificationRequestDto);
		Page<MachineType> pageContentData = new PageImpl<>(Arrays.asList());
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(MachineType.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/machinespecifications/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterMachineSpecificationTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("name");
		filterDto.setType("all");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(masterDataFilterHelper.filterValues(Mockito.eq(MachineSpecification.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("machineSpecificationName", "secondMachineSpecificationName"));
		mockMvc.perform(
				post("/machinespecifications/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	// TODO:
	/* holiday search test case */
	@Test
	@WithUserDetails("zonal-admin")
	public void searchHolidayTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<Holiday> holidays = new ArrayList<Holiday>();
		Holiday holiday1 = new Holiday();
		holiday1.setHolidayId(2000001);
		holiday1.setHolidayName("New Year's Day");
		holiday1.setHolidayDesc("National Holiday");
		holiday1.setLangCode("eng");
		holiday1.setHolidayDate(LocalDate.now());
		holiday1.setLocationCode("KTA");
		holidays.add(holiday1);
		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(2000001);
		holiday2.setHolidayName("New Year's Day");
		holiday2.setHolidayDesc("National Holiday");
		holiday2.setLangCode("eng");
		holiday2.setHolidayDate(LocalDate.now());
		holiday2.setLocationCode("RBT");
		holidays.add(holiday2);

		Location location1 = new Location("KNT", "Kenitra", (short) 2, "Province", "RSK", "eng", null);
		Location location2 = new Location("RBT", "Rabat", (short) 2, "province", "RSK", "eng", null);
		List<Location> locations = new ArrayList<>();
		locations.add(location1);
		locations.add(location2);

		Page<Holiday> pageContentData = new PageImpl<>(holidays);
		Page<Location> locpageContentData = new PageImpl<>(locations);
		when(locationRepository.findByLangCode(Mockito.anyString())).thenReturn(locations);
		when(filterTypeValidator.validate(Mockito.eq(LocationDto.class), Mockito.anyList())).thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
				.thenReturn(locpageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Holiday.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/holidays/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchHolidayByLocationNotFoundTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<Holiday> holidays = new ArrayList<Holiday>();
		Holiday holiday1 = new Holiday();
		holiday1.setHolidayId(2000001);
		holiday1.setHolidayName("New Year's Day");
		holiday1.setHolidayDesc("National Holiday");
		holiday1.setLangCode("eng");
		holiday1.setHolidayDate(LocalDate.now());
		holiday1.setLocationCode("KTA");
		holidays.add(holiday1);
		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(2000001);
		holiday2.setHolidayName("New Year's Day");
		holiday2.setHolidayDesc("National Holiday");
		holiday2.setLangCode("eng");
		holiday2.setHolidayDate(LocalDate.now());
		holiday2.setLocationCode("RBT");
		holidays.add(holiday2);

		List<Location> locations = new ArrayList<>();

		Page<Holiday> pageContentData = new PageImpl<>(holidays);
		Page<Location> locpageContentData = new PageImpl<>(locations);
		when(locationRepository.findByLangCode(Mockito.anyString())).thenReturn(locations);
		when(filterTypeValidator.validate(Mockito.eq(LocationDto.class), Mockito.anyList())).thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
				.thenReturn(locpageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Holiday.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/holidays/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void searchHolidayByLocationFetchExpTest() throws Exception {
		String json = objectMapper.writeValueAsString(machineRequestDto);
		List<Holiday> holidays = new ArrayList<Holiday>();
		Holiday holiday1 = new Holiday();
		holiday1.setHolidayId(2000001);
		holiday1.setHolidayName("New Year's Day");
		holiday1.setHolidayDesc("National Holiday");
		holiday1.setLangCode("eng");
		holiday1.setHolidayDate(LocalDate.now());
		holiday1.setLocationCode("KTA");
		holidays.add(holiday1);
		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(2000001);
		holiday2.setHolidayName("New Year's Day");
		holiday2.setHolidayDesc("National Holiday");
		holiday2.setLangCode("eng");
		holiday2.setHolidayDate(LocalDate.now());
		holiday2.setLocationCode("RBT");
		holidays.add(holiday2);

		List<Location> locations = new ArrayList<>();

		Page<Holiday> pageContentData = new PageImpl<>(holidays);
		Page<Location> locpageContentData = new PageImpl<>(locations);
		when(locationRepository.findByLangCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		when(filterTypeValidator.validate(Mockito.eq(LocationDto.class), Mockito.anyList())).thenReturn(true);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Location.class), Mockito.any(), Mockito.any()))
				.thenReturn(locpageContentData);
		when(masterdataSearchHelper.searchMasterdata(Mockito.eq(Holiday.class), Mockito.any(), Mockito.any()))
				.thenReturn(pageContentData);
		mockMvc.perform(post("/holidays/search").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void filterHolidayTest() throws Exception {
		FilterDto filterDto = new FilterDto();
		filterDto.setColumnName("isActive");
		filterDto.setType("all");
		filterDto.setText("true");
		FilterValueDto filterValueDto = new FilterValueDto();
		filterValueDto.setFilters(Arrays.asList(filterDto));
		filterValueDto.setLanguageCode("eng");
		RequestWrapper<FilterValueDto> requestDto = new RequestWrapper<>();
		requestDto.setRequest(filterValueDto);
		String json = objectMapper.writeValueAsString(requestDto);
		when(filterColumnValidator.validate(Mockito.eq(FilterDto.class), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		when(masterDataFilterHelper.filterValues(Mockito.eq(Holiday.class), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList("true", "false"));
		mockMvc.perform(post("/holidays/filtervalues").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

}
