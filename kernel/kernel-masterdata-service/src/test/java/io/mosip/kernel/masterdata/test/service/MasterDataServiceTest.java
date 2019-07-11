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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterPutReqAdmDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterReqAdmDto;
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
import io.mosip.kernel.masterdata.test.TestBootApplication;
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

@SpringBootTest(classes = TestBootApplication.class)
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

	private RequestWrapper<ApplicationDto> applicationRequestWrapper;
	private RequestWrapper<DocumentCategoryDto> documentCategoryRequestDto;

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
	List<Location> locationHierarchyList = null;
	List<Object[]> locObjList = null;
	LocationCodeResponseDto locationCodeResponseDto = null;
	Location locationHierarchy = null;
	Location locationHierarchy1 = null;
	LocationDto locationDtos = null;
	Location locationHierarchy2 = null;
	Location locationHierarchy3 = null;
	

	RequestWrapper<LocationDto> requestLocationDto = null;
	RequestWrapper<LocationDto> requestLocationDto1 = null;

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	private TemplateFileFormat templateFileFormat;

	private RequestWrapper<TemplateFileFormatDto> templateFileFormatRequestDto;

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

	private RequestWrapper<BiometricTypeDto> biometricTypeRequestWrapper;

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
		updateRegistrationCenter();

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
		locationHierarchy.setHierarchyLevel((short) 0);
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
		locationHierarchy1.setHierarchyLevel((short) 1);
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
		requestLocationDto = new RequestWrapper<>();
		requestLocationDto.setRequest(locationDto);

		locationHierarchyList = new ArrayList<>();
		locationHierarchy3 = new Location();
		locationHierarchy3.setCode("KAR");
		locationHierarchy3.setName("KARNATAKA");
		locationHierarchy3.setHierarchyLevel((short) 1);
		locationHierarchy3.setHierarchyName(null);
		locationHierarchy3.setParentLocCode("IND");
		locationHierarchy3.setLangCode("KAN");
		locationHierarchy3.setCreatedBy("dfs");
		locationHierarchy3.setUpdatedBy("sdfsd");
		locationHierarchy3.setIsActive(true);
		locationHierarchyList.add(locationHierarchy3);
		
		LocationDto locationDto1 = new LocationDto();
		locationDto1.setCode("IND");
		locationDto1.setName("INDIA");
		locationDto1.setHierarchyLevel(1);
		locationDto1.setHierarchyName("CONTRY");
		locationDto1.setLangCode("HIN");
		locationDto1.setParentLocCode(null);
		locationDto1.setIsActive(false);
		requestLocationDto1 = new RequestWrapper<>();
		requestLocationDto1.setRequest(locationDto1);

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

		documentCategoryRequestDto = new RequestWrapper<DocumentCategoryDto>();
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

		biometricTypeRequestWrapper = new RequestWrapper<BiometricTypeDto>();
		// BiometricTypeData request = new BiometricTypeData();
		biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setName("DNA MATCHING");
		biometricTypeDto.setDescription(null);
		biometricTypeDto.setLangCode("ENG");
		// request.setBiometricType(biometricTypeDto);
		biometricTypeRequestWrapper.setRequest(biometricTypeDto);
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

		applicationRequestWrapper = new RequestWrapper<ApplicationDto>();
		// ApplicationData request = new ApplicationData();
		applicationDto = new ApplicationDto();
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLangCode("ENG");
		// request.setApplicationtype(applicationDto);
		applicationRequestWrapper.setRequest(applicationDto);
	}

	private void templateFileFormatSetup() {
		templateFileFormat = new TemplateFileFormat();
		templateFileFormat.setCode("xml");
		templateFileFormat.setLangCode("ENG");

		templateFileFormatRequestDto = new RequestWrapper<TemplateFileFormatDto>();
		TemplateFileFormatDto templateFileFormatDto = new TemplateFileFormatDto();
		templateFileFormatDto.setCode("xml");
		templateFileFormatDto.setLangCode("ENG");

		templateFileFormatRequestDto.setRequest(templateFileFormatDto);
	}

	List<RegistrationCenterReqAdmDto> requestNotAllLang = null;
	List<RegistrationCenterReqAdmDto> requestDuplicateLang = null;
	List<RegistrationCenterReqAdmDto> requestSetLongitudeInvalide = null;
	List<RegistrationCenterReqAdmDto> requestCenterTime = null;
	List<RegistrationCenterReqAdmDto> requestLunchTime = null;
	RegistrationCenterReqAdmDto registrationCenterDto1  = null; 
	RegistrationCenterReqAdmDto registrationCenterDto2  = null; 
	RegistrationCenterReqAdmDto registrationCenterDto3  = null; 
	RegistrationCenterReqAdmDto registrationCenterDto4  = null; 
	RegistrationCenterReqAdmDto registrationCenterDto5 = null;
	RegistrationCenterReqAdmDto registrationCenterDto6 = null;
	RegistrationCenterReqAdmDto registrationCenterDto7 = null;
	
	
	private void registrationCenterSetup() {
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLangCode("ENG");
		
		//----
		LocalTime centerStartTime = LocalTime.of(1, 10, 10, 30);
		LocalTime centerEndTime = LocalTime.of(1, 10, 10, 30);
		LocalTime lunchStartTime = LocalTime.of(1, 10, 10, 30);
		LocalTime lunchEndTime = LocalTime.of(1, 10, 10, 30);
		LocalTime perKioskProcessTime = LocalTime.of(1, 10, 10, 30);
		
		LocalTime centerStartTimeGrt = LocalTime.parse("18:00:00");
		LocalTime centerEndTimeSm = LocalTime.parse("17:00:00");
		LocalTime lunchStartTimeGrt = LocalTime.parse("18:00:00");
		LocalTime lunchEndTimeSm = LocalTime.parse("17:00:00");
		
		requestNotAllLang = new ArrayList<>();
		requestDuplicateLang = new ArrayList<>();
		requestSetLongitudeInvalide = new ArrayList<>();
		requestCenterTime = new ArrayList<>();
		requestLunchTime = new ArrayList<>();
		
		// 1st obj
	    registrationCenterDto1 = new RegistrationCenterReqAdmDto();
		registrationCenterDto1.setName("TEST CENTER");
		registrationCenterDto1.setAddressLine1("Address Line 1");
		registrationCenterDto1.setAddressLine2("Address Line 2");
		registrationCenterDto1.setAddressLine3("Address Line 3");
		registrationCenterDto1.setCenterTypeCode("REG");
		registrationCenterDto1.setContactPerson("Test");
		registrationCenterDto1.setContactPhone("9999999999");
		registrationCenterDto1.setHolidayLocationCode("HLC01");
		registrationCenterDto1.setLangCode("eng");
		registrationCenterDto1.setLatitude("12.9646818");
		registrationCenterDto1.setLocationCode("10190");
		registrationCenterDto1.setLongitude("77.70168");
		registrationCenterDto1.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto1.setCenterStartTime(centerStartTime);
		registrationCenterDto1.setCenterEndTime(centerEndTime);
		registrationCenterDto1.setLunchStartTime(lunchStartTime);
		registrationCenterDto1.setLunchEndTime(lunchEndTime);
		registrationCenterDto1.setTimeZone("UTC");
		registrationCenterDto1.setWorkingHours("9");
		requestNotAllLang.add(registrationCenterDto1);
		requestDuplicateLang.add(registrationCenterDto1);
		requestCenterTime.add(registrationCenterDto1);
		requestLunchTime.add(registrationCenterDto1);
		
		// 2nd obj
		registrationCenterDto2 = new RegistrationCenterReqAdmDto();
		registrationCenterDto2.setName("TEST CENTER");
		registrationCenterDto2.setAddressLine1("Address Line 1");
		registrationCenterDto2.setAddressLine2("Address Line 2");
		registrationCenterDto2.setAddressLine3("Address Line 3");
		registrationCenterDto2.setCenterTypeCode("REG");
		registrationCenterDto2.setContactPerson("Test");
		registrationCenterDto2.setContactPhone("9999999999");
		registrationCenterDto2.setHolidayLocationCode("HLC01");
		registrationCenterDto2.setLangCode("ara");
		registrationCenterDto2.setLatitude("12.9646818");
		registrationCenterDto2.setLocationCode("10190");
		registrationCenterDto2.setLongitude("77.70168");
		registrationCenterDto2.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto2.setCenterStartTime(centerStartTime);
		registrationCenterDto2.setCenterEndTime(centerEndTime);
		registrationCenterDto2.setLunchStartTime(lunchStartTime);
		registrationCenterDto2.setLunchEndTime(lunchEndTime);
		registrationCenterDto2.setTimeZone("UTC");
		registrationCenterDto2.setWorkingHours("9");
		requestNotAllLang.add(registrationCenterDto2);
		requestDuplicateLang.add(registrationCenterDto2);
		requestCenterTime.add(registrationCenterDto2);
		requestLunchTime.add(registrationCenterDto2);

		// 3rd obj
		registrationCenterDto3 = new RegistrationCenterReqAdmDto();
		registrationCenterDto3.setName("TEST CENTER");
		registrationCenterDto3.setAddressLine1("Address Line 1");
		registrationCenterDto3.setAddressLine2("Address Line 2");
		registrationCenterDto3.setAddressLine3("Address Line 3");
		registrationCenterDto3.setCenterTypeCode("REG");
		registrationCenterDto3.setContactPerson("Test");
		registrationCenterDto3.setContactPhone("9999999999");
		registrationCenterDto3.setHolidayLocationCode("HLC01");
		registrationCenterDto3.setLangCode("fra");
		registrationCenterDto3.setLatitude("12.9646818");
		registrationCenterDto3.setLocationCode("10190");
		registrationCenterDto3.setLongitude("77.70168");
		registrationCenterDto3.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto3.setCenterStartTime(centerStartTime);
		registrationCenterDto3.setCenterEndTime(centerEndTime);
		registrationCenterDto3.setLunchStartTime(lunchStartTime);
		registrationCenterDto3.setLunchEndTime(lunchEndTime);
		registrationCenterDto3.setTimeZone("UTC");
		registrationCenterDto3.setWorkingHours("9");
		
		registrationCenterDto4 = new RegistrationCenterReqAdmDto();
		registrationCenterDto4.setName("TEST CENTER");
		registrationCenterDto4.setAddressLine1("Address Line 1");
		registrationCenterDto4.setAddressLine2("Address Line 2");
		registrationCenterDto4.setAddressLine3("Address Line 3");
		registrationCenterDto4.setCenterTypeCode("REG");
		registrationCenterDto4.setContactPerson("Test");
		registrationCenterDto4.setContactPhone("9999999999");
		registrationCenterDto4.setHolidayLocationCode("HLC01");
		registrationCenterDto4.setLangCode("eng");
		registrationCenterDto4.setLatitude("-7.333");
		registrationCenterDto4.setLocationCode("10190");
		registrationCenterDto4.setLongitude("77.70168");
		registrationCenterDto4.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto4.setCenterStartTime(centerStartTime);
		registrationCenterDto4.setCenterEndTime(centerEndTime);
		registrationCenterDto4.setLunchStartTime(lunchStartTime);
		registrationCenterDto4.setLunchEndTime(lunchEndTime);
		registrationCenterDto4.setTimeZone("UTC");
		registrationCenterDto4.setWorkingHours("9");
		requestSetLongitudeInvalide.add(registrationCenterDto4);
		
		registrationCenterDto5 = new RegistrationCenterReqAdmDto();
		registrationCenterDto5.setName("TEST CENTER");
		registrationCenterDto5.setAddressLine1("Address Line 1");
		registrationCenterDto5.setAddressLine2("Address Line 2");
		registrationCenterDto5.setAddressLine3("Address Line 3");
		registrationCenterDto5.setCenterTypeCode("REG");
		registrationCenterDto5.setContactPerson("Test");
		registrationCenterDto5.setContactPhone("9999999999");
		registrationCenterDto5.setHolidayLocationCode("HLC01");
		registrationCenterDto5.setLangCode("fra");
		registrationCenterDto5.setLatitude("12.9646818");
		registrationCenterDto5.setLocationCode("10190");
		registrationCenterDto5.setLongitude("77.70168");
		registrationCenterDto5.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto5.setCenterStartTime(centerStartTime);
		registrationCenterDto5.setCenterEndTime(centerEndTime);
		registrationCenterDto5.setLunchStartTime(lunchStartTime);
		registrationCenterDto5.setLunchEndTime(lunchEndTime);
		registrationCenterDto5.setTimeZone("UTC");
		registrationCenterDto5.setWorkingHours("9");
		requestDuplicateLang.add(registrationCenterDto5);
		requestDuplicateLang.add(registrationCenterDto5);
		
		registrationCenterDto6 = new RegistrationCenterReqAdmDto();
		registrationCenterDto6.setName("TEST CENTER");
		registrationCenterDto6.setAddressLine1("Address Line 1");
		registrationCenterDto6.setAddressLine2("Address Line 2");
		registrationCenterDto6.setAddressLine3("Address Line 3");
		registrationCenterDto6.setCenterTypeCode("REG");
		registrationCenterDto6.setContactPerson("Test");
		registrationCenterDto6.setContactPhone("9999999999");
		registrationCenterDto6.setHolidayLocationCode("HLC01");
		registrationCenterDto6.setLangCode("eng");
		registrationCenterDto6.setLatitude("12.9646818");
		registrationCenterDto6.setLocationCode("10190");
		registrationCenterDto6.setLongitude("77.70168");
		registrationCenterDto6.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto6.setCenterStartTime(centerStartTimeGrt);
		registrationCenterDto6.setCenterEndTime(centerEndTimeSm);
		registrationCenterDto6.setLunchStartTime(lunchStartTime);
		registrationCenterDto6.setLunchEndTime(lunchEndTime);
		registrationCenterDto6.setTimeZone("UTC");
		registrationCenterDto6.setWorkingHours("9");
		requestCenterTime.add(registrationCenterDto6);
		
		registrationCenterDto7 = new RegistrationCenterReqAdmDto();
		registrationCenterDto7.setName("TEST CENTER");
		registrationCenterDto7.setAddressLine1("Address Line 1");
		registrationCenterDto7.setAddressLine2("Address Line 2");
		registrationCenterDto7.setAddressLine3("Address Line 3");
		registrationCenterDto7.setCenterTypeCode("REG");
		registrationCenterDto7.setContactPerson("Test");
		registrationCenterDto7.setContactPhone("9999999999");
		registrationCenterDto7.setHolidayLocationCode("HLC01");
		registrationCenterDto7.setLangCode("eng");
		registrationCenterDto7.setLatitude("12.9646818");
		registrationCenterDto7.setLocationCode("10190");
		registrationCenterDto7.setLongitude("77.70168");
		registrationCenterDto7.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterDto7.setCenterStartTime(centerStartTime);
		registrationCenterDto7.setCenterEndTime(centerEndTime);
		registrationCenterDto7.setLunchStartTime(lunchStartTimeGrt);
		registrationCenterDto7.setLunchEndTime(lunchEndTimeSm);
		registrationCenterDto7.setTimeZone("UTC");
		registrationCenterDto7.setWorkingHours("9");
		requestLunchTime.add(registrationCenterDto7);
		
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
	
	
	List<RegistrationCenterPutReqAdmDto> updRequestNotAllLang = null;
	List<RegistrationCenterPutReqAdmDto> updRequestInvalideID = null;
	List<RegistrationCenterPutReqAdmDto> updRequestDuplicateIDLang = null;
	List<RegistrationCenterPutReqAdmDto> updRequestSetLongitudeInvalide = null;
	List<RegistrationCenterPutReqAdmDto> updRequestCenterTime = null;
	List<RegistrationCenterPutReqAdmDto> updRequestLunchTime = null;
	
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto1 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto2 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto3 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto4 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto5 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto6 = null;
	RegistrationCenterPutReqAdmDto registrationCenterPutReqAdmDto7 = null;
	
	private void updateRegistrationCenter() {
		
		
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLangCode("ENG");
		
		//----
		LocalTime centerStartTime = LocalTime.of(1, 10, 10, 30);
		LocalTime centerEndTime = LocalTime.of(1, 10, 10, 30);
		LocalTime lunchStartTime = LocalTime.of(1, 10, 10, 30);
		LocalTime lunchEndTime = LocalTime.of(1, 10, 10, 30);
		LocalTime perKioskProcessTime = LocalTime.of(1, 10, 10, 30);
		
		LocalTime centerStartTimeGrt = LocalTime.parse("18:00:00");
		LocalTime centerEndTimeSm = LocalTime.parse("17:00:00");
		LocalTime lunchStartTimeGrt = LocalTime.parse("18:00:00");
		LocalTime lunchEndTimeSm = LocalTime.parse("17:00:00");
		
		
		updRequestNotAllLang = new ArrayList<>();
		updRequestInvalideID = new ArrayList<>();
		updRequestDuplicateIDLang = new ArrayList<>();
		updRequestSetLongitudeInvalide = new ArrayList<>();
		updRequestCenterTime = new ArrayList<>();
		updRequestLunchTime = new ArrayList<>();
		
		// 1st obj
		registrationCenterPutReqAdmDto1 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto1.setName("TEST CENTER");
		registrationCenterPutReqAdmDto1.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto1.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto1.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto1.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto1.setContactPerson("Test");
		registrationCenterPutReqAdmDto1.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto1.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto1.setId("676");
		registrationCenterPutReqAdmDto1.setLangCode("eng");
		registrationCenterPutReqAdmDto1.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto1.setLocationCode("10190");
		registrationCenterPutReqAdmDto1.setLongitude("77.70168");
		registrationCenterPutReqAdmDto1.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto1.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto1.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto1.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto1.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto1.setTimeZone("UTC");
		registrationCenterPutReqAdmDto1.setWorkingHours("9");
		registrationCenterPutReqAdmDto1.setIsActive(false);
		updRequestNotAllLang.add(registrationCenterPutReqAdmDto1);
		updRequestInvalideID.add(registrationCenterPutReqAdmDto1);
		updRequestDuplicateIDLang.add(registrationCenterPutReqAdmDto1);
		updRequestCenterTime.add(registrationCenterPutReqAdmDto1);
		updRequestLunchTime.add(registrationCenterPutReqAdmDto1);
		
		// 2nd obj
		registrationCenterPutReqAdmDto2 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto2.setName("TEST CENTER");
		registrationCenterPutReqAdmDto2.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto2.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto2.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto2.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto2.setContactPerson("Test");
		registrationCenterPutReqAdmDto2.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto2.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto2.setId("676");
		registrationCenterPutReqAdmDto2.setLangCode("ara");
		registrationCenterPutReqAdmDto2.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto2.setLocationCode("10190");
		registrationCenterPutReqAdmDto2.setLongitude("77.70168");
		registrationCenterPutReqAdmDto2.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto2.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto2.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto2.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto2.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto2.setTimeZone("UTC");
		registrationCenterPutReqAdmDto2.setWorkingHours("9");
		registrationCenterPutReqAdmDto2.setIsActive(false);
		updRequestNotAllLang.add(registrationCenterPutReqAdmDto2);
		updRequestInvalideID.add(registrationCenterPutReqAdmDto2);
		updRequestDuplicateIDLang.add(registrationCenterPutReqAdmDto2);
		updRequestCenterTime.add(registrationCenterPutReqAdmDto2);
		updRequestLunchTime.add(registrationCenterPutReqAdmDto2);

		// 3rd obj
		registrationCenterPutReqAdmDto3 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto3.setName("TEST CENTER");
		registrationCenterPutReqAdmDto3.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto3.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto3.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto3.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto3.setContactPerson("Test");
		registrationCenterPutReqAdmDto3.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto3.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto3.setId("6767");
		registrationCenterPutReqAdmDto3.setLangCode("fra");
		registrationCenterPutReqAdmDto3.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto3.setLocationCode("10190");
		registrationCenterPutReqAdmDto3.setLongitude("77.70168");
		registrationCenterPutReqAdmDto3.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto3.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto3.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto3.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto3.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto3.setTimeZone("UTC");
		registrationCenterPutReqAdmDto3.setWorkingHours("9");
		registrationCenterPutReqAdmDto3.setIsActive(false);
		updRequestInvalideID.add(registrationCenterPutReqAdmDto3);
		
		
		registrationCenterPutReqAdmDto4 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto4.setName("TEST CENTER");
		registrationCenterPutReqAdmDto4.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto4.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto4.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto4.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto4.setContactPerson("Test");
		registrationCenterPutReqAdmDto4.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto4.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto4.setId("676");
		registrationCenterPutReqAdmDto4.setLangCode("eng");
		registrationCenterPutReqAdmDto4.setLatitude("-7.333");
		registrationCenterPutReqAdmDto4.setLocationCode("10190");
		registrationCenterPutReqAdmDto4.setLongitude("77.70168");
		registrationCenterPutReqAdmDto4.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto4.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto4.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto4.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto4.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto4.setTimeZone("UTC");
		registrationCenterPutReqAdmDto4.setWorkingHours("9");
		registrationCenterPutReqAdmDto4.setIsActive(false);
		updRequestSetLongitudeInvalide.add(registrationCenterPutReqAdmDto4);
		
		registrationCenterPutReqAdmDto5 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto5.setName("TEST CENTER");
		registrationCenterPutReqAdmDto5.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto5.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto5.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto5.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto5.setContactPerson("Test");
		registrationCenterPutReqAdmDto5.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto5.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto5.setId("676");
		registrationCenterPutReqAdmDto5.setLangCode("fra");
		registrationCenterPutReqAdmDto5.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto5.setLocationCode("10190");
		registrationCenterPutReqAdmDto5.setLongitude("77.70168");
		registrationCenterPutReqAdmDto5.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto5.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto5.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto5.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto5.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto5.setTimeZone("UTC");
		registrationCenterPutReqAdmDto5.setWorkingHours("9");
		registrationCenterPutReqAdmDto5.setIsActive(false);
		updRequestDuplicateIDLang.add(registrationCenterPutReqAdmDto5);
		updRequestDuplicateIDLang.add(registrationCenterPutReqAdmDto5);
		
		
		registrationCenterPutReqAdmDto6 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto6.setName("TEST CENTER");
		registrationCenterPutReqAdmDto6.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto6.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto6.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto6.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto6.setContactPerson("Test");
		registrationCenterPutReqAdmDto6.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto6.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto6.setId("676");
		registrationCenterPutReqAdmDto6.setLangCode("eng");
		registrationCenterPutReqAdmDto6.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto6.setLocationCode("10190");
		registrationCenterPutReqAdmDto6.setLongitude("77.70168");
		registrationCenterPutReqAdmDto6.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto6.setCenterStartTime(centerStartTimeGrt);
		registrationCenterPutReqAdmDto6.setCenterEndTime(centerEndTimeSm);
		registrationCenterPutReqAdmDto6.setLunchStartTime(lunchStartTime);
		registrationCenterPutReqAdmDto6.setLunchEndTime(lunchEndTime);
		registrationCenterPutReqAdmDto6.setTimeZone("UTC");
		registrationCenterPutReqAdmDto6.setWorkingHours("9");
		registrationCenterPutReqAdmDto6.setIsActive(false);
		updRequestCenterTime.add(registrationCenterPutReqAdmDto6);
		
		registrationCenterPutReqAdmDto7 = new RegistrationCenterPutReqAdmDto();
		registrationCenterPutReqAdmDto7.setName("TEST CENTER");
		registrationCenterPutReqAdmDto7.setAddressLine1("Address Line 1");
		registrationCenterPutReqAdmDto7.setAddressLine2("Address Line 2");
		registrationCenterPutReqAdmDto7.setAddressLine3("Address Line 3");
		registrationCenterPutReqAdmDto7.setCenterTypeCode("REG");
		registrationCenterPutReqAdmDto7.setContactPerson("Test");
		registrationCenterPutReqAdmDto7.setContactPhone("9999999999");
		registrationCenterPutReqAdmDto7.setHolidayLocationCode("HLC01");
		registrationCenterPutReqAdmDto7.setId("676");
		registrationCenterPutReqAdmDto7.setLangCode("eng");
		registrationCenterPutReqAdmDto7.setLatitude("12.9646818");
		registrationCenterPutReqAdmDto7.setLocationCode("10190");
		registrationCenterPutReqAdmDto7.setLongitude("77.70168");
		registrationCenterPutReqAdmDto7.setPerKioskProcessTime(perKioskProcessTime);
		registrationCenterPutReqAdmDto7.setCenterStartTime(centerStartTime);
		registrationCenterPutReqAdmDto7.setCenterEndTime(centerEndTime);
		registrationCenterPutReqAdmDto7.setLunchStartTime(lunchStartTimeGrt);
		registrationCenterPutReqAdmDto7.setLunchEndTime(lunchEndTimeSm);
		registrationCenterPutReqAdmDto7.setTimeZone("UTC");
		registrationCenterPutReqAdmDto7.setWorkingHours("9");
		registrationCenterPutReqAdmDto7.setIsActive(false);
		updRequestLunchTime.add(registrationCenterPutReqAdmDto7);
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

		CodeAndLanguageCodeID codeAndLanguageCodeId = applicationService
				.createApplication(applicationRequestWrapper.getRequest());
		assertEquals(applicationRequestWrapper.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(applicationRequestWrapper.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addApplicationDataFetchException() {
		Mockito.when(applicationRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		applicationService.createApplication(applicationRequestWrapper.getRequest());
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

		CodeAndLanguageCodeID codeAndLanguageCodeId = biometricTypeService
				.createBiometricType(biometricTypeRequestWrapper.getRequest());
		assertEquals(biometricTypeRequestWrapper.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(biometricTypeRequestWrapper.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addBiometricTypeDataInsertException() {
		Mockito.when(biometricTypeRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		biometricTypeService.createBiometricType(biometricTypeRequestWrapper.getRequest());
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
				.createDocumentCategory(documentCategoryRequestDto.getRequest());
		assertEquals(documentCategoryRequestDto.getRequest().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(documentCategoryRequestDto.getRequest().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addDocumentcategoryDataFetchException() {
		Mockito.when(documentCategoryRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		documentCategoryService.createDocumentCategory(documentCategoryRequestDto.getRequest());
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
		locationHierarchyService.createLocationHierarchy(requestLocationDto.getRequest());
	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchySaveNegativeTest() {
		Mockito.when(locationHierarchyRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		locationHierarchyService.createLocationHierarchy(requestLocationDto.getRequest());
	}

	@Test(expected = RequestException.class)
	public void updateLocationDetailsIsActiveTest() {

		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(locationHierarchy2);
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByParentLocCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(locationHierarchyList);

		locationHierarchyService.updateLocationDetails(requestLocationDto1.getRequest());
	}

	@Test
	public void updateLocationDetailsTest() {

		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(locationHierarchy);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenReturn(locationHierarchy);

		locationHierarchyService.updateLocationDetails(requestLocationDto.getRequest());
	}

	@Test(expected = MasterDataServiceException.class)
	public void updateLocationDetailsExceptionTest() {
		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(locationHierarchy);
		Mockito.when(locationHierarchyRepository.update(Mockito.any())).thenThrow(DataRetrievalFailureException.class);

		locationHierarchyService.updateLocationDetails(requestLocationDto.getRequest());
	}

	@Test(expected = RequestException.class)
	public void updateLocationDetailsDataNotFoundTest() {
		Mockito.when(locationHierarchyRepository.findById(Mockito.any(), Mockito.any())).thenReturn(null);
		locationHierarchyService.updateLocationDetails(requestLocationDto.getRequest());
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
				.createTemplateFileFormat(templateFileFormatRequestDto.getRequest());
		assertEquals(templateFileFormat.getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(templateFileFormat.getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addTemplateFileFormatInsertExceptionTest() {
		Mockito.when(templateFileFormatRepository.create(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.createTemplateFileFormat(templateFileFormatRequestDto.getRequest());
	}

	@Test(expected = MasterDataServiceException.class)
	public void updateTemplateFileFormatDataAccessExceptionTest() {
		Mockito.when(templateFileFormatRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any(),
				Mockito.any())).thenReturn(templateFileFormat);
		Mockito.when(templateFileFormatRepository.update(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.updateTemplateFileFormat(templateFileFormatRequestDto.getRequest());
	}

	@Test(expected = MasterDataServiceException.class)
	public void deleteTemplateFileFormatDataAccessExceptionTest() {
		Mockito.when(templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(templateList);
		Mockito.when(templateFileFormatRepository.deleteTemplateFileFormat(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.deleteTemplateFileFormat(templateFileFormatRequestDto.getRequest().getCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void deleteTemplateFileFormatDataAccessExceptionTest2() {
		Mockito.when(templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.deleteTemplateFileFormat(templateFileFormatRequestDto.getRequest().getCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void deleteTemplateFileFormatDataAccessExceptionTest3() {
		Mockito.when(templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.any()))
				.thenReturn(templateList);
		Mockito.when(templateFileFormatRepository.deleteTemplateFileFormat(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(0);
		templateFileFormatService.deleteTemplateFileFormat(templateFileFormatRequestDto.getRequest().getCode());
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

	/*
	 * @Test public void getStatusOfWorkingHoursRejectedTest() throws Exception {
	 * Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any
	 * (), Mockito.any())) .thenReturn(true);
	 * Mockito.when(registrationCenterRepository.findById(Mockito.any(),
	 * Mockito.anyString())) .thenReturn(registrationCenter); LocalTime startTime =
	 * LocalTime.of(10, 00, 000); LocalTime endTime = LocalTime.of(18, 00, 000);
	 * registrationCenter.setCenterStartTime(startTime);
	 * registrationCenter.setCenterEndTime(endTime);
	 * 
	 * mockMvc.perform(get(
	 * "/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
	 * .andExpect(status().isOk());
	 * 
	 * 
	 * ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto =
	 * registrationCenterService .validateTimeStampWithRegistrationCenter("1",
	 * "eng", "2017-12-12T17:59:59.999Z");
	 * 
	 * Assert.assertEquals(MasterDataConstant.INVALID,
	 * resgistrationCenterStatusResponseDto.getStatus());
	 * 
	 * }
	 */

	@Test
	public void getStatusOfWorkingHoursTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findByIdAndLangCode(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);
		LocalTime startTime = LocalTime.of(10, 00, 000);
		LocalTime endTime = LocalTime.of(18, 00, 000);
		registrationCenter.setCenterStartTime(startTime);
		registrationCenter.setCenterEndTime(endTime);

		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = registrationCenterService
				.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-12T17:59:59.999Z");

		/*
		 * mockMvc.perform(get(
		 * "/registrationcenters/validate/1/2017-12-12T17:59:59.999Z"))
		 * .andExpect(status().isOk());
		 */

		Assert.assertEquals(MasterDataConstant.VALID, resgistrationCenterStatusResponseDto.getStatus());

	}

	@Test(expected = DataNotFoundException.class)
	public void getStatusOfWorkingHoursServiceExceptionTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-12T17:59:59.999Z");

	}

	@Test(expected = DataNotFoundException.class)
	public void getStatusOfWorkingHoursDataNotFoundExceptionTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString())).thenReturn(null);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-12T17:59:59.999Z");

	}

	@Test(expected = DataNotFoundException.class)
	public void getStatusOfWorkingHoursDataNotFoundTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-12T17:59:59.999Z");

	}

	@Test
	public void getStatusOfWorkingHoursRejectedWorkingHourTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findByIdAndLangCode(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);
		LocalTime startTime = LocalTime.of(10, 00, 000);
		LocalTime endTime = LocalTime.of(15, 00, 000);
		registrationCenter.setCenterStartTime(startTime);
		registrationCenter.setCenterEndTime(endTime);

		ResgistrationCenterStatusResponseDto resgistrationCenterStatusResponseDto = registrationCenterService
				.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-12T17:59:59.999Z");

		Assert.assertEquals(MasterDataConstant.VALID, resgistrationCenterStatusResponseDto.getStatus());

	}

	@Test(expected = RequestException.class)
	public void invalidDateFormatTest() throws Exception {
		Mockito.when(registrationCenterRepository.validateDateWithHoliday(Mockito.any(), Mockito.any()))
				.thenReturn(false);
		Mockito.when(registrationCenterRepository.findById(Mockito.any(), Mockito.anyString()))
				.thenReturn(registrationCenter);

		registrationCenterService.validateTimeStampWithRegistrationCenter("1", "eng", "2017-12-1217:59:59.999Z");

	}
	// ---------------------------------Registration Center TestCases----------------------------------
	@Test(expected= RequestException.class)
	public void notAllCongfLangRegCenterCreateExcpTest() {
		registrationCenterService.createRegistrationCenterAdmin(requestNotAllLang);
	}
	
	@Test(expected= RequestException.class)
	public void invalideLongitudeRegCenterCreateExcpTest() {
		registrationCenterService.createRegistrationCenterAdmin(requestSetLongitudeInvalide);
	}
	
	@Test(expected= RequestException.class)
	public void duplicateLangCodeRegCenterCreateExcpTest() {
		registrationCenterService.createRegistrationCenterAdmin(requestDuplicateLang);
	}
	
	@Test(expected= RequestException.class)
	public void startTimeValidationRegCenterCreateExcpTest() {
		registrationCenterService.createRegistrationCenterAdmin(requestCenterTime);
	}
	
	@Test(expected= RequestException.class)
	public void lunchTimeValidationRegCenterCreateExcpTest() {
		registrationCenterService.createRegistrationCenterAdmin(requestLunchTime);
	}
	
	// ----------------------- update Registration center-----------------------
	@Test(expected= RequestException.class)
	public void notAllCongfLangRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestNotAllLang);
	}
	
	@Test(expected= RequestException.class)
	public void invalideIDRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestInvalideID);
	}
	
	@Test(expected= RequestException.class)
	public void invalideLongitudeRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestSetLongitudeInvalide);
	}
	
	@Test(expected= RequestException.class)
	public void duplicateIDLangCodeRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestDuplicateIDLang);
	}
	
	@Test(expected= RequestException.class)
	public void startTimeValidationRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestCenterTime);
	}
	
	@Test(expected= RequestException.class)
	public void lunchTimeValidationRegCenterUpdateExcpTest() {
		registrationCenterService.updateRegistrationCenterAdmin(updRequestLunchTime);
	}
	

}