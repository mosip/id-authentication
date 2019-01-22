package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate5.HibernateObjectRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.getresponse.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResgistrationCenterStatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegCenterMachineDeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceHistoryID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.service.DeviceHistoryService;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.service.MachineHistoryService;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceHistoryService;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceHistoryService;
import io.mosip.kernel.masterdata.service.RegistrationCenterService;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * @author Bal Vikash Sharma
 * @author Neha Sinha
 * @author tapaswini
 * @author srinivasan
 * @since 1.0.0
 *
 * 
 * @since 1.0.0
 *
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterDataServiceTest {

	@MockBean
	private ApplicationRepository applicationRepository;

	@Autowired
	private ApplicationService applicationService;

	private Application application1;
	private Application application2;

	private List<Application> applicationList;
	private ApplicationDto applicationDto;

	private RequestDto<ApplicationDto> applicationRequestDto;
	private RequestDto<DocumentCategoryDto> documentCategoryRequestDto;

	private RegistrationCenterMachineDeviceHistoryDto registrationCenterMachimeDeviceHistoryDto;

	@MockBean
	BiometricAttributeRepository biometricAttributeRepository;

	@Autowired
	BiometricAttributeService biometricAttributeService;

	List<BiometricAttribute> biometricattributes = null;

	@MockBean
	private BiometricTypeRepository biometricTypeRepository;

	@Autowired
	private BiometricTypeService biometricTypeService;

	private BiometricType biometricType1 = new BiometricType();
	private BiometricType biometricType2 = new BiometricType();

	List<BiometricType> biometricTypeList = new ArrayList<>();

	@Autowired
	private BlacklistedWordsService blacklistedWordsService;

	@Autowired
	private RegistrationCenterService registrationCenterService;

	private RegistrationCenter registrationCenter;

	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;

	@MockBean
	private BlacklistedWordsRepository wordsRepository;

	List<BlacklistedWords> words;

	@MockBean
	DeviceSpecificationRepository deviceSpecificationRepository;

	@Autowired
	DeviceSpecificationService deviceSpecificationService;

	List<DeviceSpecification> deviceSpecifications = null;
	List<DeviceSpecification> deviceSpecificationListWithDeviceTypeCode = null;

	@MockBean
	DocumentCategoryRepository documentCategoryRepository;

	@Autowired
	DocumentCategoryService documentCategoryService;

	@MockBean
	RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepository;

	RegistrationCenterMachineDeviceHistory registrationCenterMachineDeviceHistory;

	@Autowired
	RegistrationCenterMachineDeviceHistoryService registrationCenterMachineDeviceHistoryService;

	private DocumentCategory documentCategory1;
	private DocumentCategory documentCategory2;

	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	@Autowired
	private LanguageService languageService;
	@Autowired
	private RegistrationCenterDeviceHistoryService registrationCenterDeviceHistoryService;

	private RegCenterMachineDeviceHistoryResponseDto regCenterMachineDeviceHistroyResponseDto;
	@MockBean
	private LanguageRepository languageRepository;

	private List<Language> languages;
	private LanguageResponseDto resp;
	private List<LanguageDto> languageDtos;
	private Language hin;
	private Language eng;
	private LanguageDto hinDto;
	private LanguageDto engDto;

	@MockBean
	LocationRepository locationHierarchyRepository;

	@Autowired
	LocationService locationHierarchyService;

	List<Location> locationHierarchies = null;
	List<Object[]> locObjList = null;
	LocationCodeResponseDto locationCodeResponseDto = null;
	Location locationHierarchy = null;
	Location locationHierarchy1 = null;
	LocationDto locationDtos = null;

	RequestDto<LocationDto> requestLocationDto = null;

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	private TemplateFileFormat templateFileFormat;

	private RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto;

	@Autowired
	private TemplateService templateService;

	private List<Template> templateList = new ArrayList<>();

	private TemplateResponseDto templateResponseDto;

	@MockBean
	DocumentTypeRepository documentTypeRepository;

	@Autowired
	DocumentTypeService documentTypeService;

	List<DocumentType> documents = null;

	// -----------------------------DeviceType-------------------------------------------------
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;

	@MockBean
	private MetaDataUtils metaUtils;

	// -----------------------------DeviceSpecification----------------------------------

	private List<DeviceSpecification> deviceSpecificationList;
	private DeviceSpecification deviceSpecification;

	@Autowired
	MachineHistoryService machineHistoryService;

	@Autowired
	DeviceHistoryService deviceHistoryService;

	private RequestDto<BiometricTypeDto> biometricTypeRequestDto;

	private BiometricTypeDto biometricTypeDto;

	@Before
	public void setUp() {

		applicationSetup();

		biometricAttrSetup();

		biometricTypeSetup();

		blackListedSetup();

		deviceSpecSetup();

		docCategorySetup();

		langServiceSetup();

		locationServiceSetup();

		templateServiceSetup();

		templateFileFormatSetup();

		documentTypeSetup();

		registrationCenterSetup();

		registrationCenterMachineDeviceHistorySetup();

	}

	private void documentTypeSetup() {
		documents = new ArrayList<DocumentType>();
		DocumentType documentType = new DocumentType();
		documentType.setCode("addhar");
		documentType.setName("addhar_card");
		documentType.setDescription("adhar_card_desc");
		documentType.setIsActive(true);
		documents.add(documentType);
		DocumentType documentType1 = new DocumentType();
		documentType1.setCode("residensial");
		documentType1.setName("residensial_proof");
		documentType1.setDescription("residensial_proof_desc");
		documentType1.setIsActive(true);
		documents.add(documentType1);
	}

	private void templateServiceSetup() {
		Template template = new Template();
		template.setId("3");
		template.setName("Email template");
		template.setFileFormatCode("xml");
		template.setTemplateTypeCode("EMAIL");
		template.setLangCode("HIN");
		template.setCreatedBy("Neha");
		template.setCreatedDateTime(LocalDateTime.of(2018, Month.NOVEMBER, 12, 0, 0, 0));
		template.setIsActive(true);
		template.setIsDeleted(false);

		templateList.add(template);
	}

	private void locationServiceSetup() {
		locationHierarchies = new ArrayList<>();
		locationHierarchy = new Location();
		locationHierarchy.setCode("IND");
		locationHierarchy.setName("INDIA");
		locationHierarchy.setHierarchyLevel(0);
		locationHierarchy.setHierarchyName("country");
		locationHierarchy.setParentLocCode(null);
		locationHierarchy.setLangCode("HIN");
		locationHierarchy.setCreatedBy("dfs");
		locationHierarchy.setUpdatedBy("sdfsd");
		locationHierarchy.setIsActive(true);
		locationHierarchies.add(locationHierarchy);
		locationHierarchy1 = new Location();
		locationHierarchy1.setCode("KAR");
		locationHierarchy1.setName("KARNATAKA");
		locationHierarchy1.setHierarchyLevel(1);
		locationHierarchy1.setHierarchyName(null);
		locationHierarchy1.setParentLocCode("TEST");
		locationHierarchy1.setLangCode("KAN");
		locationHierarchy1.setCreatedBy("dfs");
		locationHierarchy1.setUpdatedBy("sdfsd");
		locationHierarchy1.setIsActive(true);
		locationHierarchies.add(locationHierarchy1);
		Object[] objectArray = new Object[3];
		objectArray[0] = (short) 0;
		objectArray[1] = "COUNTRY";
		objectArray[2] = true;
		locObjList = new ArrayList<>();
		locObjList.add(objectArray);
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("KAR");
		locationDto.setName("KARNATAKA");
		locationDto.setHierarchyLevel(2);
		locationDto.setHierarchyName("STATE");
		locationDto.setLangCode("FRA");
		locationDto.setParentLocCode("IND");
		locationDto.setIsActive(true);
		requestLocationDto = new RequestDto<>();
		requestLocationDto.setRequest(locationDto);

	}

	private void langServiceSetup() {
		languages = new ArrayList<>();

		// creating language
		hin = new Language();
		hin.setCode("hin");
		hin.setName("hindi");
		hin.setFamily("hindi");
		hin.setNativeName("hindi");
		hin.setIsActive(Boolean.TRUE);

		eng = new Language();
		eng.setCode("en");
		eng.setName("english");
		eng.setFamily("english");
		eng.setNativeName("english");
		eng.setIsActive(Boolean.TRUE);

		// adding language to list
		languages.add(hin);
		languages.add(eng);

		languageDtos = new ArrayList<>();
		// creating language
		hinDto = new LanguageDto();
		hinDto.setCode("hin");
		hinDto.setName("hindi");
		hinDto.setFamily("hindi");
		hinDto.setNativeName("hindi");

		engDto = new LanguageDto();
		engDto.setCode("en");
		engDto.setName("english");
		engDto.setFamily("english");
		engDto.setNativeName("english");

		languageDtos.add(hinDto);
		languageDtos.add(engDto);

		resp = new LanguageResponseDto();
		resp.setLanguages(languageDtos);
	}

	private void docCategorySetup() {
		documentCategory1 = new DocumentCategory();
		documentCategory1.setCode("101");
		documentCategory1.setName("POI");
		documentCategory1.setLangCode("ENG");
		documentCategory1.setIsActive(true);
		documentCategory1.setIsDeleted(false);
		documentCategory1.setDescription(null);
		documentCategory1.setCreatedBy("Neha");
		documentCategory1.setUpdatedBy(null);

		documentCategory2 = new DocumentCategory();
		documentCategory2.setCode("102");
		documentCategory2.setName("POR");
		documentCategory2.setLangCode("ENG");
		documentCategory2.setIsActive(true);
		documentCategory2.setIsDeleted(false);
		documentCategory2.setDescription(null);
		documentCategory2.setCreatedBy("Neha");
		documentCategory2.setUpdatedBy(null);

		documentCategoryList.add(documentCategory1);
		documentCategoryList.add(documentCategory2);

		documentCategoryRequestDto = new RequestDto<DocumentCategoryDto>();
		DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
		documentCategoryDto.setCode("102");
		documentCategoryDto.setName("POR");
		documentCategoryDto.setDescription(null);
		documentCategoryDto.setLangCode("ENG");

		documentCategoryRequestDto.setRequest(documentCategoryDto);
	}

	private void deviceSpecSetup() {
		deviceSpecifications = new ArrayList<>();
		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("lp");
		deviceSpecification.setName("laptop");
		deviceSpecification.setBrand("hp");
		deviceSpecification.setModel("pavalian_dv6");
		deviceSpecification.setDeviceTypeCode("operating_sys");
		deviceSpecification.setMinDriverversion("window_10");
		deviceSpecification.setDescription("laptop discription");
		deviceSpecification.setLangCode("ENG");
		deviceSpecification.setIsActive(true);
		deviceSpecifications.add(deviceSpecification);
		DeviceSpecification deviceSpecification1 = new DeviceSpecification();
		deviceSpecification1.setId("printer");
		deviceSpecification1.setName("printer");
		deviceSpecification1.setBrand("hp");
		deviceSpecification1.setModel("marker_dv6");
		deviceSpecification1.setDeviceTypeCode("printer_id");
		deviceSpecification1.setMinDriverversion("ver_5.0");
		deviceSpecification1.setDescription("printer discription");
		deviceSpecification1.setLangCode("ENG");
		deviceSpecification1.setIsActive(true);
		deviceSpecifications.add(deviceSpecification1);
		deviceSpecificationListWithDeviceTypeCode = new ArrayList<DeviceSpecification>();
		deviceSpecificationListWithDeviceTypeCode.add(deviceSpecification);

		deviceSpecificationList = new ArrayList<>();
		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("100");
		deviceSpecification.setDeviceTypeCode("Laptop");
		deviceSpecification.setLangCode("ENG");
		deviceSpecificationList.add(deviceSpecification);

		IdResponseDto idResponseDto = new IdResponseDto();
		idResponseDto.setId("1111");
	}

	private void blackListedSetup() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("ENG");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
	}

	private void biometricTypeSetup() {
		biometricType1.setCode("1");
		biometricType1.setName("DNA MATCHING");
		biometricType1.setDescription(null);
		biometricType1.setLangCode("ENG");
		biometricType1.setIsActive(true);
		biometricType1.setCreatedBy("Neha");
		biometricType1.setUpdatedBy(null);
		biometricType1.setIsDeleted(false);

		biometricType2.setCode("3");
		biometricType2.setName("EYE SCAN");
		biometricType2.setDescription(null);
		biometricType2.setLangCode("ENG");
		biometricType2.setIsActive(true);
		biometricType2.setCreatedBy("Neha");
		biometricType2.setUpdatedBy(null);
		biometricType2.setIsDeleted(false);

		biometricTypeList.add(biometricType1);
		biometricTypeList.add(biometricType2);

		biometricTypeRequestDto = new RequestDto<BiometricTypeDto>();
		// BiometricTypeData request = new BiometricTypeData();
		biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setName("DNA MATCHING");
		biometricTypeDto.setDescription(null);
		biometricTypeDto.setLangCode("ENG");
		// request.setBiometricType(biometricTypeDto);
		biometricTypeRequestDto.setRequest(biometricTypeDto);
	}

	private void biometricAttrSetup() {
		biometricattributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("iric_black");
		biometricAttribute.setName("black");
		biometricAttribute.setIsActive(true);
		biometricattributes.add(biometricAttribute);
		BiometricAttribute biometricAttribute1 = new BiometricAttribute();
		biometricAttribute1.setCode("iric_brown");
		biometricAttribute1.setName("brown");
		biometricAttribute1.setIsActive(true);
		biometricattributes.add(biometricAttribute1);
	}

	private void applicationSetup() {
		application1 = new Application();
		application2 = new Application();

		applicationList = new ArrayList<>();
		application1.setCode("101");
		application1.setName("pre-registeration");
		application1.setDescription("Pre-registration Application Form");
		application1.setLangCode("ENG");
		application1.setIsActive(true);
		application1.setCreatedBy("Neha");
		application1.setUpdatedBy(null);
		application1.setIsDeleted(false);

		application2.setCode("102");
		application2.setName("registeration");
		application2.setDescription("Registeration Application Form");
		application2.setLangCode("ENG");
		application2.setIsActive(true);
		application2.setCreatedBy("Neha");
		application2.setUpdatedBy(null);
		application2.setIsDeleted(false);

		applicationList.add(application1);
		applicationList.add(application2);

		applicationRequestDto = new RequestDto<ApplicationDto>();
		// ApplicationData request = new ApplicationData();
		applicationDto = new ApplicationDto();
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLangCode("ENG");
		// request.setApplicationtype(applicationDto);
		applicationRequestDto.setRequest(applicationDto);
	}

	private void templateFileFormatSetup() {
		templateFileFormat = new TemplateFileFormat();
		templateFileFormat.setCode("xml");
		templateFileFormat.setLangCode("ENG");

		templateFileFormatRequestDto = new RequestDto<TemplateFileFormatDto>();
		TemplateFileFormatDto templateFileFormatDto = new TemplateFileFormatDto();
		templateFileFormatDto.setCode("xml");
		templateFileFormatDto.setLangCode("ENG");

		templateFileFormatRequestDto.setRequest(templateFileFormatDto);
	}

	private void registrationCenterSetup() {
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLanguageCode("ENG");
	}

	private void registrationCenterMachineDeviceHistorySetup() {
		registrationCenterMachimeDeviceHistoryDto = new RegistrationCenterMachineDeviceHistoryDto();
		registrationCenterMachimeDeviceHistoryDto.setDeviceId("1");
		registrationCenterMachimeDeviceHistoryDto.setMachineId("1000");
		registrationCenterMachimeDeviceHistoryDto.setRegCenterId("10");
		RegistrationCenterMachineDeviceHistoryID registrationCenterMachineDeviceHistoryPk = new RegistrationCenterMachineDeviceHistoryID();
		regCenterMachineDeviceHistroyResponseDto = new RegCenterMachineDeviceHistoryResponseDto();
		regCenterMachineDeviceHistroyResponseDto
				.setRegistrationCenterMachineDeviceHistoryDto(registrationCenterMachimeDeviceHistoryDto);

		registrationCenterMachineDeviceHistory = new RegistrationCenterMachineDeviceHistory();
		registrationCenterMachineDeviceHistory
				.setRegistrationCenterMachineDeviceHistoryPk(registrationCenterMachineDeviceHistoryPk);

	}

	// ----------------------- ApplicationServiceTest ----------------//
	@Test
	public void getAllApplicationSuccess() {
		Mockito.when(applicationRepository.findAllByIsDeletedFalseOrIsDeletedNull(Mockito.eq(Application.class)))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService.getAllApplication();
		List<ApplicationDto> applicationDtos = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtos.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtos.get(0).getName());
	}

	@Test
	public void getAllApplicationByLanguageCodeSuccess() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService
				.getAllApplicationByLanguageCode(Mockito.anyString());
		List<ApplicationDto> applicationDtoList = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtoList.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtoList.get(0).getName());
	}

	@Test
	public void getApplicationByCodeAndLangCodeSuccess() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(application1);
		ApplicationResponseDto applicationResponseDto = applicationService
				.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
		List<ApplicationDto> actual = applicationResponseDto.getApplicationtypes();
		assertEquals(application1.getCode(), actual.get(0).getCode());
		assertEquals(application1.getName(), actual.get(0).getName());
	}

	@Test
	public void addApplicationDataSuccess() {
		Mockito.when(applicationRepository.create(Mockito.any())).thenReturn(application1);

		CodeAndLanguageCodeID codeAndLanguageCodeId = applicationService.createApplication(applicationRequestDto);
		assertEquals(applicationRequestDto.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(applicationRequestDto.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addApplicationDataFetchException() {
		Mockito.when(applicationRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		applicationService.createApplication(applicationRequestDto);
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationFetchException() {
		Mockito.when(applicationRepository.findAllByIsDeletedFalseOrIsDeletedNull(Mockito.eq(Application.class)))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplication();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationNotFoundException() {
		applicationList = new ArrayList<>();
		Mockito.when(applicationRepository.findAllByIsDeletedFalseOrIsDeletedNull(Application.class))
				.thenReturn(applicationList);
		applicationService.getAllApplication();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationByLanguageCodeFetchException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationByLanguageCodeNotFoundException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<Application>());
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getApplicationByCodeAndLangCodeFetchException() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getApplicationByCodeAndLangCodeNotFoundException() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}

	// ------------------ BiometricAttributeServiceTest -----------------//
	@Test
	public void getBiometricAttributeTest() {
		String biometricTypeCode = "iric";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository
				.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(biometricTypeCode, langCode))
				.thenReturn(biometricattributes);

		List<BiometricAttributeDto> attributes = biometricAttributeService.getBiometricAttribute(biometricTypeCode,
				langCode);
		Assert.assertEquals(attributes.get(0).getCode(), biometricattributes.get(0).getCode());
		Assert.assertEquals(attributes.get(0).getName(), biometricattributes.get(0).getName());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionTest() {
		List<BiometricAttribute> empityList = new ArrayList<BiometricAttribute>();
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository
				.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(biometricTypeCode, langCode))
				.thenReturn(empityList);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionForNullTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository
				.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(biometricTypeCode, langCode))
				.thenReturn(null);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);
	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionInGetAllTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository
				.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(biometricTypeCode, langCode))
				.thenThrow(DataAccessResourceFailureException.class);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);
	}

	// ------------------ BiometricTypeServiceTest -----------------//

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesFetchException() {
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(BiometricType.class)))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesNotFoundException() {
		biometricTypeList = new ArrayList<>();
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(BiometricType.class))
				.thenReturn(biometricTypeList);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesByLanguageCodeFetchException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesByLanguageCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<BiometricType>());
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getBiometricTypeByCodeAndLangCodeFetchException() {
		Mockito.when(biometricTypeRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getBiometricTypeByCodeAndLangCodeNotFoundException() {
		Mockito.when(biometricTypeRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void addBiometricTypeDataSuccess() {
		Mockito.when(biometricTypeRepository.create(Mockito.any())).thenReturn(biometricType1);

		CodeAndLanguageCodeID codeAndLanguageCodeId = biometricTypeService.createBiometricType(biometricTypeRequestDto);
		assertEquals(biometricTypeRequestDto.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(biometricTypeRequestDto.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addBiometricTypeDataInsertException() {
		Mockito.when(biometricTypeRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		biometricTypeService.createBiometricType(biometricTypeRequestDto);
	}

	@Test
	public void getAllBioTypesSuccess() {
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(BiometricType.class)))
				.thenReturn(biometricTypeList);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService.getAllBiometricTypes();
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	@Test
	public void getAllBioTypesByLanguageCodeSuccess() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(biometricTypeList);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService
				.getAllBiometricTypesByLanguageCode(Mockito.anyString());
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	@Test
	public void getBioTypeByCodeAndLangCodeSuccess() {
		Mockito.when(biometricTypeRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(biometricType1);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService
				.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
		assertEquals(biometricType1.getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricType1.getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	// ------------------ BlacklistedServiceTest -----------------//

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlacklistedWordsNullvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode(null);
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlacklistedWordsEmptyvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("");
	}

	@Test
	public void testGetAllBlackListedWordsSuccess() {
		int expected = 1;
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(words);
		BlacklistedWordsResponseDto actual = blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
		assertEquals(actual.getBlacklistedwords().size(), expected);
	}

	@Test(expected = MasterDataServiceException.class)
	public void testGetAllBlackListedWordsFetchException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsNoDataFound() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(null);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsEmptyData() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(new ArrayList<>());
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsDataNotFoundException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(null);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsEmptyDataException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(new ArrayList<>());
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = MasterDataServiceException.class)
	public void testGetAllBlackListedWordsServiceException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	// ------------------ DeviceSpecificationServiceTest -----------------//

	@Test
	public void findDeviceSpecificationByLangugeCodeTest() {
		String languageCode = "ENG";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode))
				.thenReturn(deviceSpecifications);

		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCode(languageCode);
		Assert.assertEquals(deviceSpecificationDtos.get(0).getId(), deviceSpecifications.get(0).getId());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getName(), deviceSpecifications.get(0).getName());

	}

	@Test(expected = DataNotFoundException.class)
	public void noDeviceSpecRecordsFoudExceptionTest() {
		List<DeviceSpecification> empityList = new ArrayList<DeviceSpecification>();
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noDeviceSpecRecordsFoudExceptionForNullTest() {
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataDeviceSpecAccessExceptionInGetAllTest() {
		String languageCode = "eng";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test
	public void findDeviceSpecificationByLangugeCodeAndDeviceTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode, deviceTypeCode))
				.thenReturn(deviceSpecificationListWithDeviceTypeCode);

		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecByLangCodeAndDevTypeCode(languageCode, deviceTypeCode);
		Assert.assertEquals(deviceSpecificationDtos.get(0).getId(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getId());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getName(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getName());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getDeviceTypeCode(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getDeviceTypeCode());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionInDeviceSpecificationByDevicTypeCodeTest() {
		List<DeviceSpecification> empityList = new ArrayList<DeviceSpecification>();
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(deviceTypeCode, deviceTypeCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecByLangCodeAndDevTypeCode(languageCode, deviceTypeCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionnDeviceSpecificationByDevicTypeCodeForNullTest() {
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(deviceTypeCode, deviceTypeCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecByLangCodeAndDevTypeCode(languageCode, deviceTypeCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionnDeviceSpecificationByDevicTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode, deviceTypeCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecByLangCodeAndDevTypeCode(languageCode, deviceTypeCode);

	}

	// ------------------ DocumentCategoryServiceTest -----------------//

	@Test
	public void getAllDocumentCategorySuccessTest() {
		Mockito.when(
				documentCategoryRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(DocumentCategory.class)))
				.thenReturn(documentCategoryList);
		DocumentCategoryResponseDto documentCategoryResponseDto = documentCategoryService.getAllDocumentCategory();
		assertEquals(documentCategoryList.get(0).getName(),
				documentCategoryResponseDto.getDocumentcategories().get(0).getName());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategoryFetchException() {
		Mockito.when(
				documentCategoryRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(DocumentCategory.class)))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryNotFoundException() {
		Mockito.when(documentCategoryRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(DocumentCategory.class))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategory();
	}

	@Test
	public void getAllDocumentCategoryByLaguageCodeSuccessTest() {
		Mockito.when(
				documentCategoryRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(documentCategoryList);
		DocumentCategoryResponseDto documentCategoryResponseDto = documentCategoryService
				.getAllDocumentCategoryByLaguageCode("ENG");
		assertEquals(documentCategoryList.get(0).getName(),
				documentCategoryResponseDto.getDocumentcategories().get(0).getName());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategoryByLaguageCodeFetchException() {
		Mockito.when(
				documentCategoryRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryByLaguageCodeNotFound() {
		Mockito.when(
				documentCategoryRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test
	public void getDocumentCategoryByCodeAndLangCodeSuccessTest() {
		Mockito.when(documentCategoryRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(documentCategory1);
		DocumentCategoryResponseDto documentCategoryResponseDto = documentCategoryService
				.getDocumentCategoryByCodeAndLangCode("123", "ENG");
		assertEquals(documentCategory1.getName(), documentCategoryResponseDto.getDocumentcategories().get(0).getName());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getDocumentCategoryByCodeAndLangCodeFetchException() {
		Mockito.when(documentCategoryRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getDocumentCategoryByCodeAndLangCodeNotFoundException() {
		Mockito.when(documentCategoryRepository
				.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void addDocumentcategoryDataSuccess() {
		Mockito.when(documentCategoryRepository.create(Mockito.any())).thenReturn(documentCategory2);

		CodeAndLanguageCodeID codeAndLanguageCodeId = documentCategoryService
				.createDocumentCategory(documentCategoryRequestDto);
		assertEquals(documentCategoryRequestDto.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(documentCategoryRequestDto.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addDocumentcategoryDataFetchException() {
		Mockito.when(documentCategoryRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		documentCategoryService.createDocumentCategory(documentCategoryRequestDto);
	}

	// ------------------ LanguageServiceTest -----------------//

	@Test
	public void testSucessGetAllLaguages() {
		Mockito.when(languageRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(languages);
		LanguageResponseDto dto = languageService.getAllLaguages();
		assertNotNull(dto);
		assertEquals(2, dto.getLanguages().size());
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundException() {
		Mockito.when(languageRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(null);
		languageService.getAllLaguages();
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundExceptionWhenNoLanguagePresent() {
		Mockito.when(languageRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(new ArrayList<Language>());
		languageService.getAllLaguages();
	}

	@Test(expected = MasterDataServiceException.class)
	public void testLanguageFetchException() {
		Mockito.when(languageRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(HibernateObjectRetrievalFailureException.class);
		languageService.getAllLaguages();
	}

	// ------------------ LocationServiceTest -----------------//

	@Test()
	public void getLocationHierarchyTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString()))
				.thenReturn(locObjList);
		LocationHierarchyResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationDetails(Mockito.anyString());
		Assert.assertEquals("COUNTRY", locationHierarchyResponseDto.getLocations().get(0).getLocationHierarchyName());
	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyNoDataFoundExceptionTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<Object[]>());
		locationHierarchyService.getLocationDetails(Mockito.anyString());

	}

	@Test(expected = MasterDataServiceException.class)
	public void getLocationHierarchyFetchExceptionTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationDetails(Mockito.anyString());

	}

	@Test()
	public void getLocationHierachyBasedOnLangAndLoc() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(locationHierarchies);

		LocationResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationHierarchyByLangCode("IND", "HIN");
		Assert.assertEquals("IND", locationHierarchyResponseDto.getLocations().get(0).getCode());

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTest() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(null);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTestWithEmptyList() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(new ArrayList<Location>());
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchyDataAccessExceptionTest() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");
	}

	@Test
	public void locationHierarchySaveTest() {
		Mockito.when(locationHierarchyRepository.create(Mockito.any())).thenReturn(locationHierarchy);
		locationHierarchyService.createLocationHierarchy(requestLocationDto);
	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchySaveNegativeTest() {
		Mockito.when(locationHierarchyRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		locationHierarchyService.createLocationHierarchy(requestLocationDto);
	}

	@Test
	public void updateLocationDetailsTest() {

		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(locationHierarchy);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenReturn(locationHierarchy);

		locationHierarchyService.updateLocationDetails(requestLocationDto);
	}

	@Test(expected = MasterDataServiceException.class)
	public void updateLocationDetailsExceptionTest() {
		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(locationHierarchy);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenThrow(DataRetrievalFailureException.class);

		locationHierarchyService.updateLocationDetails(requestLocationDto);
	}

	@Test(expected = RequestException.class)
	public void updateLocationDetailsDataNotFoundTest() {
		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(null);
		locationHierarchyService.updateLocationDetails(requestLocationDto);
	}

	@Test
	public void deleteLocationDetailsTest() {

		Mockito.when(locationHierarchyRepository.findByCode(Mockito.anyString())).thenReturn(locationHierarchies);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenReturn(locationHierarchy);
		locationHierarchyService.deleteLocationDetials("KAR");

	}

	@Test(expected = MasterDataServiceException.class)
	public void deleteLocationDetailsServiceExceptionTest() {

		Mockito.when(locationHierarchyRepository.findByCode(Mockito.anyString())).thenReturn(locationHierarchies);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.deleteLocationDetials("KAR");

	}

	@Test(expected = RequestException.class)
	public void deleteLocationDetailDataNotFoundExceptionTest() {

		Mockito.when(locationHierarchyRepository.findByCode(Mockito.anyString())).thenReturn(new ArrayList<Location>());

		locationHierarchyService.deleteLocationDetials("KAR");

	}

	@Test()
	public void getLocationHierachyBasedOnHierarchyNameTest() {
		Mockito.when(locationHierarchyRepository.findAllByHierarchyNameIgnoreCase("country"))
				.thenReturn(locationHierarchies);

		LocationResponseDto locationResponseDto = locationHierarchyService.getLocationDataByHierarchyName("country");

		Assert.assertEquals("country", locationResponseDto.getLocations().get(0).getHierarchyName());

	}

	@Test(expected = DataNotFoundException.class)
	public void dataNotFoundExceptionTest() {

		Mockito.when(locationHierarchyRepository.findAllByHierarchyNameIgnoreCase("123")).thenReturn(null);

		locationHierarchyService.getLocationDataByHierarchyName("country");

	}

	@Test(expected = MasterDataServiceException.class)
	public void masterDataServiceExceptionTest() {
		Mockito.when(locationHierarchyRepository.findAllByHierarchyNameIgnoreCase("country"))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationDataByHierarchyName("country");

	}

	@Test
	public void getImmediateChildrenTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByParentLocCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(locationHierarchies);
		locationHierarchyService.getImmediateChildrenByLocCodeAndLangCode("KAR", "KAN");
	}

	@Test(expected = MasterDataServiceException.class)
	public void getImmediateChildrenServiceExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByParentLocCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getImmediateChildrenByLocCodeAndLangCode("KAR", "KAN");
	}

	@Test(expected = DataNotFoundException.class)
	public void getImmediateChildrenDataExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByParentLocCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ArrayList<Location>());
		locationHierarchyService.getImmediateChildrenByLocCodeAndLangCode("KAR", "KAN");
	}

	// ------------------ TemplateServiceTest -----------------//

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(Template.class)))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Mockito.eq(Template.class)))
				.thenReturn(templateList);
		templateService.getAllTemplate();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(templateList);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test
	public void getAllTemplateTest() {
		Mockito.when(templateRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Template.class))
				.thenReturn(templateList);
		templateResponseDto = templateService.getAllTemplate();

		assertEquals(templateList.get(0).getId(), templateResponseDto.getTemplates().get(0).getId());
		assertEquals(templateList.get(0).getName(), templateResponseDto.getTemplates().get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(templateList);
		templateResponseDto = templateService.getAllTemplateByLanguageCode(Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateResponseDto.getTemplates().get(0).getId());
		assertEquals(templateList.get(0).getName(), templateResponseDto.getTemplates().get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
				Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateResponseDto = templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(),
				Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateResponseDto.getTemplates().get(0).getId());
		assertEquals(templateList.get(0).getName(), templateResponseDto.getTemplates().get(0).getName());
	}

	// ------------------------------------TemplateFileFormatServiceTest---------------------------//
	@Test
	public void addTemplateFileFormatSuccess() {
		Mockito.when(templateFileFormatRepository.create(Mockito.any())).thenReturn(templateFileFormat);

		CodeAndLanguageCodeID codeAndLanguageCodeId = templateFileFormatService
				.createTemplateFileFormat(templateFileFormatRequestDto);
		assertEquals(templateFileFormat.getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(templateFileFormat.getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addTemplateFileFormatInsertExceptionTest() {
		Mockito.when(templateFileFormatRepository.create(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.createTemplateFileFormat(templateFileFormatRequestDto);
	}

	// ----------------------------------DocumentTypeServiceTest-------------------------//

	@Test
	public void getAllValidDocumentTypeTest() {
		String documentCategoryCode = "iric";
		String langCode = "eng";

		Mockito.when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(documentCategoryCode, langCode))
				.thenReturn(documents);

		List<DocumentTypeDto> documentTypes = documentTypeService.getAllValidDocumentType(documentCategoryCode,
				langCode);
		Assert.assertEquals(documentTypes.get(0).getCode(), documents.get(0).getCode());
		Assert.assertEquals(documentTypes.get(0).getName(), documents.get(0).getName());

	}

	@Test(expected = DataNotFoundException.class)
	public void documentTypeNoRecordsFoudExceptionTest() {
		String documentCategoryCode = "poc";
		String langCode = "eng";
		List<DocumentType> entitydocuments = new ArrayList<DocumentType>();
		Mockito.when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(documentCategoryCode, langCode))
				.thenReturn(entitydocuments);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

	@Test(expected = DataNotFoundException.class)
	public void documentTypeNoRecordsFoudExceptionForNullTest() {
		String documentCategoryCode = "poc";
		String langCode = "eng";
		Mockito.when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(documentCategoryCode, langCode))
				.thenReturn(null);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void documentTypeDataAccessExceptionInGetAllTest() {
		String documentCategoryCode = "poc";
		String langCode = "eng";
		Mockito.when(documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(documentCategoryCode, langCode))
				.thenThrow(DataAccessResourceFailureException.class);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

	/*---------------------- Blacklisted word validator----------------------*/

	@Test
	public void validateWordNegativeTest() {
		List<BlacklistedWords> badWords = new ArrayList<>();
		BlacklistedWords word = new BlacklistedWords();
		word.setWord("not-allowed");
		badWords.add(word);
		doReturn(badWords).when(wordsRepository).findAllByIsDeletedFalseOrIsDeletedNull();
		List<String> wordsList = new ArrayList<>();
		wordsList.add("not-allowed");
		boolean isValid = blacklistedWordsService.validateWord(wordsList);
		assertEquals("Invalid word", false, isValid);
	}

	@Test
	public void validateWordPositiveTest() {
		List<BlacklistedWords> badWords = new ArrayList<>();
		BlacklistedWords word = new BlacklistedWords();
		word.setWord("nun");
		badWords.add(word);
		doReturn(badWords).when(wordsRepository).findAllByIsDeletedFalseOrIsDeletedNull();
		List<String> wordsList = new ArrayList<>();
		wordsList.add("allowed");
		boolean isValid = blacklistedWordsService.validateWord(wordsList);
		assertEquals("Valid word", true, isValid);
	}

	@Test(expected = MasterDataServiceException.class)
	public void validateWordExceptionTest() {
		doThrow(DataRetrievalFailureException.class).when(wordsRepository).findAllByIsDeletedFalseOrIsDeletedNull();
		List<String> wordsList = new ArrayList<>();
		wordsList.add("allowed");
		blacklistedWordsService.validateWord(wordsList);
	}

	// -------------------------------------MachineHistroyTest----------------------------
	@Test(expected = RequestException.class)
	public void getMachineHistroyIdLangEffDTimeParseDateException() {
		machineHistoryService.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-12-11T11:18:261.033Z");
	}

	// ----------------------------------
	@Test(expected = RequestException.class)
	public void getRegCentDevHistByregCentIdDevIdEffTimeinvalidDateFormateTest() {
		registrationCenterDeviceHistoryService.getRegCenterDeviceHisByregCenterIdDevIdEffDTime("RCI100", "DI001",
				"2018-12-11T11:18:261.033Z");
	}

	// -------------------------------------DeviceHistroyTest------------------------------------------
	@Test(expected = RequestException.class)
	public void getDeviceHistroyIdLangEffDTimeParseDateException() {
		deviceHistoryService.getDeviceHistroyIdLangEffDTime("1000", "ENG", "2018-12-11T11:18:261.033Z");
	}

	// ---------------------RegistrationCenterIntegrationTest-validatetimestamp----------------//

	@Test
	public void getStatusOfWorkingHoursRejectedTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(true);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);
		LocalTime startTime = LocalTime.of(10, 00, 000);
		LocalTime endTime = LocalTime.of(18, 00, 000);
		registrationCenter.setCenterStartTime(startTime);
		registrationCenter.setCenterEndTime(endTime);
		/*
		 * mockMvc.perform(get(
		 * "/v1.0/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
		 * .andExpect(status().isOk());
		 */

		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = registrationCenterService
				.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

		Assert.assertEquals("Rejected", resgistrationCenterStatusResponseDto.getStatus());

	}

	@Test
	public void getStatusOfWorkingHoursTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);
		LocalTime startTime = LocalTime.of(10, 00, 000);
		LocalTime endTime = LocalTime.of(18, 00, 000);
		registrationCenter.setCenterStartTime(startTime);
		registrationCenter.setCenterEndTime(endTime);

		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = registrationCenterService
				.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

		/*
		 * mockMvc.perform(get(
		 * "/v1.0/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
		 * .andExpect(status().isOk());
		 */

		Assert.assertEquals("Accepted", resgistrationCenterStatusResponseDto.getStatus());

	}

	@Test(expected = MasterDataServiceException.class)
	public void getStatusOfWorkingHoursServiceExceptionTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

	}

	@Test(expected = DataNotFoundException.class)
	public void getStatusOfWorkingHoursDataNotFoundExceptionTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString())).thenReturn(null);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

	}

	@Test(expected = DataNotFoundException.class)
	public void getStatusOfWorkingHoursDataNotFoundTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

	}

	@Test
	public void getStatusOfWorkingHoursRejectedWorkingHourTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);
		LocalTime startTime = LocalTime.of(10, 00, 000);
		LocalTime endTime = LocalTime.of(15, 00, 000);
		registrationCenter.setCenterStartTime(startTime);
		registrationCenter.setCenterEndTime(endTime);

		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = registrationCenterService
				.validateTimeStampWithRegistrationCenter("1", "2017-12-12T17:59:59.999Z");

		Assert.assertEquals("Rejected", resgistrationCenterStatusResponseDto.getStatus());

	}
	
	@Test(expected =RequestException.class)
	public void invalidDateFormatTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "2017-12-1217:59:59.999Z");

	}

	

}