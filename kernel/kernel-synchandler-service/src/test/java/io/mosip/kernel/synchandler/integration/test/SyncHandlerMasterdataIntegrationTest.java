package io.mosip.kernel.synchandler.integration.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.synchandler.entity.Application;
import io.mosip.kernel.synchandler.entity.BiometricAttribute;
import io.mosip.kernel.synchandler.entity.BiometricType;
import io.mosip.kernel.synchandler.entity.BlacklistedWords;
import io.mosip.kernel.synchandler.entity.Device;
import io.mosip.kernel.synchandler.entity.DeviceSpecification;
import io.mosip.kernel.synchandler.entity.DeviceType;
import io.mosip.kernel.synchandler.entity.DocumentCategory;
import io.mosip.kernel.synchandler.entity.DocumentType;
import io.mosip.kernel.synchandler.entity.Gender;
import io.mosip.kernel.synchandler.entity.Holiday;
import io.mosip.kernel.synchandler.entity.IdType;
import io.mosip.kernel.synchandler.entity.Language;
import io.mosip.kernel.synchandler.entity.Location;
import io.mosip.kernel.synchandler.entity.Machine;
import io.mosip.kernel.synchandler.entity.MachineSpecification;
import io.mosip.kernel.synchandler.entity.MachineType;
import io.mosip.kernel.synchandler.entity.ReasonCategory;
import io.mosip.kernel.synchandler.entity.ReasonList;
import io.mosip.kernel.synchandler.entity.RegistrationCenter;
import io.mosip.kernel.synchandler.entity.RegistrationCenterType;
import io.mosip.kernel.synchandler.entity.Template;
import io.mosip.kernel.synchandler.entity.TemplateFileFormat;
import io.mosip.kernel.synchandler.entity.TemplateType;
import io.mosip.kernel.synchandler.entity.Title;
import io.mosip.kernel.synchandler.entity.ValidDocument;
import io.mosip.kernel.synchandler.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.synchandler.entity.id.HolidayID;
import io.mosip.kernel.synchandler.repository.ApplicationRepository;
import io.mosip.kernel.synchandler.repository.BiometricAttributeRepository;
import io.mosip.kernel.synchandler.repository.BiometricTypeRepository;
import io.mosip.kernel.synchandler.repository.BlacklistedWordsRepository;
import io.mosip.kernel.synchandler.repository.DeviceRepository;
import io.mosip.kernel.synchandler.repository.DeviceSpecificationRepository;
import io.mosip.kernel.synchandler.repository.DeviceTypeRepository;
import io.mosip.kernel.synchandler.repository.DocumentCategoryRepository;
import io.mosip.kernel.synchandler.repository.DocumentTypeRepository;
import io.mosip.kernel.synchandler.repository.GenderRepository;
import io.mosip.kernel.synchandler.repository.HolidayRepository;
import io.mosip.kernel.synchandler.repository.IdTypeRepository;
import io.mosip.kernel.synchandler.repository.LanguageRepository;
import io.mosip.kernel.synchandler.repository.LocationRepository;
import io.mosip.kernel.synchandler.repository.MachineRepository;
import io.mosip.kernel.synchandler.repository.MachineSpecificationRepository;
import io.mosip.kernel.synchandler.repository.MachineTypeRepository;
import io.mosip.kernel.synchandler.repository.ReasonCategoryRepository;
import io.mosip.kernel.synchandler.repository.ReasonListRepository;
import io.mosip.kernel.synchandler.repository.RegistrationCenterRepository;
import io.mosip.kernel.synchandler.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.synchandler.repository.TemplateFileFormatRepository;
import io.mosip.kernel.synchandler.repository.TemplateRepository;
import io.mosip.kernel.synchandler.repository.TemplateTypeRepository;
import io.mosip.kernel.synchandler.repository.TitleRepository;
import io.mosip.kernel.synchandler.repository.ValidDocumentRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncHandlerMasterdataIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private List<Application> applications;
	private List<Machine> machines;
	private List<MachineSpecification> machineSpecification;
	private List<MachineType> machineType;
	private List<RegistrationCenter> registrationCenters;
	private List<RegistrationCenterType> registrationCenterType;
	private List<Device> devices;
	private List<DeviceSpecification> deviceSpecification;
	private List<DeviceType> deviceType;
	private List<Holiday> holidays;
	private List<BlacklistedWords> blackListedWords;
	private List<Title> titles;
	private List<Gender> genders;
	private List<Language> languages;
	private List<Template> templates;
	private List<TemplateFileFormat> templateFileFormats;
	private List<TemplateType> templateTypes;
	private List<BiometricAttribute> biometricAttributes;
	private List<BiometricType> biometricTypes;
	private List<DocumentCategory> documentCategories;
	private List<DocumentType> documentTypes;
	private List<ValidDocument> validDocuments;
	private List<ReasonCategory> reasonCategories;
	private List<ReasonList> reasonLists;
	private List<IdType> idTypes;
	private List<Location> locations;

	@MockBean
	private ApplicationRepository applicationRepository;
	@MockBean
	private MachineRepository machineRepository;
	@MockBean
	private MachineTypeRepository machineTypeRepository;
	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;
	@MockBean
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;
	@MockBean
	private TemplateRepository templateRepository;
	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;
	@MockBean
	private ReasonCategoryRepository reasonCategoryRepository;
	@MockBean
	private HolidayRepository holidayRepository;
	@MockBean
	private BlacklistedWordsRepository blacklistedWordsRepository;
	@MockBean
	private BiometricTypeRepository biometricTypeRepository;
	@MockBean
	private BiometricAttributeRepository biometricAttributeRepository;
	@MockBean
	private TitleRepository titleRepository;
	@MockBean
	private LanguageRepository languageRepository;
	@MockBean
	private GenderRepository genderTypeRepository;
	@MockBean
	private DeviceRepository deviceRepository;
	@MockBean
	private DocumentCategoryRepository documentCategoryRepository;
	@MockBean
	private DocumentTypeRepository documentTypeRepository;
	@MockBean
	private IdTypeRepository idTypeRepository;
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;
	@MockBean
	private LocationRepository locationRepository;
	@MockBean
	private TemplateTypeRepository templateTypeRepository;
	@MockBean
	private MachineSpecificationRepository machineSpecificationRepository;
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	@MockBean
	private ValidDocumentRepository validDocumentRepository;
	@MockBean
	private ReasonListRepository reasonListRepository;

	@Before
	public void setup() {
		LocalDateTime localdateTime = LocalDateTime.parse("2018-11-01T01:01:01");
		LocalTime localTime = LocalTime.parse("09:00:00");
		applications = new ArrayList<>();
		applications.add(new Application("101", "ENG", "MOSIP", "MOSIP"));
		machines = new ArrayList<>();
		machines.add(new Machine("1001", "Laptop", "9876427", "172.12.01.128", "21:21:21:12", "1001", "ENG",
				localdateTime, null));
		machineSpecification = new ArrayList<>();
		machineSpecification.add(
				new MachineSpecification("1001", "Laptop", "Lenovo", "T480", "1001", "1.0", "Laptop", "ENG", null));
		machineType = new ArrayList<>();
		machineType.add(new MachineType("1001", "ENG", "System", "System"));
		devices = new ArrayList<>();
		devices.add(new Device("1011", "printer", "123", "127.0.0.122", "213:21:132:312", "1011", "ENG", localdateTime,
				true, "moisp", localdateTime, null, null, null, null));
		deviceSpecification = new ArrayList<>();
		deviceSpecification.add(new DeviceSpecification("1011", "SP-1011", "HP", "E1011", "T1011", "1.0", "HP-SP1011",
				"Hp Printer", null));
		deviceType = new ArrayList<>();
		deviceType.add(new DeviceType("T1011", "ENG", "device", "deviceDescriptiom"));
		registrationCenters = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1011");
		registrationCenter.setAddressLine1("address-line1");
		registrationCenter.setAddressLine2("address-line2");
		registrationCenter.setAddressLine3("address-line3");
		registrationCenter.setCenterEndTime(localTime);
		registrationCenter.setCenterStartTime(localTime);
		registrationCenter.setCenterTypeCode("T1011");
		registrationCenter.setContactPerson("admin");
		registrationCenter.setContactPhone("9865123456");
		registrationCenter.setHolidayLocationCode("LOC01");
		registrationCenter.setIsActive(true);
		registrationCenter.setLanguageCode("ENG");
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLunchEndTime(localTime);
		registrationCenter.setLunchStartTime(localTime);
		registrationCenters.add(registrationCenter);

		registrationCenterType = new ArrayList<>();
		registrationCenterType.add(new RegistrationCenterType("T1011", "ENG", "Main", "Main"));
		templates = new ArrayList<>();
		templates.add(new Template("T1", "ENG", "Email-Template", "Email-Template", "F101", "m", "text", "M101",
				"ModuleName", "T101"));
		templateFileFormats = new ArrayList<>();
		templateFileFormats.add(new TemplateFileFormat("T101", "ENG", "Email"));
		templateTypes = new ArrayList<>();
		templateTypes.add(new TemplateType("T101", "ENG", "Description"));
		holidays = new ArrayList<>();
		holidays.add(new Holiday(new HolidayID(1, "LOC01", LocalDate.parse("2019-01-01"), "ENG"), "New Year",
				"description"));
		blackListedWords = new ArrayList<>();
		blackListedWords.add(new BlacklistedWords("ABC", "ENG", "description"));
		titles = new ArrayList<>();
		titles.add(new Title(new CodeAndLanguageCodeID("1011", "ENG"), "title", "titleDescription"));
		genders = new ArrayList<>();
		genders.add(new Gender("G1011", "MALE", "description"));
		languages = new ArrayList<>();
		languages.add(new Language("ENG", "english", "family", "native name"));
		idTypes = new ArrayList<>();
		idTypes.add(new IdType("ID101", "ENG", "ID", "descr"));
		validDocuments = new ArrayList<>();
		validDocuments.add(new ValidDocument("D101", "DC101", null, null, "ENG"));
		biometricAttributes = new ArrayList<>();
		biometricAttributes.add(new BiometricAttribute("B101", "101", "Fingerprint", "description", "BT101", null));
		biometricTypes = new ArrayList<>();
		biometricTypes.add(new BiometricType("BT101", "ENG", "name", "description"));
		documentCategories = new ArrayList<>();
		documentCategories.add(new DocumentCategory("DC101", "ENG", "DC name", "description"));
		documentTypes = new ArrayList<>();
		documentTypes.add(new DocumentType("DT101", "ENG", "DT Type", "description"));
		reasonCategories = new ArrayList<>();
		reasonCategories.add(new ReasonCategory("RC101", "101", "R-1", "description", null));
		reasonLists = new ArrayList<>();
		reasonLists.add(new ReasonList("RL101", "RL1", "ENG", "RL", "description", null));
		locations = new ArrayList<>();
		locations.add(new Location("LOC01", "1", 1, "COUNTRY", null, "ENG"));

	}

	private void mockSuccess() {
		when(applicationRepository.findAll()).thenReturn(applications);
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(applications);
		when(machineRepository.findMachineById(Mockito.anyString())).thenReturn(machines);
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any()))
				.thenReturn(machines);
		when(machineSpecificationRepository.findByMachineId(Mockito.anyString())).thenReturn(machineSpecification);
		when(machineSpecificationRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(machineSpecification);
		when(machineTypeRepository.findAllByMachineId(Mockito.anyString())).thenReturn(machineType);
		when(machineTypeRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any())).thenReturn(machineType);
		when(templateRepository.findAll()).thenReturn(templates);
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(templates);
		when(templateFileFormatRepository.findAllTemplateFormat()).thenReturn(templateFileFormats);
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(templateFileFormats);
		when(templateTypeRepository.findAll()).thenReturn(templateTypes);
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(templateTypes);
		when(holidayRepository.findAllByMachineId(Mockito.anyString())).thenReturn(holidays);
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(holidays);
		when(blacklistedWordsRepository.findAll()).thenReturn(blackListedWords);
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(blackListedWords);
		when(registrationCenterRepository.findRegistrationCenterByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(registrationCenters);
		when(registrationCenterTypeRepository.findRegistrationCenterTypeByMachineId(Mockito.anyString()))
				.thenReturn(registrationCenterType);
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),
				Mockito.any())).thenReturn(registrationCenterType);
		when(genderTypeRepository.findAll()).thenReturn(genders);
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(genders);
		when(idTypeRepository.findAll()).thenReturn(idTypes);
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(idTypes);
		when(deviceRepository.findDeviceByMachineId(Mockito.anyString())).thenReturn(devices);
		when(deviceRepository.findLatestDevicesByMachineId(Mockito.anyString(), Mockito.any())).thenReturn(devices);
		when(deviceSpecificationRepository.findDeviceTypeByMachineId(Mockito.anyString()))
				.thenReturn(deviceSpecification);
		when(deviceSpecificationRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(deviceSpecification);
		when(deviceTypeRepository.findDeviceTypeByMachineId(Mockito.anyString())).thenReturn(deviceType);
		when(deviceTypeRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenReturn(deviceType);
		when(languageRepository.findAll()).thenReturn(languages);
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(languages);
		when(reasonCategoryRepository.findAllReasons()).thenReturn(reasonCategories);
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(reasonCategories);
		when(reasonListRepository.findAll()).thenReturn(reasonLists);
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(reasonLists);
		when(documentCategoryRepository.findAll()).thenReturn(documentCategories);
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(documentCategories);
		when(documentTypeRepository.findAll()).thenReturn(documentTypes).thenReturn(documentTypes);
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(documentTypes);
		when(validDocumentRepository.findAll()).thenReturn(validDocuments);
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(validDocuments);
		when(biometricAttributeRepository.findAll()).thenReturn(biometricAttributes);
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenReturn(biometricAttributes);
		when(biometricTypeRepository.findAll()).thenReturn(biometricTypes);
		when(titleRepository.findAll()).thenReturn(titles);
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(titles);
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any())).thenReturn(locations);
		when(locationRepository.findAll()).thenReturn(locations);
	}

	@Test
	public void syncMasterDataSuccess() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/syncmasterdata/{machineId}", "1001")).andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataSuccessWithlastUpadtedTimestamp() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:01:01", "1001"))
				.andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataInvalidTimeStampException() throws Exception {
		mockSuccess();
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:101:01", "1001"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void syncMasterDataApplicationFetchException() throws Exception {
		mockSuccess();
		when(applicationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineFetchException() throws Exception {
		mockSuccess();
		when(machineRepository.findAllLatestCreatedUpdateDeleted(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineSpecFetchException() throws Exception {
		mockSuccess();
		when(machineSpecificationRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataMachineTypeFetchException() throws Exception {
		mockSuccess();
		when(machineTypeRepository.findLatestByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceFetchException() throws Exception {
		mockSuccess();
		when(deviceRepository.findLatestDevicesByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceSpecFetchException() throws Exception {
		mockSuccess();
		when(deviceSpecificationRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void syncMasterDataDeviceTypeFetchException() throws Exception {
		mockSuccess();
		when(deviceTypeRepository.findLatestDeviceTypeByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataTemplateFetchException() throws Exception {
		mockSuccess();
		when(templateRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataTemplateFileFormatFetchException() throws Exception {
		mockSuccess();
		when(templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataTemplateTypeFetchException() throws Exception {
		mockSuccess();
		when(templateTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	
	@Test
	public void syncMasterDataHolidayFetchException() throws Exception {
		mockSuccess();
		when(holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(Mockito.anyString(),Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataBiometricAttrFetchException() throws Exception {
		mockSuccess();
		when(biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataBiometricTypeFetchException() throws Exception {
		mockSuccess();
		when(biometricTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataDocCategoryFetchException() throws Exception {
		mockSuccess();
		when(documentCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataDocTypeFetchException() throws Exception {
		mockSuccess();
		when(documentTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataLanguageFetchException() throws Exception {
		mockSuccess();
		when(languageRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataGenderFetchException() throws Exception {
		mockSuccess();
		when(genderTypeRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataLocationFetchException() throws Exception {
		mockSuccess();
		when(locationRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataIdTypesFetchException() throws Exception {
		mockSuccess();
		when(idTypeRepository.findAllLatestCreatedUpdateDeleted(	Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataRegistrationCenterFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterRepository.findLatestRegistrationCenterByMachineId(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataRegistrationCenterTypeFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataValidFetchException() throws Exception {
		mockSuccess();
		when(registrationCenterTypeRepository.findLatestRegistrationCenterTypeByMachineId(Mockito.anyString(),Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataBlackListedWordFetchException() throws Exception {
		mockSuccess();
		when(blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	@Test
	public void syncMasterDataReasonCatFetchException() throws Exception {
		mockSuccess();
		when(reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataReasonListFetchException() throws Exception {
		mockSuccess();
		when(reasonListRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDataTitleFetchException() throws Exception {
		mockSuccess();
		when(titleRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void syncMasterDatavalidDocumentFetchException() throws Exception {
		mockSuccess();
		when(validDocumentRepository.findAllLatestCreatedUpdateDeleted(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-11-01T12:10:01", "1001"))
				.andExpect(status().isInternalServerError());
	}
}
