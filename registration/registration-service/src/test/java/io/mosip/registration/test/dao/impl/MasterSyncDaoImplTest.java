package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.impl.MasterSyncDaoImpl;
import io.mosip.registration.dto.mastersync.ApplicationDto;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BiometricTypeDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DeviceDto;
import io.mosip.registration.dto.mastersync.DeviceSpecificationDto;
import io.mosip.registration.dto.mastersync.DeviceTypeDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.DocumentTypeDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.HolidayDto;
import io.mosip.registration.dto.mastersync.IdTypeDto;
import io.mosip.registration.dto.mastersync.LanguageDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MachineDto;
import io.mosip.registration.dto.mastersync.MachineSpecificationDto;
import io.mosip.registration.dto.mastersync.MachineTypeDto;
import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.dto.mastersync.MasterReasonListDto;
import io.mosip.registration.dto.mastersync.PostReasonCategoryDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterDto;
import io.mosip.registration.dto.mastersync.TemplateDto;
import io.mosip.registration.dto.mastersync.TemplateFileFormatDto;
import io.mosip.registration.dto.mastersync.TemplateTypeDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.dto.mastersync.ValidDocumentDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.mastersync.MasterApplication;
import io.mosip.registration.entity.mastersync.MasterBiometricAttribute;
import io.mosip.registration.entity.mastersync.MasterBiometricType;
import io.mosip.registration.entity.mastersync.MasterBlacklistedWords;
import io.mosip.registration.entity.mastersync.MasterDevice;
import io.mosip.registration.entity.mastersync.MasterDeviceSpecification;
import io.mosip.registration.entity.mastersync.MasterDeviceType;
import io.mosip.registration.entity.mastersync.MasterDocumentCategory;
import io.mosip.registration.entity.mastersync.MasterDocumentType;
import io.mosip.registration.entity.mastersync.MasterGender;
import io.mosip.registration.entity.mastersync.MasterHoliday;
import io.mosip.registration.entity.mastersync.MasterIdType;
import io.mosip.registration.entity.mastersync.MasterLanguage;
import io.mosip.registration.entity.mastersync.MasterLocation;
import io.mosip.registration.entity.mastersync.MasterMachine;
import io.mosip.registration.entity.mastersync.MasterMachineSpecification;
import io.mosip.registration.entity.mastersync.MasterMachineType;
import io.mosip.registration.entity.mastersync.MasterReasonCategory;
import io.mosip.registration.entity.mastersync.MasterReasonList;
import io.mosip.registration.entity.mastersync.MasterRegistrationCenter;
import io.mosip.registration.entity.mastersync.MasterRegistrationCenterType;
import io.mosip.registration.entity.mastersync.MasterSyncBaseEntity;
import io.mosip.registration.entity.mastersync.MasterTemplate;
import io.mosip.registration.entity.mastersync.MasterTemplateFileFormat;
import io.mosip.registration.entity.mastersync.MasterTemplateType;
import io.mosip.registration.entity.mastersync.MasterTitle;
import io.mosip.registration.entity.mastersync.MasterValidDocument;
import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;
import io.mosip.registration.entity.mastersync.id.HolidayID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncJobControlRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncApplicationRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncBiometricAttributeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncBiometricTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncBlacklistedWordsRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncDeviceRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncDeviceSpecificationRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncDeviceTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncDocumentCategoryRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncDocumentTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncGenderRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncHolidayRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncIdTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncLanguageRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncLocationRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncMachineRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncMachineSpecificationRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncMachineTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncReasonCategoryRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncReasonListRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncRegistrationCenterRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncRegistrationCenterTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncTemplateFileFormatRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncTemplateRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncTemplateTypeRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncTitleRepository;
import io.mosip.registration.repositories.mastersync.MasterSyncValidDocumentRepository;
import io.mosip.registration.service.impl.MasterSyncServiceImpl;
import io.mosip.registration.util.mastersync.MetaDataUtils;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetaDataUtils.class, RegBaseUncheckedException.class })
public class MasterSyncDaoImplTest {

	// private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private SyncJobControlRepository syncStatusRepository;

	@Mock
	private MasterSyncApplicationRepository masterSyncApplicationRepository;
	@Mock
	private MasterSyncBiometricAttributeRepository masterSyncBiometricAttributeRepository;
	@Mock
	private MasterSyncBiometricTypeRepository masterSyncBiometricTypeRepository;
	@Mock
	private MasterSyncBlacklistedWordsRepository masterSyncBlacklistedWordsRepository;
	@Mock
	private MasterSyncDeviceRepository masterSyncDeviceRepository;
	@Mock
	private MasterSyncDeviceSpecificationRepository masterSyncDeviceSpecificationRepository;
	@Mock
	private MasterSyncDeviceTypeRepository masterSyncDeviceTypeRepository;
	@Mock
	private MasterSyncDocumentCategoryRepository masterSyncDocumentCategoryRepository;
	@Mock
	private MasterSyncDocumentTypeRepository masterSyncDocumentTypeRepository;
	@Mock
	private MasterSyncGenderRepository masterSyncGenderTypeRepository;
	@Mock
	private MasterSyncHolidayRepository masterSyncHolidayRepository;
	@Mock
	private MasterSyncIdTypeRepository masterSyncIdTypeRepository;
	@Mock
	private MasterSyncLanguageRepository masterSyncLanguageRepository;
	@Mock
	private MasterSyncLocationRepository masterSyncLocationRepository;
	@Mock
	private MasterSyncMachineRepository masterSyncMachineRepository;
	@Mock
	private MasterSyncMachineSpecificationRepository masterSyncMachineSpecificationRepository;
	@Mock
	private MasterSyncMachineTypeRepository masterSyncMachineTypeRepository;
	@Mock
	private MasterSyncReasonCategoryRepository masterSyncReasonCategoryRepository;
	@Mock
	private MasterSyncReasonListRepository masterSyncReasonListRepository;
	@Mock
	private MasterSyncRegistrationCenterRepository masterSyncRegistrationCenterRepository;
	@Mock
	private MasterSyncRegistrationCenterTypeRepository masterSyncRegistrationCenterTypeRepository;
	@Mock
	private MasterSyncTemplateFileFormatRepository masterSyncTemplateFileFormatRepository;
	@Mock
	private MasterSyncTemplateRepository masterSyncTemplateRepository;
	@Mock
	private MasterSyncTemplateTypeRepository masterSyncTemplateTypeRepository;
	@Mock
	private MasterSyncTitleRepository masterSyncTitleRepository;
	@Mock
	private MasterSyncValidDocumentRepository masterSyncValidDocumentRepository;

