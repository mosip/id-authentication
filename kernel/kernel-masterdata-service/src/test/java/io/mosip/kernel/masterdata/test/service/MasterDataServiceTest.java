package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import io.mosip.kernel.masterdata.dto.ApplicationData;
import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationListDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDtoData;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.LocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.LocationRequestDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
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
import io.mosip.kernel.masterdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * @author Bal Vikash Sharma
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

	private RequestDto<ApplicationData> applicationRequestDto;


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

	private DocumentCategory documentCategory1;
	private DocumentCategory documentCategory2;

	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	@Autowired
	private LanguageService languageService;

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
	List<Object[]> locObjList=null;
	LocationCodeResponseDto locationCodeResponseDto=null;
	Location locationHierarchy=null;
	Location locationHierarchy1=null;
	LocationDto locationDtos=null;
	LocationRequestDto locationRequestDto= null;

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private TemplateFileFormatService templateFileFormatService;
	
	private TemplateFileFormat templateFileFormat;

	// private List<TemplateFileFormat> templateFileFormatList;

	private RequestDto<TemplateFileFormatData> templateFileFormatRequestDto;

	@Autowired
	private TemplateService templateService;

	private List<Template> templateList = new ArrayList<>();

	private List<TemplateDto> templateDtoList;

	@MockBean
	DocumentTypeRepository documentTypeRepository;

	@Autowired
	DocumentTypeService documentTypeService;

	List<DocumentType> documents = null;
	
	
	//-----------------------------DeviceType-------------------------------------------------
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@MockBean
	private MetaDataUtils metaUtils;
	
	//-----------------------------DeviceSpecification----------------------------------

	private List<DeviceSpecification> deviceSpecificationList ;
	private DeviceSpecification deviceSpecification;
	
	private DeviceSpecificationRequestDto deviceSpecificationRequestDto ;
	private DeviceSpecificationListDto deviceSpecificationListDto;
	private List<DeviceSpecificationDto> deviceSpecificationDtos ;
	private DeviceSpecificationDto deviceSpecificationDto ;
	
	
	@Before
	public void setUp() {
		appSetup();

		biometricAttrSetup();

		bioTypeSetup();

		blackListedSetup();

		// TO-DO device service not implemented

		deviceSpecSetup();

		docCategorySetup();

		langServiceSetup();

		// TO-DO location hierarchy service not implemented

		locationServiceSetup();
		// TO-DO machine detail service not implemented
		// TO-DO machine history service not implemented

		templateServiceSetup();

		templateFileFormatSetup();

		documentTypeSetup();
		
		deviceTypeSetUp();

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
		locationHierarchy.setHierarchyName(null);
		locationHierarchy.setParentLocCode(null);
		locationHierarchy.setLanguageCode("HIN");
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
		locationHierarchy1.setLanguageCode("KAN");
		locationHierarchy1.setCreatedBy("dfs");
		locationHierarchy1.setUpdatedBy("sdfsd");
		locationHierarchy1.setIsActive(true);
		locationHierarchies.add(locationHierarchy1);
		Object[] objectArray=new Object[3];
		objectArray[0]=(short)0;
		objectArray[1]="COUNTRY";
		objectArray[2]=true;
	    locObjList=new ArrayList<>();
		locObjList.add(objectArray);
		LocationDto locationDto= new LocationDto();
		locationDto.setCode("KAR");
		locationDto.setName("KARNATAKA");
		locationDto.setHierarchyLevel(2);
		locationDto.setHierarchyName("STATE");
		locationDto.setLanguageCode("FRA");
		locationDto.setParentLocCode("IND");
		locationDto.setIsActive(true);
		
		locationRequestDto = new LocationRequestDto();
		locationRequestDto.setLocations(locationDto);
		
		
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
		
		deviceSpecificationRequestDto = new DeviceSpecificationRequestDto();
		deviceSpecificationListDto = new DeviceSpecificationListDto();
		deviceSpecificationDtos = new ArrayList<>();
		deviceSpecificationDto = new DeviceSpecificationDto();
		deviceSpecificationDto.setId("100");
		deviceSpecificationDto.setDeviceTypeCode("Laptop");
		deviceSpecificationDto.setLangCode("ENG");
		deviceSpecificationDtos.add(deviceSpecificationDto);
		//deviceSpecificationListDto.setDeviceSpecificationDtos(deviceSpecificationDtos);
		deviceSpecificationRequestDto.setRequest(deviceSpecificationListDto);
	}

	private void blackListedSetup() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("ENG");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
	}

	private void bioTypeSetup() {
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

	private void appSetup() {
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

		applicationRequestDto = new RequestDto<ApplicationData>();
		ApplicationData request = new ApplicationData();
		applicationDto = new ApplicationDto();
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLangCode("ENG");
		// List<ApplicationDto> applicationDtos = new ArrayList<>();
		// applicationDtos.add(applicationDto);
		request.setApplicationtype(applicationDto);
		applicationRequestDto.setRequest(request);
	}

	private void templateFileFormatSetup() {
		templateFileFormat = new TemplateFileFormat();
		// templateFileFormatList = new ArrayList<>();
		templateFileFormat.setCode("xml");
		templateFileFormat.setLangCode("ENG");
		// templateFileFormatList.add(templateFileFormat);

		templateFileFormatRequestDto = new RequestDto<TemplateFileFormatData>();
		TemplateFileFormatData request = new TemplateFileFormatData();
		TemplateFileFormatDto templateFileFormatDto = new TemplateFileFormatDto();
		templateFileFormatDto.setCode("xml");
		templateFileFormatDto.setLangCode("ENG");
		// List<TemplateFileFormatDto> templateFileFormatDtos = new ArrayList<>();
		// templateFileFormatDtos.add(templateFileFormatDto);
		// request.setTemplateFileFormatDtos(templateFileFormatDtos);
		request.setTemplateFileFormat(templateFileFormatDto);
		templateFileFormatRequestDto.setRequest(request);
	}
	
	private DeviceTypeRequestDto reqTypeDto;
	private DeviceTypeDtoData request;
	private List<DeviceTypeDto> deviceTypeDtoList;
	private DeviceTypeDto deviceTypeDto;

	private List<DeviceType> deviceTypeList;
	private DeviceType deviceType;
	private List<CodeAndLanguageCodeId> codeLangCodeIds;
	private CodeAndLanguageCodeId codeAndLanguageCodeId;

	
	private void deviceTypeSetUp() {

		reqTypeDto = new DeviceTypeRequestDto();
		request = new DeviceTypeDtoData();
		deviceTypeDtoList = new ArrayList<>();
		deviceTypeDto = new DeviceTypeDto();

		deviceTypeDto.setCode("Laptop");
		deviceTypeDto.setCode("Laptop");
		deviceTypeDto.setLangCode("ENG");
		deviceTypeDto.setName("HP");
		deviceTypeDto.setDescription("Laptop Desc");
		deviceTypeDtoList.add(deviceTypeDto);
		//request.setDeviceTypeDtos(deviceTypeDtoList);
		reqTypeDto.setRequest(request);

		deviceTypeList = new ArrayList<>();
		deviceType = new DeviceType();
		deviceType.setCode("Laptop");
		deviceType.setLangCode("ENG");
		deviceType.setName("HP");
		deviceType.setDescription("Laptop Desc");
		deviceTypeList.add(deviceType);

		codeLangCodeIds = new ArrayList<>();
		codeAndLanguageCodeId = new CodeAndLanguageCodeId();
		codeAndLanguageCodeId.setCode("Laptop");
		codeAndLanguageCodeId.setLangCode("ENG");
		codeLangCodeIds.add(codeAndLanguageCodeId);

	}

	// ----------------------- ApplicationServiceTest ----------------
	@Test
	public void getAllApplicationSuccess() {
		Mockito.when(applicationRepository.findAllByIsDeletedFalse(Mockito.eq(Application.class)))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService.getAllApplication();
		List<ApplicationDto> applicationDtos = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtos.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtos.get(0).getName());
	}

	@Test
	public void getAllApplicationByLanguageCodeSuccess() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService
				.getAllApplicationByLanguageCode(Mockito.anyString());
		List<ApplicationDto> applicationDtoList = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtoList.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtoList.get(0).getName());
	}

	@Test
	public void getApplicationByCodeAndLangCodeSuccess() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
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

		CodeAndLanguageCodeId codeAndLanguageCodeId = applicationService.addApplicationData(applicationRequestDto);
		assertEquals(applicationRequestDto.getRequest().getApplicationtype().getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(applicationRequestDto.getRequest().getApplicationtype().getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addApplicationDataFetchException() {
		Mockito.when(applicationRepository.create(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		applicationService.addApplicationData(applicationRequestDto);
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationFetchException() {
		Mockito.when(applicationRepository.findAllByIsDeletedFalse(Mockito.eq(Application.class)))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplication();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationNotFoundException() {
		applicationList = new ArrayList<>();
		Mockito.when(applicationRepository.findAllByIsDeletedFalse(Application.class)).thenReturn(applicationList);
		applicationService.getAllApplication();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationByLanguageCodeFetchException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationByLanguageCodeNotFoundException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<Application>());
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getApplicationByCodeAndLangCodeFetchException() {
		Mockito.when(
				applicationRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getApplicationByCodeAndLangCodeNotFoundException() {
		Mockito.when(
				applicationRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}
	// ------------------ BiometricAttributeServiceTest -----------------

	@Test
	public void getBiometricAttributeTest() {
		String biometricTypeCode = "iric";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(biometricTypeCode,
				langCode)).thenReturn(biometricattributes);

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
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(biometricTypeCode,
				langCode)).thenReturn(empityList);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionForNullTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(biometricTypeCode,
				langCode)).thenReturn(null);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionInGetAllTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(biometricTypeCode,
				langCode)).thenThrow(DataAccessResourceFailureException.class);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	// ------------------ BiometricTypeServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesFetchException() {
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalse(Mockito.eq(BiometricType.class)))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesNotFoundException() {
		biometricTypeList = new ArrayList<>();
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalse(BiometricType.class))
				.thenReturn(biometricTypeList);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesByLanguageCodeFetchException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesByLanguageCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<BiometricType>());
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getBiometricTypeByCodeAndLangCodeFetchException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getBiometricTypeByCodeAndLangCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void getAllBioTypesSuccess() {
		Mockito.when(biometricTypeRepository.findAllByIsDeletedFalse(Mockito.eq(BiometricType.class)))
				.thenReturn(biometricTypeList);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService.getAllBiometricTypes();
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	@Test
	public void getAllBioTypesByLanguageCodeSuccess() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(biometricTypeList);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService
				.getAllBiometricTypesByLanguageCode(Mockito.anyString());
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	@Test
	public void getBioTypeByCodeAndLangCodeSuccess() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(biometricType1);
		BiometricTypeResponseDto biometricTypeResponseDto = biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(),
				Mockito.anyString());
		assertEquals(biometricType1.getCode(), biometricTypeResponseDto.getBiometrictypes().get(0).getCode());
		assertEquals(biometricType1.getName(), biometricTypeResponseDto.getBiometrictypes().get(0).getName());
	}

	// ------------------ BlacklistedServiceTest -----------------

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

	// ------------------ DeviceSpecificationServiceTest -----------------

	@Test
	public void findDeviceSpecificationByLangugeCodeTest() {
		String languageCode = "ENG";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalse(languageCode))
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
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalse(languageCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noDeviceSpecRecordsFoudExceptionForNullTest() {
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalse(languageCode)).thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataDeviceSpecAccessExceptionInGetAllTest() {
		String languageCode = "eng";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalse(languageCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test
	public void findDeviceSpecificationByLangugeCodeAndDeviceTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(languageCode,
				deviceTypeCode)).thenReturn(deviceSpecificationListWithDeviceTypeCode);

		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
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
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(deviceTypeCode,
				deviceTypeCode)).thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionnDeviceSpecificationByDevicTypeCodeForNullTest() {
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(deviceTypeCode,
				deviceTypeCode)).thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionnDeviceSpecificationByDevicTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(languageCode,
				deviceTypeCode)).thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}
	
	/*@Test
	public void addDeviceSpecificationsTest() {
		Mockito.when(deviceSpecificationRepository.saveAll(Mockito.any())).thenReturn(deviceSpecificationList);
		DeviceSpecPostResponseDto deviceSpecPostResponseDto = deviceSpecificationService
				.saveDeviceSpecifications(deviceSpecificationRequestDto);
		assertEquals(deviceSpecificationListDto.getDeviceSpecificationDtos().get(0).getId(),
				deviceSpecPostResponseDto.getResults().get(0).getId());
	}
	
	
	@Test(expected = MasterDataServiceException.class)
	public void testaddSpecificationThrowsDataAccessException() {
		Mockito.when(deviceSpecificationRepository.saveAll(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		deviceSpecificationService.saveDeviceSpecifications(deviceSpecificationRequestDto);
	}*/

	// ------------------ DocumentCategoryServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategorysFetchException() {
		Mockito.when(documentCategoryRepository.findAllByIsDeletedFalse(Mockito.eq(DocumentCategory.class)))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryNotFoundException() {
		Mockito.when(documentCategoryRepository.findAllByIsDeletedFalse(DocumentCategory.class))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategoryByLaguageCodeFetchException() {
		Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryByLaguageCodeNotFound() {
		Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getDocumentCategoryByCodeAndLangCodeFetchException() {
		Mockito.when(documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getDocumentCategoryByCodeAndLangCodeNotFoundException() {
		Mockito.when(documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	// @Test
	// public void getAllDocumentCategorySuccess() {
	//
	// Mockito.when(documentCategoryRepository.findAllByIsDeletedFalse(DocumentCategory.class))
	// .thenReturn(documentCategoryList);
	// List<DocumentCategoryDto> DocumentCategoryDtoList =
	// documentCategoryService.getAllDocumentCategory();
	// assertEquals(documentCategoryList.get(0).getCode(),
	// DocumentCategoryDtoList.get(0).getCode());
	// assertEquals(documentCategoryList.get(0).getName(),
	// DocumentCategoryDtoList.get(0).getName());
	// }
	//
	// @Test
	// public void getAllDocumentCategoryByLaguageCodeSuccess() {
	// Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
	// .thenReturn(documentCategoryList);
	// List<DocumentCategoryDto> DocumentCategoryDtoList = documentCategoryService
	// .getAllDocumentCategoryByLaguageCode("ENG");
	// assertEquals(documentCategoryList.get(0).getCode(),
	// DocumentCategoryDtoList.get(0).getCode());
	// assertEquals(documentCategoryList.get(0).getName(),
	// DocumentCategoryDtoList.get(0).getName());
	// }
	//
	// @Test
	// public void getDocumentCategoryByCodeAndLangCodeSuccess() {
	// Mockito.when(documentCategoryRepository
	// .findByCodeAndLangCodeAndIsDeletedFalse(Mockito.anyString(),
	// Mockito.anyString()))
	// .thenReturn(documentCategory1);
	// DocumentCategoryDto actual =
	// documentCategoryService.getDocumentCategoryByCodeAndLangCode("101", "ENG");
	// assertEquals(documentCategory1.getCode(), actual.getCode());
	// assertEquals(documentCategory1.getName(), actual.getName());
	// }

	// ------------------ LanguageServiceTest -----------------

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

	// ------------------ LocationServiceTest -----------------

	@Test()
	public void getLocationHierarchyTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString())).thenReturn(locObjList);
		LocationHierarchyResponseDto locationHierarchyResponseDto=locationHierarchyService.getLocationDetails(Mockito.anyString());
		Assert.assertEquals("COUNTRY",locationHierarchyResponseDto.getLocations().get(0).getLocationHierarchyName());
	}
	
	@Test(expected=DataNotFoundException.class)
	public void getLocationHierarchyNoDataFoundExceptionTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString())).thenReturn(new ArrayList<Object[]>());
		locationHierarchyService.getLocationDetails(Mockito.anyString());
		
	}
	
	@Test(expected=MasterDataServiceException.class)
	public void getLocationHierarchyFetchExceptionTest() {
		Mockito.when(locationHierarchyRepository.findDistinctLocationHierarchyByIsDeletedFalse(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationDetails(Mockito.anyString());
		
	}
	@Test()
	public void getLocationHierachyBasedOnLangAndLoc() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(locationHierarchies);

		LocationResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationHierarchyByLangCode("IND", "HIN");
		Assert.assertEquals(locationHierarchyResponseDto.getLocations().get(0).getCode(), "IND");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(null);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTestWithEmptyList() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(new ArrayList<Location>());
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchyDataAccessExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");
	}
	
	@Test
	public void locationHierarchySaveTest() {
		Mockito.when(locationHierarchyRepository.create(Mockito.any())).thenReturn(locationHierarchy);
		locationHierarchyService.saveLocationHierarchy(locationRequestDto);
	}
	
	@Test(expected=MasterDataServiceException.class)
	public void locationHierarchySaveNegativeTest() {
		Mockito.when(locationHierarchyRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		locationHierarchyService.saveLocationHierarchy(locationRequestDto);
	}
	
	

	// ------------------ TemplateServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByIsDeletedFalse(Mockito.eq(Template.class)))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByIsDeletedFalse(Mockito.eq(Template.class))).thenReturn(templateList);
		templateService.getAllTemplate();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(templateList);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(templateList);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test
	public void getAllTemplateTest() {
		Mockito.when(templateRepository.findAllByIsDeletedFalse(Template.class)).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplate();

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCode(Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() {
		Mockito.when(templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(),
				Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	// ------------------------------------TemplateFileFormatServiceTest---------------------------
	@Test
	public void addTemplateFileFormatSuccess() {
		Mockito.when(templateFileFormatRepository.create(Mockito.any())).thenReturn(templateFileFormat);

		CodeAndLanguageCodeId codeAndLanguageCodeId = templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
		assertEquals(templateFileFormat.getCode(), codeAndLanguageCodeId.getCode());
		assertEquals(templateFileFormat.getLangCode(), codeAndLanguageCodeId.getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addTemplateFileFormatInsertExceptionTest() {
		Mockito.when(templateFileFormatRepository.create(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
	}
	
	// ----------------------------------DocumentTypeServiceTest-------------------------

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

	//----------------------------------------DeviceTypeServiceImplTest------------------------------------------------
	
	/*@Test
	public void addDeviceTypesTest() {
		Mockito.when(deviceTypeRepository.saveAll(Mockito.any())).thenReturn(deviceTypeList);
		PostResponseDto postResponseDto = deviceTypeService.saveDeviceTypes(reqTypeDto);
		assertEquals(request.getDeviceTypeDtos().get(0).getCode(), postResponseDto.getResults().get(0).getCode());
	}

	
	@Test(expected = MasterDataServiceException.class)
	public void testaddDeviceTypesThrowsDataAccessException() {
		Mockito.when(deviceTypeRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		deviceTypeService.saveDeviceTypes(reqTypeDto);
	}*/

	// ----------------------------------------------- Blacklisted word validator
	// ----------------------
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
}