package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.dto.ApplicationListDto;
import io.mosip.kernel.masterdata.dto.ApplicationRequestDto;
import io.mosip.kernel.masterdata.dto.ApplicationResponseDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatListDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
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

	private ApplicationRequestDto applicationRequestDto;

	@MockBean
	private MetaDataUtils metaUtils;

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
	private LanguageRequestResponseDto resp;
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

	@MockBean
	private TemplateRepository templateRepository;

	@MockBean
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	private List<TemplateFileFormat> templateFileFormatList;

	private TemplateFileFormatRequestDto templateFileFormatRequestDto;

	@Autowired
	private TemplateService templateService;

	private List<Template> templateList = new ArrayList<>();

	private List<TemplateDto> templateDtoList;

	@MockBean
	DocumentTypeRepository documentTypeRepository;

	@Autowired
	DocumentTypeService documentTypeService;

	List<DocumentType> documents = null;

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
		template.setLanguageCode("HIN");
		template.setCreatedBy("Neha");
		template.setCreatedtimes(LocalDateTime.of(2018, Month.NOVEMBER, 12, 0, 0, 0));
		template.setIsActive(true);
		template.setIsDeleted(false);

		templateList.add(template);
	}

	private void locationServiceSetup() {
		locationHierarchies = new ArrayList<>();
		Location locationHierarchy = new Location();
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
		Location locationHierarchy1 = new Location();
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

		resp = new LanguageRequestResponseDto();
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
		DeviceSpecification deviceSpecification = new DeviceSpecification();
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

		applicationRequestDto = new ApplicationRequestDto();
		ApplicationListDto request = new ApplicationListDto();
		ApplicationDto applicationDto = new ApplicationDto();
		applicationDto.setCode("101");
		applicationDto.setName("pre-registeration");
		applicationDto.setDescription("Pre-registration Application Form");
		applicationDto.setLangCode("ENG");
		List<ApplicationDto> applicationDtos = new ArrayList<>();
		applicationDtos.add(applicationDto);
		request.setApplicationtypes(applicationDtos);
		applicationRequestDto.setRequest(request);
	}

	private void templateFileFormatSetup() {
		TemplateFileFormat templateFileFormat = new TemplateFileFormat();
		templateFileFormatList = new ArrayList<>();
		templateFileFormat.setCode("xml");
		templateFileFormat.setLangCode("ENG");
		templateFileFormatList.add(templateFileFormat);

		templateFileFormatRequestDto = new TemplateFileFormatRequestDto();
		TemplateFileFormatListDto request = new TemplateFileFormatListDto();
		TemplateFileFormatDto templateFileFormatDto = new TemplateFileFormatDto();
		templateFileFormatDto.setCode("xml");
		templateFileFormatDto.setLangCode("ENG");
		List<TemplateFileFormatDto> templateFileFormatDtos = new ArrayList<>();
		templateFileFormatDtos.add(templateFileFormatDto);
		request.setTemplateFileFormatDtos(templateFileFormatDtos);
		templateFileFormatRequestDto.setRequest(request);
	}

	// ----------------------- ApplicationServiceTest ----------------
	@Test
	public void getAllApplicationSuccess() {
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Application.class)))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService.getAllApplication();
		List<ApplicationDto> applicationDtos = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtos.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtos.get(0).getName());
	}

	@Test
	public void getAllApplicationByLanguageCodeSuccess() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(applicationList);
		ApplicationResponseDto applicationResponseDto = applicationService
				.getAllApplicationByLanguageCode(Mockito.anyString());
		List<ApplicationDto> applicationDtoList = applicationResponseDto.getApplicationtypes();
		assertEquals(applicationList.get(0).getCode(), applicationDtoList.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtoList.get(0).getName());
	}

	@Test
	public void getApplicationByCodeAndLangCodeSuccess() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(application1);
		ApplicationResponseDto applicationResponseDto = applicationService
				.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
		List<ApplicationDto> actual = applicationResponseDto.getApplicationtypes();
		assertEquals(application1.getCode(), actual.get(0).getCode());
		assertEquals(application1.getName(), actual.get(0).getName());
	}

	@Test
	public void addApplicationDataSuccess() {
		Mockito.when(applicationRepository.saveAll(Mockito.any())).thenReturn(applicationList);

		PostResponseDto postResponseDto = applicationService.addApplicationData(applicationRequestDto);
		assertEquals(applicationList.get(0).getCode(), postResponseDto.getResults().get(0).getCode());
		assertEquals(applicationList.get(0).getLangCode(), postResponseDto.getResults().get(0).getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addApplicationDataFetchException() {
		Mockito.when(applicationRepository.saveAll(Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.addApplicationData(applicationRequestDto);
	}
	
	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationFetchException() {
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Application.class)))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplication();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationNotFoundException() {
		applicationList = new ArrayList<>();
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Application.class))
				.thenReturn(applicationList);
		applicationService.getAllApplication();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationByLanguageCodeFetchException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationByLanguageCodeNotFoundException() {
		Mockito.when(applicationRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<Application>());
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getApplicationByCodeAndLangCodeFetchException() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getApplicationByCodeAndLangCodeNotFoundException() {
		Mockito.when(applicationRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}
	// ------------------ BiometricAttributeServiceTest -----------------

	@Test
	public void getBiometricAttributeTest() {
		String biometricTypeCode = "iric";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
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
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenReturn(empityList);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionForNullTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenReturn(null);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionInGetAllTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenThrow(DataAccessResourceFailureException.class);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	// ------------------ BiometricTypeServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesFetchException() {
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(BiometricType.class)))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesNotFoundException() {
		biometricTypeList = new ArrayList<>();
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(BiometricType.class))
				.thenReturn(biometricTypeList);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesByLanguageCodeFetchException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesByLanguageCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<BiometricType>());
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getBiometricTypeByCodeAndLangCodeFetchException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getBiometricTypeByCodeAndLangCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void getAllBioTypesSuccess() {
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(BiometricType.class)))
				.thenReturn(biometricTypeList);
		List<BiometricTypeDto> biometricTypeDtoList = biometricTypeService.getAllBiometricTypes();
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeDtoList.get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeDtoList.get(0).getName());
	}

	@Test
	public void getAllBioTypesByLanguageCodeSuccess() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(biometricTypeList);
		List<BiometricTypeDto> biometricTypeDtoList = biometricTypeService
				.getAllBiometricTypesByLanguageCode(Mockito.anyString());
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeDtoList.get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeDtoList.get(0).getName());
	}

	@Test
	public void getBioTypeByCodeAndLangCodeSuccess() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(biometricType1);
		BiometricTypeDto actual = biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(),
				Mockito.anyString());
		assertEquals(biometricType1.getCode(), actual.getCode());
		assertEquals(biometricType1.getName(), actual.getName());
	}

	// ------------------ BlacklistedServiceTest -----------------

	@Test(expected = RequestException.class)
	public void testGetAllBlacklistedWordsNullvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode(null);
	}

	@Test(expected = RequestException.class)
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
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
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
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noDeviceSpecRecordsFoudExceptionForNullTest() {
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataDeviceSpecAccessExceptionInGetAllTest() {
		String languageCode = "eng";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test
	public void findDeviceSpecificationByLangugeCodeAndDeviceTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(languageCode, deviceTypeCode))
				.thenReturn(deviceSpecificationListWithDeviceTypeCode);

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
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(deviceTypeCode, deviceTypeCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionnDeviceSpecificationByDevicTypeCodeForNullTest() {
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(deviceTypeCode, deviceTypeCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionnDeviceSpecificationByDevicTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(languageCode, deviceTypeCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}

	// ------------------ DocumentCategoryServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategorysFetchException() {
		Mockito.when(
				documentCategoryRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(DocumentCategory.class)))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryNotFoundException() {
		Mockito.when(documentCategoryRepository.findAllByIsActiveTrueAndIsDeletedFalse(DocumentCategory.class))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategory();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllDocumentCategoryByLaguageCodeFetchException() {
		Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllDocumentCategoryByLaguageCodeNotFound() {
		Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<DocumentCategory>());
		documentCategoryService.getAllDocumentCategoryByLaguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getDocumentCategoryByCodeAndLangCodeFetchException() {
		Mockito.when(documentCategoryRepository
				.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getDocumentCategoryByCodeAndLangCodeNotFoundException() {
		Mockito.when(documentCategoryRepository
				.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		documentCategoryService.getDocumentCategoryByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	// @Test
	// public void getAllDocumentCategorySuccess() {
	//
	// Mockito.when(documentCategoryRepository.findAllByIsActiveTrueAndIsDeletedFalse(DocumentCategory.class))
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
	// Mockito.when(documentCategoryRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
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
	// .findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
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
		Mockito.when(languageRepository.findAllByIsActiveTrueAndIsDeletedFalse()).thenReturn(languages);
		LanguageRequestResponseDto dto = languageService.getAllLaguages();
		assertNotNull(dto);
		assertEquals(2, dto.getLanguages().size());
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundException() {
		Mockito.when(languageRepository.findAllByIsActiveTrueAndIsDeletedFalse()).thenReturn(null);
		languageService.getAllLaguages();
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundExceptionWhenNoLanguagePresent() {
		Mockito.when(languageRepository.findAllByIsActiveTrueAndIsDeletedFalse()).thenReturn(new ArrayList<Language>());
		languageService.getAllLaguages();
	}

	@Test(expected = MasterDataServiceException.class)
	public void testLanguageFetchException() {
		Mockito.when(languageRepository.findAllByIsActiveTrueAndIsDeletedFalse())
				.thenThrow(HibernateObjectRetrievalFailureException.class);
		languageService.getAllLaguages();
	}

	// ------------------ LocationServiceTest -----------------

	@Test()
	public void getLocationHierachyBasedOnLangAndLoc() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("IND", "HIN"))
				.thenReturn(locationHierarchies);

		LocationResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationHierarchyByLangCode("IND", "HIN");
		Assert.assertEquals(locationHierarchyResponseDto.getLocations().get(0).getLocationCode(), "IND");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("IND", "HIN"))
				.thenReturn(null);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTestWithEmptyList() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("IND", "HIN"))
				.thenReturn(new ArrayList<Location>());
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchyDataAccessExceptionTest() {
		Mockito.when(locationHierarchyRepository
				.findLocationHierarchyByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("IND", "HIN"))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");
	}

	// ------------------ TemplateServiceTest -----------------

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Template.class)))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplate();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Template.class)))
				.thenReturn(templateList);
		templateService.getAllTemplate();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(templateList);

		templateService.getAllTemplateByLanguageCode("HIN");
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeFetchExceptionTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeNotFoundExceptionTest() {
		templateList = new ArrayList<>();
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode("HIN", "EMAIL");
	}

	@Test
	public void getAllTemplateTest() {
		Mockito.when(templateRepository.findAllByIsActiveTrueAndIsDeletedFalse(Template.class))
				.thenReturn(templateList);
		templateDtoList = templateService.getAllTemplate();

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCode(Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	@Test
	public void getAllTemplateByLanguageCodeAndTemplateTypeCodeTest() {
		Mockito.when(templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsActiveTrueAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString())).thenReturn(templateList);
		templateDtoList = templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(Mockito.anyString(),
				Mockito.anyString());

		assertEquals(templateList.get(0).getId(), templateDtoList.get(0).getId());
		assertEquals(templateList.get(0).getName(), templateDtoList.get(0).getName());
	}

	// ------------------------------------TemplateFileFormatServiceTest---------------------------
	@Test
	public void addTemplateFileFormatSuccess() {
		Mockito.when(templateFileFormatRepository.saveAll(Mockito.any())).thenReturn(templateFileFormatList);

		PostResponseDto postResponseDto = templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
		assertEquals(templateFileFormatList.get(0).getCode(), postResponseDto.getResults().get(0).getCode());
		assertEquals(templateFileFormatList.get(0).getLangCode(), postResponseDto.getResults().get(0).getLangCode());
	}

	@Test(expected = MasterDataServiceException.class)
	public void addTemplateFileFormatInsertExceptionTest() {
		Mockito.when(templateFileFormatRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
	}
	
	// ----------------------------------DocumentTypeServiceTest-------------------------

	@Test
	public void getAllValidDocumentTypeTest() {
		String documentCategoryCode = "iric";
		String langCode = "eng";

		Mockito.when(documentTypeRepository.findByCodeAndLangCode(documentCategoryCode, langCode))
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
		Mockito.when(documentTypeRepository.findByCodeAndLangCode(documentCategoryCode, langCode))
				.thenReturn(entitydocuments);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

	@Test(expected = DataNotFoundException.class)
	public void documentTypeNoRecordsFoudExceptionForNullTest() {
		String documentCategoryCode = "poc";
		String langCode = "eng";
		Mockito.when(documentTypeRepository.findByCodeAndLangCode(documentCategoryCode, langCode)).thenReturn(null);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void documentTypeDataAccessExceptionInGetAllTest() {
		String documentCategoryCode = "poc";
		String langCode = "eng";
		Mockito.when(documentTypeRepository.findByCodeAndLangCode(documentCategoryCode, langCode))
				.thenThrow(DataAccessResourceFailureException.class);
		documentTypeService.getAllValidDocumentType(documentCategoryCode, langCode);

	}

}