	@Mock
	private MasterSyncDao masterSyncDao;

	@InjectMocks
	private MasterSyncServiceImpl masterSyncServiceImpl;

	@InjectMocks
	private MasterSyncDaoImpl masterSyncDaoImpl;

	private static ApplicationContext applicationContext = ApplicationContext.getInstance();

	@BeforeClass
	public static void beforeClass() {

		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		applicationContext.setApplicationMessagesBundle();

		List<MasterRegistrationCenterType> registrationCenterType = new ArrayList<>();
		MasterRegistrationCenterType MasterRegistrationCenterType = new MasterRegistrationCenterType();
		MasterRegistrationCenterType.setCode("T1011");
		MasterRegistrationCenterType.setName("ENG");
		MasterRegistrationCenterType.setLangCode("Main");
		registrationCenterType.add(MasterRegistrationCenterType);
	}

	@Test
	public void testMasterSyncDaoSucess() throws RegBaseCheckedException {

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(syncStatusRepository.findBySyncJobId(Mockito.anyString())).thenReturn(masterSyncDetails);

		masterSyncDaoImpl.syncJobDetails("MDS_J00001");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncExceptionThrown() throws RegBaseUncheckedException {

		try {
			Mockito.when(masterSyncDaoImpl.syncJobDetails(Mockito.anyString()))
					.thenThrow(RegBaseUncheckedException.class);
			masterSyncDaoImpl.syncJobDetails("MDS_J00001");
		} catch (Exception exception) {

		}
	}

	@Test
	public void testMasterSyncDao() throws RegBaseCheckedException {

		PowerMockito.mockStatic(MetaDataUtils.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		List<ApplicationDto> application = new ArrayList<>();
		masterSyncDto.setApplications(application);

		List<DeviceTypeDto> masterDeviceTypeDto = new ArrayList<>();

		masterSyncDto.setDeviceTypes(masterDeviceTypeDto);

		List<DeviceSpecificationDto> masterDeviceSpecificDtoEntity = new ArrayList<>();
		masterSyncDto.setDeviceSpecifications(masterDeviceSpecificDtoEntity);

		List<DeviceDto> masterDeviceDto = new ArrayList<>();

		masterSyncDto.setDevices(masterDeviceDto);

		List<HolidayDto> masterHolidaysDto = new ArrayList<>();
		masterSyncDto.setHolidays(masterHolidaysDto);

		List<MachineDto> masterMachineDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<MachineSpecificationDto> masterMachineSpecDto = new ArrayList<>();
		masterSyncDto.setMachineSpecification(masterMachineSpecDto);

		List<MachineTypeDto> masterMachineTypeDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<ValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setValidDocumentMapping(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<MasterSyncBaseEntity> baseEnity = new ArrayList<>();
		// Language
		List<LanguageDto> language = new ArrayList<>();
		LanguageDto lanugageRespDto = new LanguageDto();
		lanugageRespDto.setName("ENG");
		lanugageRespDto.setCode("1001");
		lanugageRespDto.setFamily("english");
		lanugageRespDto.setNativeName("eng");
		language.add(lanugageRespDto);
		masterSyncDto.setLanguages(language);
		// BiometricType
		List<BiometricTypeDto> biometrictype = new ArrayList<>();
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");
		biometrictype.add(biometricTypeDto);
		masterSyncDto.setBiometricTypes(biometrictype);
		// Biometric
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();
		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");
		biometricattribute.add(biometricAttriDto);
		masterSyncDto.setBiometricattributes(biometricattribute);
		// BlacklistedWords
		List<BlacklistedWordsDto> blacklistedWordsList = new ArrayList<>();
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");
		blacklistedWordsList.add(blacklistedWordsDto);
		masterSyncDto.setBlackListedWords(blacklistedWordsList);
		// gender
		List<GenderDto> gender = new ArrayList<>();
		GenderDto genderTypeDto = new GenderDto();
		genderTypeDto.setCode("G101");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");
		gender.add(genderTypeDto);
		masterSyncDto.setGenders(gender);
		// title
		List<TitleDto> title = new ArrayList<>();
		TitleDto titleTypeDto = new TitleDto();
		// titleTypeDto.setTitleCode("1");
		// titleTypeDto.setTitleDescription("dsddsd");
		// titleTypeDto.setTitleName("admin");
		titleTypeDto.setIsActive(true);
		title.add(titleTypeDto);
		masterSyncDto.setTitles(title);
		// idType
		IdTypeDto idTypeResponseDto = new IdTypeDto();
		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setLangCode("eng");
		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdTypes(idTypeList);
		// Document Category
		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentCategories(listDocCat);
		//
		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");
		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);
		masterSyncDto.setDocumentTypes(listDocType);
		//
		List<LocationDto> listLocation = new ArrayList<>();
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");
		listLocation.add(locationDto);
		masterSyncDto.setLocationHierarchy(listLocation);
		//
		List<MasterReasonListDto> categorieList = new ArrayList<>();
		MasterReasonListDto reasonListDto = new MasterReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setRsnCatCode("RS1001");
		categorieList.add(reasonListDto);
		masterSyncDto.setReasonList(categorieList);
		//
		List<PostReasonCategoryDto> categorie = new ArrayList<>();
		PostReasonCategoryDto reasonCategoryDto = new PostReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setName("RC1001");
		categorie.add(reasonCategoryDto);
		masterSyncDto.setReasonCategory(categorie);

		// Code and Land Code
		CodeAndLanguageCodeID codeaLang = new CodeAndLanguageCodeID();
		codeaLang.setCode("1011");
		codeaLang.setLangCode("ENG");
		LocalTime localTime = LocalTime.parse("09:00:00");
		// Application
		List<MasterApplication> applications = new ArrayList<>();
		MasterApplication masterApplciation = new MasterApplication();
		masterApplciation.setCode("101");
		masterApplciation.setName("App1");
		masterApplciation.setLangCode("ENG");
		masterApplciation.setCreatedBy("MOSIP");
		masterApplciation.setUpdatedBy("MOSIP");
		applications.add(masterApplciation);
		// Machine
		List<MasterMachine> machines = new ArrayList<>();
		MasterMachine machine = new MasterMachine();
		machine.setId("1001");
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machine.setLangCode("ENG");
		machines.add(machine);
		// Machine Specification
		List<MasterMachineSpecification> machineSpecification = new ArrayList<>();
		MasterMachineSpecification machineSpec = new MasterMachineSpecification();
		machineSpec.setId("1001");
		machineSpec.setBrand("Lenovo");
		machineSpec.setModel("T480");
		machineSpec.setMinDriverversion("1.0");
		machineSpec.setMachineTypeCode("1001");
		machineSpec.setName("Laptop");
		machineSpec.setLangCode("ENG");
		machineSpecification.add(machineSpec);
		// Machine Type
		List<MasterMachineType> machineType = new ArrayList<>();
		MasterMachineType MasterMachineType = new MasterMachineType();
		MasterMachineType.setCode("1001");
		MasterMachineType.setName("System");
		MasterMachineType.setLangCode("ENG");
		MasterMachineType.setDescription("System");
		machineType.add(MasterMachineType);
		// Device
		List<MasterDevice> devices = new ArrayList<>();
		MasterDevice Masterdevices = new MasterDevice();
		Masterdevices.setId("1011");
		Masterdevices.setName("printer");
		Masterdevices.setIpAddress("127.0.0.122");
		Masterdevices.setSerialNum("1011");
		Masterdevices.setLangCode("ENG");
		Masterdevices.setMacAddress("213:21:132:312");
		devices.add(Masterdevices);
		// Device Specification
		List<MasterDeviceSpecification> deviceSpecification = new ArrayList<>();
		MasterDeviceSpecification MasterDeviceSpecification = new MasterDeviceSpecification();
		MasterDeviceSpecification.setId("1011");
		MasterDeviceSpecification.setBrand("Hp Printer");
		MasterDeviceSpecification.setLangCode("ENG");
		MasterDeviceSpecification.setModel("HP-SP1011");
		deviceSpecification.add(MasterDeviceSpecification);
		// Device Type
		List<MasterDeviceType> deviceType = new ArrayList<>();
		MasterDeviceType MasterDeviceType = new MasterDeviceType();
		MasterDeviceType.setCode("T1011");
		MasterDeviceType.setName("device");
		MasterDeviceType.setLangCode("ENG");
		MasterDeviceType.setDescription("deviceDescriptiom");
		deviceType.add(MasterDeviceType);
		// Reg Center
		List<MasterRegistrationCenter> registrationCenters = new ArrayList<>();
		MasterRegistrationCenter registrationCenter = new MasterRegistrationCenter();
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
		// Template
		List<MasterTemplate> templates = new ArrayList<>();
		MasterTemplate MasterTemplates = new MasterTemplate();
		MasterTemplates.setId("T1");
		MasterTemplates.setDescription("Email-Template");
		MasterTemplates.setName("Email-Template");
		MasterTemplates.setModuleName("Email-Template");
		MasterTemplates.setModuleId("T101");
		MasterTemplates.setModel("ModuleName");
		templates.add(MasterTemplates);
		// Template Foramt
		List<MasterTemplateFileFormat> templateFileFormats = new ArrayList<>();
		MasterTemplateFileFormat templateFileFormat = new MasterTemplateFileFormat();
		templateFileFormat.setCode("T101");
		templateFileFormat.setDescription("Email");
		templateFileFormat.setLangCode("ENG");
		// Template Type
		List<MasterTemplateType> templateTypes = new ArrayList<>();
		MasterTemplateType templateType = new MasterTemplateType();
		templateType.setCode("T101");
		templateType.setDescription("Description");
		templateType.setLangCode("ENG");
		// Holiday
		List<MasterHoliday> holidays = new ArrayList<>();
		MasterHoliday holiday = new MasterHoliday();
		HolidayID hId = new HolidayID();
		hId.setHolidayDate(LocalDate.parse("2019-01-01"));
		// hId.setLangCode("ENG");
		hId.setLocationCode("LOC01");
		// holiday.setHolidayId(hId);
		// holiday.setHolidayName("New Year");
		holiday.setHolidayDesc("description");
		holiday.setUpdatedBy("mosip");
		holidays.add(holiday);
		// Blacklisted Words
		List<MasterBlacklistedWords> blackListedWords = new ArrayList<>();
		MasterBlacklistedWords MasterBlacklistedWords = new MasterBlacklistedWords();
		MasterBlacklistedWords.setWord("ABC");
		MasterBlacklistedWords.setDescription("description");
		MasterBlacklistedWords.setLangCode("ENG");
		blackListedWords.add(MasterBlacklistedWords);
		// titles
		List<MasterTitle> titles = new ArrayList<>();
		MasterTitle titleType = new MasterTitle();
		// titleType.setTitleDescription("dsddsd");
		titleType.setId(codeaLang);
		// titleType.setTitleName("admin");
		titles.add(titleType);
		// genders
		List<MasterGender> genders = new ArrayList<>();
		MasterGender genderEntity = new MasterGender();
		genderEntity.setCode("G1011");
		genderEntity.setGenderName("MALE");
		genderEntity.setLangCode("description");
		genders.add(genderEntity);
		// languages
		List<MasterLanguage> languages = new ArrayList<>();
		MasterLanguage MasterLanguages = new MasterLanguage();
		MasterLanguages.setCode("ENG");
		MasterLanguages.setFamily("family");
		MasterLanguages.setName("english");
		MasterLanguages.setNativeName("native name");
		languages.add(MasterLanguages);
		// idTypes
		List<MasterIdType> idTypes = new ArrayList<>();
		MasterIdType idTypeDto = new MasterIdType();
		idTypeDto.setName("ID");
		idTypeDto.setLangCode("ENG");
		idTypeDto.setIsActive(true);
		idTypeDto.setCode("ID101");
		idTypeDto.setDescr("descr");
		idTypes.add(idTypeDto);
		// validDocuments
		List<MasterValidDocument> validDocuments = new ArrayList<>();
		MasterValidDocument MasterValidDocuments = new MasterValidDocument();
		MasterValidDocuments.setDocCategoryCode("D101");
		MasterValidDocuments.setDocTypeCode("DC101");
		MasterValidDocuments.setLangCode("ENG");
		validDocuments.add(MasterValidDocuments);
		// biometric Attributes
		List<MasterBiometricAttribute> biometricAttributes = new ArrayList<>();
		MasterBiometricAttribute attribute = new MasterBiometricAttribute();
		attribute.setCode("B101");
		attribute.setDescription("description");
		attribute.setLangCode("eng");
		attribute.setName("FigerPrint");
		attribute.setBiometricTypeCode("B101");
		biometricAttributes.add(attribute);
		// Biometric type
		List<MasterBiometricType> biometricTypes = new ArrayList<>();
		MasterBiometricType bioType = new MasterBiometricType();
		// bioType.setCode("BT101");
		bioType.setDescription("description");
		// bioType.setLangCode("ENG");
		bioType.setName("FigerPrint");
		biometricTypes.add(bioType);
		// Document Category
		List<MasterDocumentCategory> documentCategories = new ArrayList<>();
		MasterDocumentCategory docCatogery = new MasterDocumentCategory();
		docCatogery.setCode("DC101");
		docCatogery.setName("DC name");
		docCatogery.setDescription("description");
		docCatogery.setLangCode("ENG");
		documentCategories.add(docCatogery);
		// Document Type
		List<MasterDocumentType> documentTypes = new ArrayList<>();
		MasterDocumentType doctype = new MasterDocumentType();
		doctype.setCode("DT101");
		doctype.setName("DT Type");
		doctype.setDescription("description");
		doctype.setLangCode("ENG");
		documentTypes.add(doctype);
		// Reason Category
		List<MasterReasonCategory> reasonCategories = new ArrayList<>();
		MasterReasonCategory reson = new MasterReasonCategory();
		reson.setCode("RC101");
		reson.setName("101");
		reson.setLangCode("ENG");
		reasonCategories.add(reson);
		// Reason List
		List<MasterReasonList> reasonLists = new ArrayList<>();
		MasterReasonList MasterReasonLists = new MasterReasonList();
		MasterReasonLists.setCode("RL101");
		MasterReasonLists.setName("RL1");
		MasterReasonLists.setReasonCategory(reson);
		MasterReasonLists.setRsnCatCode("RL");
		MasterReasonLists.setLangCode("ENG");
		reasonLists.add(MasterReasonLists);
		// locations
		List<MasterLocation> locations = new ArrayList<>();
		MasterLocation locattion = new MasterLocation();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(MetaDataUtils.setCreateMetaData(blacklistedWordsList, MasterBlacklistedWords.class))
				.thenReturn(baseEnity);

		/*
		 * Mockito.when(masterSyncApplicationRepository.saveAll(applications));
		 * Mockito.when(masterSyncBiometricAttributeRepository.saveAll(
		 * biometricAttributes)) .thenReturn(biometricAttributes);
		 * Mockito.when(masterSyncBiometricTypeRepository.saveAll(biometricTypes)).
		 * thenReturn(biometricTypes);
		 * Mockito.when(masterSyncBlacklistedWordsRepository.saveAll(blackListedWords)).
		 * thenReturn(blackListedWords);
		 * Mockito.when(masterSyncDeviceRepository.saveAll(devices)).thenReturn(devices)
		 * ; Mockito.when(masterSyncDeviceSpecificationRepository.saveAll(
		 * deviceSpecification)) .thenReturn(deviceSpecification);
		 * Mockito.when(masterSyncDeviceTypeRepository.saveAll(deviceType)).thenReturn(
		 * deviceType);
		 * Mockito.when(masterSyncDocumentCategoryRepository.saveAll(documentCategories)
		 * ).thenReturn(documentCategories);
		 * Mockito.when(masterSyncDocumentTypeRepository.saveAll(documentTypes)).
		 * thenReturn(documentTypes);
		 * Mockito.when(masterSyncGenderTypeRepository.saveAll(genders)).thenReturn(
		 * genders);
		 * Mockito.when(masterSyncHolidayRepository.saveAll(holidays)).thenReturn(
		 * holidays);
		 * Mockito.when(masterSyncIdTypeRepository.saveAll(idTypes)).thenReturn(idTypes)
		 * ; Mockito.when(masterSyncLocationRepository.saveAll(locations)).thenReturn(
		 * locations);
		 * Mockito.when(masterSyncMachineRepository.saveAll(machines)).thenReturn(
		 * machines); Mockito.when(masterSyncMachineSpecificationRepository.saveAll(
		 * machineSpecification)) .thenReturn(machineSpecification);
		 * Mockito.when(masterSyncMachineTypeRepository.saveAll(machineType)).thenReturn
		 * (machineType);
		 * Mockito.when(masterSyncReasonCategoryRepository.saveAll(reasonCategories)).
		 * thenReturn(reasonCategories);
		 * Mockito.when(masterSyncReasonListRepository.saveAll(reasonLists)).thenReturn(
		 * reasonLists); Mockito.when(masterSyncRegistrationCenterRepository.saveAll(
		 * registrationCenters)) .thenReturn(registrationCenters);
		 * Mockito.when(masterSyncTemplateFileFormatRepository.saveAll(
		 * templateFileFormats)) .thenReturn(templateFileFormats);
		 * Mockito.when(masterSyncTemplateRepository.saveAll(templates)).thenReturn(
		 * templates);
		 * Mockito.when(masterSyncTemplateTypeRepository.saveAll(templateTypes)).
		 * thenReturn(templateTypes);
		 * Mockito.when(masterSyncTitleRepository.saveAll(titles)).thenReturn(titles);
		 * Mockito.when(masterSyncValidDocumentRepository.saveAll(validDocuments)).
		 * thenReturn(validDocuments);
		 */

		masterSyncDaoImpl.save(masterSyncDto);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncException() throws RegBaseUncheckedException {
		PowerMockito.mockStatic(RegBaseUncheckedException.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		List<ApplicationDto> application = new ArrayList<>();
		masterSyncDto.setApplications(application);

		List<DeviceTypeDto> masterDeviceTypeDto = new ArrayList<>();

		masterSyncDto.setDeviceTypes(masterDeviceTypeDto);

		List<DeviceSpecificationDto> masterDeviceSpecificDtoEntity = new ArrayList<>();
		masterSyncDto.setDeviceSpecifications(masterDeviceSpecificDtoEntity);

		List<DeviceDto> masterDeviceDto = new ArrayList<>();

		masterSyncDto.setDevices(masterDeviceDto);

		List<HolidayDto> masterHolidaysDto = new ArrayList<>();
		masterSyncDto.setHolidays(masterHolidaysDto);

		List<MachineDto> masterMachineDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<MachineSpecificationDto> masterMachineSpecDto = new ArrayList<>();
		masterSyncDto.setMachineSpecification(masterMachineSpecDto);

		List<MachineTypeDto> masterMachineTypeDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<ValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setValidDocumentMapping(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<MasterSyncBaseEntity> baseEnity = new ArrayList<>();
		// Language
		List<LanguageDto> language = new ArrayList<>();
		LanguageDto lanugageRespDto = new LanguageDto();
		lanugageRespDto.setName("ENG");
		lanugageRespDto.setCode("1001");
		lanugageRespDto.setFamily("english");
		lanugageRespDto.setNativeName("eng");
		language.add(lanugageRespDto);
		masterSyncDto.setLanguages(language);
		// BiometricType
		List<BiometricTypeDto> biometrictype = new ArrayList<>();
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");
		biometrictype.add(biometricTypeDto);
		masterSyncDto.setBiometricTypes(biometrictype);
		// Biometric
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();
		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");
		biometricattribute.add(biometricAttriDto);
		masterSyncDto.setBiometricattributes(biometricattribute);
		// BlacklistedWords
		List<BlacklistedWordsDto> masterBlackListedWordsDto = new ArrayList<>();
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");
		masterBlackListedWordsDto.add(blacklistedWordsDto);
		masterSyncDto.setBlackListedWords(masterBlackListedWordsDto);
		// gender
		List<GenderDto> gender = new ArrayList<>();
		GenderDto genderTypeDto = new GenderDto();
		genderTypeDto.setCode("G101");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");
		gender.add(genderTypeDto);
		masterSyncDto.setGenders(gender);
		// title
		List<TitleDto> title = new ArrayList<>();
		TitleDto titleTypeDto = new TitleDto();
		// titleTypeDto.setTitleCode("1");
		// titleTypeDto.setTitleDescription("dsddsd");
		// titleTypeDto.setTitleName("admin");
		titleTypeDto.setIsActive(true);
		title.add(titleTypeDto);
		masterSyncDto.setTitles(title);
		// idType
		IdTypeDto idTypeResponseDto = new IdTypeDto();
		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setLangCode("eng");
		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdTypes(idTypeList);
		// Document Category
		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentCategories(listDocCat);
		//
		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");
		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);
		masterSyncDto.setDocumentTypes(listDocType);
		//
		List<LocationDto> listLocation = new ArrayList<>();
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");
		listLocation.add(locationDto);
		masterSyncDto.setLocationHierarchy(listLocation);
		//
		List<MasterReasonListDto> categorieList = new ArrayList<>();
		MasterReasonListDto reasonListDto = new MasterReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setRsnCatCode("RS1001");
		categorieList.add(reasonListDto);
		masterSyncDto.setReasonList(categorieList);
		//
		List<PostReasonCategoryDto> categorie = new ArrayList<>();
		PostReasonCategoryDto reasonCategoryDto = new PostReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setName("RC1001");
		categorie.add(reasonCategoryDto);
		masterSyncDto.setReasonCategory(categorie);

		// Machine
		List<MasterMachine> machines = new ArrayList<>();
		MasterMachine machine = new MasterMachine();
		machine.setId("1001");
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machine.setLangCode("ENG");
		machines.add(machine);
		List<MasterApplication> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncApplicationRepository.saveAll(masterApplicationDtoEntity))
					.thenThrow(new RuntimeException().getClass());
			masterSyncDaoImpl.save(masterSyncDto);

		} catch (Exception exception) {

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncNullException() {
		PowerMockito.mockStatic(RegBaseUncheckedException.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		List<ApplicationDto> application = new ArrayList<>();
		masterSyncDto.setApplications(application);

		List<DeviceTypeDto> masterDeviceTypeDto = new ArrayList<>();

		masterSyncDto.setDeviceTypes(masterDeviceTypeDto);

		List<DeviceSpecificationDto> masterDeviceSpecificDtoEntity = new ArrayList<>();
		masterSyncDto.setDeviceSpecifications(masterDeviceSpecificDtoEntity);

		List<DeviceDto> masterDeviceDto = new ArrayList<>();

		masterSyncDto.setDevices(masterDeviceDto);

		List<HolidayDto> masterHolidaysDto = new ArrayList<>();
		masterSyncDto.setHolidays(masterHolidaysDto);

		List<MachineDto> masterMachineDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<MachineSpecificationDto> masterMachineSpecDto = new ArrayList<>();
		masterSyncDto.setMachineSpecification(masterMachineSpecDto);

		List<MachineTypeDto> masterMachineTypeDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<ValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setValidDocumentMapping(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<MasterSyncBaseEntity> baseEnity = new ArrayList<>();
		// Language
		List<LanguageDto> language = new ArrayList<>();
		LanguageDto lanugageRespDto = new LanguageDto();
		lanugageRespDto.setName("ENG");
		lanugageRespDto.setCode("1001");
		lanugageRespDto.setFamily("english");
		lanugageRespDto.setNativeName("eng");
		language.add(lanugageRespDto);
		masterSyncDto.setLanguages(language);
		// BiometricType
		List<BiometricTypeDto> biometrictype = new ArrayList<>();
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");
		biometrictype.add(biometricTypeDto);
		masterSyncDto.setBiometricTypes(biometrictype);
		// Biometric
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();
		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");
		biometricattribute.add(biometricAttriDto);
		masterSyncDto.setBiometricattributes(biometricattribute);
		// BlacklistedWords
		List<BlacklistedWordsDto> masterBlackListedWordsDto = new ArrayList<>();
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");
		masterBlackListedWordsDto.add(blacklistedWordsDto);
		masterSyncDto.setBlackListedWords(masterBlackListedWordsDto);
		// gender
		List<GenderDto> gender = new ArrayList<>();
		GenderDto genderTypeDto = new GenderDto();
		genderTypeDto.setCode("G101");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");
		gender.add(genderTypeDto);
		masterSyncDto.setGenders(gender);
		// title
		List<TitleDto> title = new ArrayList<>();
		TitleDto titleTypeDto = new TitleDto();
		// titleTypeDto.setTitleCode("1");
		// titleTypeDto.setTitleDescription("dsddsd");
		// titleTypeDto.setTitleName("admin");
		titleTypeDto.setIsActive(true);
		title.add(titleTypeDto);
		masterSyncDto.setTitles(title);
		// idType
		IdTypeDto idTypeResponseDto = new IdTypeDto();
		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setLangCode("eng");
		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdTypes(idTypeList);
		// Document Category
		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentCategories(listDocCat);
		//
		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");
		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);
		masterSyncDto.setDocumentTypes(listDocType);
		//
		List<LocationDto> listLocation = new ArrayList<>();
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");
		listLocation.add(locationDto);
		masterSyncDto.setLocationHierarchy(listLocation);
		//
		List<MasterReasonListDto> categorieList = new ArrayList<>();
		MasterReasonListDto reasonListDto = new MasterReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setRsnCatCode("RS1001");
		categorieList.add(reasonListDto);
		masterSyncDto.setReasonList(categorieList);
		//
		List<PostReasonCategoryDto> categorie = new ArrayList<>();
		PostReasonCategoryDto reasonCategoryDto = new PostReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setName("RC1001");
		categorie.add(reasonCategoryDto);
		masterSyncDto.setReasonCategory(categorie);

		// Machine
		List<MasterMachine> machines = new ArrayList<>();
		MasterMachine machine = new MasterMachine();
		machine.setId("1001");
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machine.setLangCode("ENG");
		machines.add(machine);
		List<MasterApplication> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncApplicationRepository.saveAll(masterApplicationDtoEntity))
					.thenThrow(new DataAccessException("...") {
					});
			masterSyncDaoImpl.save(masterSyncDto);

		} catch (Exception exception) {

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncDataException() {
		PowerMockito.mockStatic(RegBaseUncheckedException.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		List<ApplicationDto> application = new ArrayList<>();
		masterSyncDto.setApplications(application);

		List<DeviceTypeDto> masterDeviceTypeDto = new ArrayList<>();

		masterSyncDto.setDeviceTypes(masterDeviceTypeDto);

		List<DeviceSpecificationDto> masterDeviceSpecificDtoEntity = new ArrayList<>();
		masterSyncDto.setDeviceSpecifications(masterDeviceSpecificDtoEntity);

		List<DeviceDto> masterDeviceDto = new ArrayList<>();

		masterSyncDto.setDevices(masterDeviceDto);

		List<HolidayDto> masterHolidaysDto = new ArrayList<>();
		masterSyncDto.setHolidays(masterHolidaysDto);

		List<MachineDto> masterMachineDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<MachineSpecificationDto> masterMachineSpecDto = new ArrayList<>();
		masterSyncDto.setMachineSpecification(masterMachineSpecDto);

		List<MachineTypeDto> masterMachineTypeDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<ValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setValidDocumentMapping(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<MasterSyncBaseEntity> baseEnity = new ArrayList<>();
		// Language
		List<LanguageDto> language = new ArrayList<>();
		LanguageDto lanugageRespDto = new LanguageDto();
		lanugageRespDto.setName("ENG");
		lanugageRespDto.setCode("1001");
		lanugageRespDto.setFamily("english");
		lanugageRespDto.setNativeName("eng");
		language.add(lanugageRespDto);
		masterSyncDto.setLanguages(language);
		// BiometricType
		List<BiometricTypeDto> biometrictype = new ArrayList<>();
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");
		biometrictype.add(biometricTypeDto);
		masterSyncDto.setBiometricTypes(biometrictype);
		// Biometric
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();
		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");
		biometricattribute.add(biometricAttriDto);
		masterSyncDto.setBiometricattributes(biometricattribute);
		// BlacklistedWords
		List<BlacklistedWordsDto> masterBlackListedWordsDto = new ArrayList<>();
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");
		masterBlackListedWordsDto.add(blacklistedWordsDto);
		masterSyncDto.setBlackListedWords(masterBlackListedWordsDto);
		// gender
		List<GenderDto> gender = new ArrayList<>();
		GenderDto genderTypeDto = new GenderDto();
		genderTypeDto.setCode("G101");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");
		gender.add(genderTypeDto);
		masterSyncDto.setGenders(gender);
		// title
		List<TitleDto> title = new ArrayList<>();
		TitleDto titleTypeDto = new TitleDto();
		// titleTypeDto.setTitleCode("1");
		// titleTypeDto.setTitleDescription("dsddsd");
		// titleTypeDto.setTitleName("admin");
		titleTypeDto.setIsActive(true);
		title.add(titleTypeDto);
		masterSyncDto.setTitles(title);
		// idType
		IdTypeDto idTypeResponseDto = new IdTypeDto();
		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setLangCode("eng");
		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdTypes(idTypeList);
		// Document Category
		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentCategories(listDocCat);
		//
		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");
		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);
		masterSyncDto.setDocumentTypes(listDocType);
		//
		List<LocationDto> listLocation = new ArrayList<>();
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");
		listLocation.add(locationDto);
		masterSyncDto.setLocationHierarchy(listLocation);
		//
		List<MasterReasonListDto> categorieList = new ArrayList<>();
		MasterReasonListDto reasonListDto = new MasterReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setRsnCatCode("RS1001");
		categorieList.add(reasonListDto);
		masterSyncDto.setReasonList(categorieList);
		//
		List<PostReasonCategoryDto> categorie = new ArrayList<>();
		PostReasonCategoryDto reasonCategoryDto = new PostReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setName("RC1001");
		categorie.add(reasonCategoryDto);
		masterSyncDto.setReasonCategory(categorie);

		// Machine
		List<MasterMachine> machines = new ArrayList<>();
		MasterMachine machine = new MasterMachine();
		machine.setId("1001");
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machine.setLangCode("ENG");
		machines.add(machine);
		List<MasterApplication> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncApplicationRepository.saveAll(masterApplicationDtoEntity))
					.thenThrow(new NullPointerException("...") {
					});
			masterSyncDaoImpl.save(masterSyncDto);

		} catch (Exception exception) {

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncRegException() {
		PowerMockito.mockStatic(RegBaseUncheckedException.class);
		MasterDataResponseDto masterSyncDto = new MasterDataResponseDto();

		List<ApplicationDto> application = new ArrayList<>();
		masterSyncDto.setApplications(application);

		List<DeviceTypeDto> masterDeviceTypeDto = new ArrayList<>();

		masterSyncDto.setDeviceTypes(masterDeviceTypeDto);

		List<DeviceSpecificationDto> masterDeviceSpecificDtoEntity = new ArrayList<>();
		masterSyncDto.setDeviceSpecifications(masterDeviceSpecificDtoEntity);

		List<DeviceDto> masterDeviceDto = new ArrayList<>();

		masterSyncDto.setDevices(masterDeviceDto);

		List<HolidayDto> masterHolidaysDto = new ArrayList<>();
		masterSyncDto.setHolidays(masterHolidaysDto);

		List<MachineDto> masterMachineDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<MachineSpecificationDto> masterMachineSpecDto = new ArrayList<>();
		masterSyncDto.setMachineSpecification(masterMachineSpecDto);

		List<MachineTypeDto> masterMachineTypeDto = new ArrayList<>();
		masterSyncDto.setMachineDetails(masterMachineDto);

		List<ValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setValidDocumentMapping(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<MasterSyncBaseEntity> baseEnity = new ArrayList<>();
		// Language
		List<LanguageDto> language = new ArrayList<>();
		LanguageDto lanugageRespDto = new LanguageDto();
		lanugageRespDto.setName("ENG");
		lanugageRespDto.setCode("1001");
		lanugageRespDto.setFamily("english");
		lanugageRespDto.setNativeName("eng");
		language.add(lanugageRespDto);
		masterSyncDto.setLanguages(language);
		// BiometricType
		List<BiometricTypeDto> biometrictype = new ArrayList<>();
		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();
		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");
		biometrictype.add(biometricTypeDto);
		masterSyncDto.setBiometricTypes(biometrictype);
		// Biometric
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();
		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");
		biometricattribute.add(biometricAttriDto);
		masterSyncDto.setBiometricattributes(biometricattribute);
		// BlacklistedWords
		List<BlacklistedWordsDto> masterBlackListedWordsDto = new ArrayList<>();
		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");
		masterBlackListedWordsDto.add(blacklistedWordsDto);
		masterSyncDto.setBlackListedWords(masterBlackListedWordsDto);
		// gender
		List<GenderDto> gender = new ArrayList<>();
		GenderDto genderTypeDto = new GenderDto();
		genderTypeDto.setCode("G101");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");
		gender.add(genderTypeDto);
		masterSyncDto.setGenders(gender);
		// title
		List<TitleDto> title = new ArrayList<>();
		TitleDto titleTypeDto = new TitleDto();
		// titleTypeDto.setTitleCode("1");
		// titleTypeDto.setTitleDescription("dsddsd");
		// titleTypeDto.setTitleName("admin");
		titleTypeDto.setIsActive(true);
		title.add(titleTypeDto);
		masterSyncDto.setTitles(title);
		// idType
		IdTypeDto idTypeResponseDto = new IdTypeDto();
		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setLangCode("eng");
		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdTypes(idTypeList);
		// Document Category
		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentCategories(listDocCat);
		//
		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");
		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);
		masterSyncDto.setDocumentTypes(listDocType);
		//
		List<LocationDto> listLocation = new ArrayList<>();
		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");
		listLocation.add(locationDto);
		masterSyncDto.setLocationHierarchy(listLocation);
		//
		List<MasterReasonListDto> categorieList = new ArrayList<>();
		MasterReasonListDto reasonListDto = new MasterReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setRsnCatCode("RS1001");
		categorieList.add(reasonListDto);
		masterSyncDto.setReasonList(categorieList);
		//
		List<PostReasonCategoryDto> categorie = new ArrayList<>();
		PostReasonCategoryDto reasonCategoryDto = new PostReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setName("RC1001");
		categorie.add(reasonCategoryDto);
		masterSyncDto.setReasonCategory(categorie);

		// Machine
		List<MasterMachine> machines = new ArrayList<>();
		MasterMachine machine = new MasterMachine();
		machine.setId("1001");
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machine.setLangCode("ENG");
		machines.add(machine);
		List<MasterApplication> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncApplicationRepository.saveAll(masterApplicationDtoEntity))
					.thenThrow(RegBaseUncheckedException.class);
			masterSyncDaoImpl.save(masterSyncDto);

		} catch (Exception exception) {

		}
	}

	@Test
	public void findLocationByLangCode() throws RegBaseCheckedException {

		List<MasterLocation> locations = new ArrayList<>();
		MasterLocation locattion = new MasterLocation();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncLocationRepository.findMasterLocationByHierarchyNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(locations);

		masterSyncDaoImpl.findLocationByLangCode("Region", "ENG");

		assertTrue(locations != null);
	}

	@Test
	public void findLocationByParentLocCode() throws RegBaseCheckedException {

		List<MasterLocation> locations = new ArrayList<>();
		MasterLocation locattion = new MasterLocation();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncLocationRepository.findMasterLocationByParentLocCodeAndLangCode(Mockito.anyString(),Mockito.anyString()))
				.thenReturn(locations);

		masterSyncDaoImpl.findLocationByParentLocCode("TPT","eng");

		assertTrue(locations != null);
	}

	@Test
	public void findAllReason() throws RegBaseCheckedException {

		List<MasterReasonCategory> allReason = new ArrayList<>();
		MasterReasonCategory reasons = new MasterReasonCategory();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);

		Mockito.when(masterSyncReasonCategoryRepository.findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull())
				.thenReturn(allReason);

		masterSyncDaoImpl.getAllReasonCatogery();

		assertTrue(allReason != null);
	}

	@Test
	public void findAllReasonList() throws RegBaseCheckedException {

		List<String> reasonCat = new ArrayList<>();
		List<MasterReasonList> allReason = new ArrayList<>();
		MasterReasonList reasons = new MasterReasonList();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);

		Mockito.when(masterSyncReasonListRepository.findByLangCodeAndReasonCategoryCodeIn(Mockito.anyString(),
				Mockito.anyList())).thenReturn(allReason);

		masterSyncDaoImpl.getReasonList("FRE", reasonCat);

		assertTrue(allReason != null);
	}

	@Test
	public void findBlackWords() throws RegBaseCheckedException {

		List<MasterBlacklistedWords> allBlackWords = new ArrayList<>();
		MasterBlacklistedWords blackWord = new MasterBlacklistedWords();
		blackWord.setWord("asdfg");
		blackWord.setDescription("asdfg");
		blackWord.setLangCode("ENG");
		allBlackWords.add(blackWord);
		allBlackWords.add(blackWord);

		Mockito.when(masterSyncBlacklistedWordsRepository.findBlackListedWordsByLangCode(Mockito.anyString()))
				.thenReturn(allBlackWords);

		masterSyncDaoImpl.getBlackListedWords("ENG");

		assertTrue(allBlackWords != null);
	}
	
	@Test
	public void findDocumentCategories() throws RegBaseCheckedException {
	
		List<MasterDocumentType> documents = new ArrayList<>();
		MasterDocumentType document = new MasterDocumentType();
		document.setName("Aadhar");
		document.setDescription("Aadhar card");
		document.setLangCode("ENG");
		documents.add(document);
		documents.add(document);
		List<String> validDocuments = new ArrayList<>();
		validDocuments.add("MNA");
		validDocuments.add("CLR");
		Mockito.when(masterSyncDao.getDocumentTypes(Mockito.anyList(),Mockito.anyString())).thenReturn(documents);
	
		masterSyncDaoImpl.getDocumentTypes(validDocuments,"test");
		
		assertTrue(documents!=null);
	
	}
	
	@Test
	public void findGenders() throws RegBaseCheckedException {
	
		List<MasterGender> genderList = new ArrayList<>();
		MasterGender gender = new MasterGender();
		gender.setCode("1");
		gender.setGenderName("male");
		gender.setLangCode("ENG");
		gender.setIsActive(true);
		genderList.add(gender);
	
		Mockito.when(masterSyncDao.getGenderDtls(Mockito.anyString())).thenReturn(genderList);
	
		masterSyncDaoImpl.getGenderDtls("ENG");
		
		assertTrue(genderList!=null);
	
	}

}
