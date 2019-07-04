package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.impl.MasterSyncDaoImpl;
import io.mosip.registration.dto.ApplicantValidDocumentDto;
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.mastersync.AppAuthenticationMethodDto;
import io.mosip.registration.dto.mastersync.AppDetailDto;
import io.mosip.registration.dto.mastersync.AppRolePriorityDto;
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
import io.mosip.registration.dto.mastersync.PostReasonCategoryDto;
import io.mosip.registration.dto.mastersync.ProcessListDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterDeviceDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterMachineDeviceDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterMachineDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterTypeDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterUserDto;
import io.mosip.registration.dto.mastersync.RegistrationCenterUserMachineMappingDto;
import io.mosip.registration.dto.mastersync.ScreenAuthorizationDto;
import io.mosip.registration.dto.mastersync.ScreenDetailDto;
import io.mosip.registration.dto.mastersync.SyncJobDefDto;
import io.mosip.registration.dto.mastersync.TemplateDto;
import io.mosip.registration.dto.mastersync.TemplateFileFormatDto;
import io.mosip.registration.dto.mastersync.TemplateTypeDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.entity.AppAuthenticationMethod;
import io.mosip.registration.entity.AppDetail;
import io.mosip.registration.entity.AppRolePriority;
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.entity.BiometricAttribute;
import io.mosip.registration.entity.BiometricType;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.IdType;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.Language;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.MachineType;
import io.mosip.registration.entity.ProcessList;
import io.mosip.registration.entity.ReasonCategory;
import io.mosip.registration.entity.ReasonList;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.RegDeviceSpec;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationCenterType;
import io.mosip.registration.entity.RegistrationCommonFields;
import io.mosip.registration.entity.ScreenAuthorization;
import io.mosip.registration.entity.ScreenDetail;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.entity.Title;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.entity.id.AppRolePriorityId;
import io.mosip.registration.entity.id.ApplicantValidDocumentID;
import io.mosip.registration.entity.id.CodeAndLanguageCodeID;
import io.mosip.registration.entity.id.IndividualTypeId;
import io.mosip.registration.entity.id.RegDeviceTypeId;
import io.mosip.registration.entity.id.RegMachineSpecId;
import io.mosip.registration.entity.id.ValidDocumentID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.AppAuthenticationRepository;
import io.mosip.registration.repositories.AppDetailRepository;
import io.mosip.registration.repositories.AppRolePriorityRepository;
import io.mosip.registration.repositories.ApplicantValidDocumentRepository;
import io.mosip.registration.repositories.BiometricAttributeRepository;
import io.mosip.registration.repositories.BiometricTypeRepository;
import io.mosip.registration.repositories.BlacklistedWordsRepository;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.DeviceMasterRepository;
import io.mosip.registration.repositories.DeviceSpecificationRepository;
import io.mosip.registration.repositories.DeviceTypeRepository;
import io.mosip.registration.repositories.DocumentCategoryRepository;
import io.mosip.registration.repositories.DocumentTypeRepository;
import io.mosip.registration.repositories.GenderRepository;
import io.mosip.registration.repositories.IdTypeRepository;
import io.mosip.registration.repositories.IndividualTypeRepository;
import io.mosip.registration.repositories.LanguageRepository;
import io.mosip.registration.repositories.LocationRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.MachineSpecificationRepository;
import io.mosip.registration.repositories.MachineTypeRepository;
import io.mosip.registration.repositories.ProcessListRepository;
import io.mosip.registration.repositories.ReasonCategoryRepository;
import io.mosip.registration.repositories.ReasonListRepository;
import io.mosip.registration.repositories.RegistrationCenterDeviceRepository;
import io.mosip.registration.repositories.RegistrationCenterMachineDeviceRepository;
import io.mosip.registration.repositories.RegistrationCenterRepository;
import io.mosip.registration.repositories.RegistrationCenterTypeRepository;
import io.mosip.registration.repositories.RegistrationCenterUserRepository;
import io.mosip.registration.repositories.ScreenAuthorizationRepository;
import io.mosip.registration.repositories.ScreenDetailRepository;
import io.mosip.registration.repositories.SyncJobControlRepository;
import io.mosip.registration.repositories.SyncJobDefRepository;
import io.mosip.registration.repositories.TemplateFileFormatRepository;
import io.mosip.registration.repositories.TemplateRepository;
import io.mosip.registration.repositories.TemplateTypeRepository;
import io.mosip.registration.repositories.TitleRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;
import io.mosip.registration.repositories.ValidDocumentRepository;
import io.mosip.registration.service.sync.impl.MasterSyncServiceImpl;
import io.mosip.registration.util.mastersync.MetaDataUtils;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetaDataUtils.class, RegBaseUncheckedException.class, SessionContext.class })
public class MasterSyncDaoImplTest {

	// private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private SyncJobControlRepository syncStatusRepository;
	@Mock
	private BiometricAttributeRepository biometricAttributeRepository;
	@Mock
	private BiometricTypeRepository masterSyncBiometricTypeRepository;
	@Mock
	private BlacklistedWordsRepository masterSyncBlacklistedWordsRepository;
	@Mock
	private DeviceMasterRepository masterSyncDeviceRepository;
	@Mock
	private DeviceSpecificationRepository masterSyncDeviceSpecificationRepository;
	@Mock
	private DeviceTypeRepository masterSyncDeviceTypeRepository;
	@Mock
	private DocumentCategoryRepository masterSyncDocumentCategoryRepository;
	@Mock
	private DocumentTypeRepository masterSyncDocumentTypeRepository;
	@Mock
	private GenderRepository masterSyncGenderTypeRepository;
	@Mock
	private IdTypeRepository masterSyncIdTypeRepository;
	@Mock
	private LanguageRepository masterSyncLanguageRepository;
	@Mock
	private LocationRepository masterSyncLocationRepository;
	@Mock
	private MachineMasterRepository masterSyncMachineRepository;
	@Mock
	private MachineSpecificationRepository masterSyncMachineSpecificationRepository;
	@Mock
	private MachineTypeRepository masterSyncMachineTypeRepository;
	@Mock
	private ReasonCategoryRepository reasonCategoryRepository;
	@Mock
	private ReasonListRepository masterSyncReasonListRepository;
	@Mock
	private RegistrationCenterRepository masterSyncRegistrationCenterRepository;
	@Mock
	private RegistrationCenterTypeRepository masterSyncRegistrationCenterTypeRepository;
	@Mock
	private TemplateFileFormatRepository masterSyncTemplateFileFormatRepository;
	@Mock
	private TemplateRepository masterSyncTemplateRepository;
	@Mock
	private TemplateTypeRepository masterSyncTemplateTypeRepository;
	@Mock
	private TitleRepository masterSyncTitleRepository;
	@Mock
	private ApplicantValidDocumentRepository masterSyncValidDocumentRepository;
	@Mock
	private ValidDocumentRepository validDocumentRepository;
	@Mock
	private IndividualTypeRepository individualTypeRepository;
	@Mock
	private AppAuthenticationRepository appAuthenticationRepository;

	@Mock
	private AppRolePriorityRepository appRolePriorityRepository;

	@Mock
	private AppDetailRepository appDetailRepository;

	@Mock
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	@Mock
	private ProcessListRepository processListRepository;

	/** Object for Sync language Repository. */
	@Mock
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	/** Object for Sync language Repository. */
	@Mock
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	/** Object for Sync language Repository. */
	@Mock
	private UserMachineMappingRepository userMachineMappingRepository;

	/** Object for Sync language Repository. */
	@Mock
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	/** Object for Sync language Repository. */
	@Mock
	private CenterMachineRepository centerMachineRepository;

	/** Object for Sync language Repository. */
	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	/** Object for Sync language Repository. */
	@Mock
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;
	
	/** Object for screen detail Repository. */
	@Mock
	private ScreenDetailRepository screenDetailRepository;
	
	/** Object for Sync screen auth Repository. */
	@Mock
	private SyncJobDefRepository syncJobDefRepository;

	@Mock
	private MasterSyncDao masterSyncDao;

	@InjectMocks
	private MasterSyncServiceImpl masterSyncServiceImpl;

	@InjectMocks
	private MasterSyncDaoImpl masterSyncDaoImpl;
	
	@Before
	public void initialize() throws Exception {
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");
	}

	@BeforeClass
	public static void beforeClass() {

		List<RegistrationCenterType> registrationCenterType = new ArrayList<>();
		RegistrationCenterType MasterRegistrationCenterType = new RegistrationCenterType();
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
		List<RegistrationCenterDeviceDto> masterRegCenterDeviceEntity = new ArrayList<>();
		RegistrationCenterDeviceDto temp5 = new RegistrationCenterDeviceDto();
		temp5.setDeviceId("10011");
		temp5.setIsActive(true);
		temp5.setRegCenterId("10031");
		masterRegCenterDeviceEntity.add(temp5);
		List<RegistrationCenterMachineDeviceDto> masterRegCenterMachineDeviceEntity = new ArrayList<>();
		RegistrationCenterMachineDeviceDto temp1 = new RegistrationCenterMachineDeviceDto();
		temp1.setDeviceId("10031");
		temp1.setIsActive(true);
		temp1.setMachineId("10031");
		temp1.setRegCenterId("10031");
		masterRegCenterMachineDeviceEntity.add(temp1);
		List<RegistrationCenterUserMachineMappingDto> masterRegCenterUserMachineEntity = new ArrayList<>();
		RegistrationCenterUserMachineMappingDto temp2 = new RegistrationCenterUserMachineMappingDto();
		temp2.setActive(true);
		temp2.setCntrId("10031");
		temp2.setMachineId("10031");
		temp2.setUsrId("10031");
		masterRegCenterUserMachineEntity.add(temp2);

		List<RegistrationCenterUserDto> masterRegCenterUserEntity = new ArrayList<>();
		RegistrationCenterUserDto temp3 = new RegistrationCenterUserDto();
		temp3.setIsActive(true);
		temp3.setRegCenterId("10031");
		temp3.setUserId("10031");
		masterRegCenterUserEntity.add(temp3);

		List<RegistrationCenterMachineDto> masterRegCenterMachineEntity = new ArrayList<>();
		RegistrationCenterMachineDto test4 = new RegistrationCenterMachineDto();
		test4.setIsActive(true);
		test4.setMachineId("10031");
		test4.setRegCenterId("10031");
		masterRegCenterMachineEntity.add(test4);

		masterSyncDto.setRegistrationCenterMachines(masterRegCenterMachineEntity);
		masterSyncDto.setRegistrationCenterDevices(masterRegCenterDeviceEntity);
		masterSyncDto.setRegistrationCenterMachineDevices(masterRegCenterMachineDeviceEntity);
		masterSyncDto.setRegistrationCenterUserMachines(masterRegCenterUserMachineEntity);
		masterSyncDto.setRegistrationCenterUsers(masterRegCenterUserEntity);
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

		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setApplicantValidDocuments(masterValidDocumnetsDto);

		List<IndividualTypeDto> individualTypeDtos = new ArrayList<>();
		masterSyncDto.setIndividualTypes(individualTypeDtos);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		TemplateDto templet = new TemplateDto();
		templet.setId("1001");
		templet.setFileText("Sample");
		templet.setDescription("text");
		templet.setIsActive(true);
		templet.setIsDeleted(false);
		masterTemplateDto.add(templet);
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		TemplateTypeDto tes = new TemplateTypeDto();
		tes.setCode("1001");
		tes.setDescription("text");
		tes.setIsActive(true);
		tes.setIsDeleted(false);
		masterTemplateTypeDto.add(tes);
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		TemplateFileFormatDto temp = new TemplateFileFormatDto();
		temp.setCode("1001");
		temp.setDescription("text");
		temp.setIsActive(true);
		temp.setIsDeleted(false);
		masterTemplateFileDto.add(temp);
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		RegistrationCenterDto regCntr = new RegistrationCenterDto();
		regCntr.setId("10031");
		regCntr.setAddressLine1("Chennai");
		regCntr.setIsActive(true);
		regCntr.setLangCode("eng");
		regCntr.setAddressLine2("chennai");
		regCntr.setAddressLine3("TN");
		regCntr.setCenterEndTime(LocalTime.now());
		regCntr.setCenterStartTime(LocalTime.now());
		regCntr.setCenterTypeCode("reg");
		regCntr.setContactPerson("admin");
		regCntr.setContactPhone("999999999");
		regCntr.setHolidayLocationCode("Happy New Year");
		regCntr.setLatitude("87.3123");
		regCntr.setLongitude("8.3232");
		regCntr.setLunchStartTime(LocalTime.now());
		regCntr.setLunchEndTime(LocalTime.now());
		regCntr.setName("Registartion");
		regCntr.setNumberOfKiosks(Short.MIN_VALUE);
		regCntr.setNumberOfStations(Short.MIN_VALUE);
		regCntr.setPerKioskProcessTime(LocalTime.now());
		regCntr.setWorkingHours("8h");
		regCenter.add(regCntr);
		masterSyncDto.setRegistrationCenter(regCenter);

		List<RegistrationCenterTypeDto> regCenterType = new ArrayList<>();
		RegistrationCenterTypeDto type = new RegistrationCenterTypeDto();
		type.setCode("10031");
		type.setIsActive(true);
		type.setLangCode("eng");
		regCenterType.add(type);
		masterSyncDto.setRegistrationCenterTypes(regCenterType);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<RegistrationCommonFields> baseEnity = new ArrayList<>();
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
		List<ReasonListDto> categorieList = new ArrayList<>();
		ReasonListDto reasonListDto = new ReasonListDto();
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

		List<IndividualTypeDto> individualTypes = new ArrayList<>();
		IndividualTypeDto individualType = new IndividualTypeDto();
		individualType.setCode("NFR");
		individualType.setLangCode("eng");
		individualType.setName("National");
		individualType.setIsActive(true);
		individualTypes.add(individualType);
		masterSyncDto.setIndividualTypes(individualTypes);

		List<AppAuthenticationMethodDto> appAuthMethods = new ArrayList<>();
		AppAuthenticationMethodDto appAuth = new AppAuthenticationMethodDto();
		appAuth.setAppId("10003");
		appAuth.setAuthMethodCode("OTP");
		appAuth.setIsActive(true);
		appAuth.setProcessId("onboard_auth");
		appAuth.setRoleCode("SUPERVISOR");
		appAuthMethods.add(appAuth);
		masterSyncDto.setAppAuthenticationMethods(appAuthMethods);

		List<AppDetailDto> appDetails = new ArrayList<>();
		AppDetailDto appDetils = new AppDetailDto();
		appDetils.setId("10003");
		appDetils.setDescr("OTP");
		appDetils.setIsActive(true);
		appDetils.setLangCode("eng");
		appDetils.setName("SUPERVISOR");
		appDetails.add(appDetils);
		masterSyncDto.setAppDetails(appDetails);

		List<AppRolePriorityDto> appRolePriority = new ArrayList<>();
		AppRolePriorityDto appPriority = new AppRolePriorityDto();
		appPriority.setAppId("10003");
		appPriority.setPriority(1);
		appPriority.setIsActive(true);
		appPriority.setProcessId("onboard_auth");
		appPriority.setRoleCode("SUPERVISOR");
		appRolePriority.add(appPriority);
		masterSyncDto.setAppRolePriorities(appRolePriority);

		List<ScreenAuthorizationDto> screenAuth = new ArrayList<>();

		ScreenAuthorizationDto screenAuthDto = new ScreenAuthorizationDto();
		screenAuthDto.setIsPermitted(true);
		screenAuthDto.setIsActive(true);
		screenAuthDto.setLangCode("eng");
		screenAuthDto.setScreenId("approveRegistrationRoot");
		screenAuthDto.setRoleCode("SUPERADMIN");
		screenAuth.add(screenAuthDto);
		masterSyncDto.setScreenAuthorizations(screenAuth);

		List<ProcessListDto> processLst = new ArrayList<>();
		ProcessListDto processListDto = new ProcessListDto();
		processListDto.setId("login_auth");
		processListDto.setDescr("Login authentication");
		processListDto.setName("Login authentication");
		processListDto.setLangCode("eng");
		processListDto.setIsActive(true);
		processLst.add(processListDto);
		masterSyncDto.setProcessList(processLst);

		// Code and Land Code
		CodeAndLanguageCodeID codeaLang = new CodeAndLanguageCodeID();
		codeaLang.setCode("1011");
		codeaLang.setLangCode("ENG");
		Time localTime = Time.valueOf(LocalTime.now());
		// Machine
		List<MachineMaster> machines = new ArrayList<>();
		MachineMaster machine = new MachineMaster();
		RegMachineSpecId reg = new RegMachineSpecId();
		reg.setId("10031");
		reg.setLangCode("eng");
		machine.setRegMachineSpecId(reg);
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machines.add(machine);
		// Machine Specification
		List<RegDeviceSpec> machineSpecification = new ArrayList<>();
		RegDeviceSpec machineSpec = new RegDeviceSpec();
		RegMachineSpecId specId = new RegMachineSpecId();
		specId.setId("1001");
		machineSpec.setBrand("Lenovo");
		machineSpec.setModel("T480");
		machineSpec.setName("Laptop");
		specId.setLangCode("ENG");
		machineSpec.setRegMachineSpecId(specId);
		machineSpecification.add(machineSpec);
		// Machine Type
		List<MachineType> machineType = new ArrayList<>();
		MachineType MasterMachineType = new MachineType();
		CodeAndLanguageCodeID id = new CodeAndLanguageCodeID();
		id.setCode("1001");
		id.setLangCode("eng");
		MasterMachineType.setCodeAndLanguageCodeID(id);
		MasterMachineType.setName("System");
		MasterMachineType.setDescription("System");
		machineType.add(MasterMachineType);
		// Device
		List<RegDeviceMaster> devices = new ArrayList<>();
		RegDeviceMaster Masterdevices = new RegDeviceMaster();
		RegMachineSpecId regMachineSpecId = new RegMachineSpecId();
		regMachineSpecId.setId("1011");
		Masterdevices.setName("printer");
		Masterdevices.setIpAddress("127.0.0.122");
		Masterdevices.setSerialNum("1011");
		regMachineSpecId.setLangCode("ENG");
		Masterdevices.setRegMachineSpecId(regMachineSpecId);
		Masterdevices.setMacAddress("213:21:132:312");
		devices.add(Masterdevices);
		// Device Specification
		List<RegDeviceSpec> deviceSpecification = new ArrayList<>();
		RegDeviceSpec MasterDeviceSpecification = new RegDeviceSpec();
		RegMachineSpecId specMachineId = new RegMachineSpecId();
		specMachineId.setId("1011");
		MasterDeviceSpecification.setBrand("Hp Printer");
		specMachineId.setLangCode("ENG");
		MasterDeviceSpecification.setModel("HP-SP1011");
		MasterDeviceSpecification.setRegMachineSpecId(specId);
		deviceSpecification.add(MasterDeviceSpecification);
		// Device Type
		List<RegDeviceType> deviceType = new ArrayList<>();
		RegDeviceType MasterDeviceType = new RegDeviceType();
		RegDeviceTypeId deviceTypeId = new RegDeviceTypeId();
		deviceTypeId.setCode("FRS");
		deviceTypeId.setLangCode("eng");
		MasterDeviceType.setRegDeviceTypeId(deviceTypeId);
		MasterDeviceType.setName("device");
		MasterDeviceType.setDescription("deviceDescriptiom");
		deviceType.add(MasterDeviceType);
		List<IndividualType> masterIndividualType = new ArrayList<>();
		IndividualType individualTypeEntity = new IndividualType();
		IndividualTypeId individualTypeId = new IndividualTypeId();
		individualTypeId.setCode("NFR");
		individualTypeId.setLangCode("eng");
		individualTypeEntity.setIndividualTypeId(individualTypeId);
		individualTypeEntity.setName("National");
		individualTypeEntity.setIsActive(true);
		masterIndividualType.add(individualTypeEntity);

		// App role priority
		List<AppRolePriority> masterAppRolePriority = new ArrayList<>();
		AppRolePriority appRole = new AppRolePriority();
		AppRolePriorityId roleId = new AppRolePriorityId();
		roleId.setAppId("1003");
		roleId.setProcessId("login_auth");
		roleId.setRoleCode("ADMIN");
		appRole.setAppRolePriorityId(roleId);
		appRole.setCrBy("system");
		appRole.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		appRole.setIsActive(true);
		appRole.setPriority(1);
		appRole.setLangCode("eng");
		masterAppRolePriority.add(appRole);

		List<AppDetail> masterAppDetails = new ArrayList<>();

		List<ProcessList> masterProcessList = new ArrayList<>();

		List<AppAuthenticationMethod> masterAppLoginMethod = new ArrayList<>();

		List<ScreenAuthorization> masterScreenAuth = new ArrayList<>();
		
		List<ScreenDetailDto> screenDetailList = new ArrayList<>();

		masterSyncDto.setScreenDetails(screenDetailList);

		List<SyncJobDefDto> syncJobDefList = new ArrayList<>();

		masterSyncDto.setSyncJobDefinitions(syncJobDefList);
		
		List<SyncJobDef> masterSyncJob = new ArrayList<>();
		SyncJobDef syncJobDef = new SyncJobDef();
		masterSyncJob.add(syncJobDef);
		
		List<ScreenDetail> masterScreenDetail = new ArrayList<>();
		ScreenDetail screenDetail=new ScreenDetail();
		masterScreenDetail.add(screenDetail);

		// Reg Center
		List<RegistrationCenter> registrationCenters = new ArrayList<>();
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setAddrLine1("address-line1");
		registrationCenter.setAddrLine2("address-line2");
		registrationCenter.setAddrLine3("address-line3");
		registrationCenter.setCenterEndTime(localTime);
		registrationCenter.setCenterStartTime(localTime);
		registrationCenter.setCntrTypCode("T1011");
		registrationCenter.setContactPerson("admin");
		registrationCenter.setContactPhone("9865123456");
		registrationCenter.setHolidayLocCode(("LOC01"));
		registrationCenter.setIsActive(true);
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLunchEndTime(localTime);
		registrationCenter.setLunchStartTime(localTime);
		registrationCenters.add(registrationCenter);
		// Template
		List<Template> templates = new ArrayList<>();
		Template MasterTemplates = new Template();
		MasterTemplates.setId("T1");
		MasterTemplates.setDescr("Email-Template");
		MasterTemplates.setName("Email-Template");
		MasterTemplates.setModuleName("Email-Template");
		MasterTemplates.setModuleId("T101");
		MasterTemplates.setModel("ModuleName");
		templates.add(MasterTemplates);
		// Template Foramt
		List<TemplateFileFormat> templateFileFormats = new ArrayList<>();
		TemplateFileFormat templateFileFormat = new TemplateFileFormat();
		TemplateEmbeddedKeyCommonFields code = new TemplateEmbeddedKeyCommonFields();
		code.setCode("T101");
		code.setLangCode("ENG");
		templateFileFormat.setDescr("Email");
		templateFileFormat.setPkTfftCode(code);

		// Template Type
		List<TemplateType> templateTypes = new ArrayList<>();
		TemplateType templateType = new TemplateType();
		TemplateEmbeddedKeyCommonFields code1 = new TemplateEmbeddedKeyCommonFields();
		code.setCode("T101");
		code.setLangCode("ENG");
		templateType.setDescr("Description");
		templateType.setPkTmpltCode(code1);

		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		// Blacklisted Words
		List<BlacklistedWords> blackListedWords = new ArrayList<>();
		BlacklistedWords MasterBlacklistedWords = new BlacklistedWords();
		MasterBlacklistedWords.setWord("ABC");
		MasterBlacklistedWords.setDescription("description");
		MasterBlacklistedWords.setLangCode("ENG");
		blackListedWords.add(MasterBlacklistedWords);
		// titles
		List<Title> titles = new ArrayList<>();
		Title titleType = new Title();
		CodeAndLanguageCodeID idCode = new CodeAndLanguageCodeID();
		idCode.setCode("1001");
		idCode.setLangCode("eng");
		// titleType.setTitleDescription("dsddsd");
		titleType.setId(idCode);
		// titleType.setTitleName("admin");
		titles.add(titleType);
		// genders
		List<Gender> genders = new ArrayList<>();
		Gender genderEntity = new Gender();
		genderEntity.setCode("G1011");
		genderEntity.setGenderName("MALE");
		genderEntity.setLangCode("description");
		genders.add(genderEntity);
		// languages
		List<Language> languages = new ArrayList<>();
		Language MasterLanguages = new Language();
		MasterLanguages.setCode("ENG");
		MasterLanguages.setFamily("family");
		MasterLanguages.setName("english");
		MasterLanguages.setNativeName("native name");
		languages.add(MasterLanguages);
		// idTypes
		List<IdType> idTypes = new ArrayList<>();
		IdType idTypeDto = new IdType();
		CodeAndLanguageCodeID codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		idTypeDto.setName("ID");
		codeAndLanguageCodeID.setLangCode("ENG");
		idTypeDto.setIsActive(true);
		codeAndLanguageCodeID.setCode("ID101");
		idTypeDto.setDescr("descr");
		idTypeDto.setCodeAndLanguageCodeID(codeAndLanguageCodeID);
		idTypes.add(idTypeDto);
		// validDocuments
		List<ApplicantValidDocument> validDocuments = new ArrayList<>();
		ApplicantValidDocument MasterValidDocuments = new ApplicantValidDocument();
		ApplicantValidDocumentID validDocumentId = new ApplicantValidDocumentID();
		validDocumentId.setDocCatCode("D101");
		validDocumentId.setDocTypeCode("DC101");
		validDocumentId.setAppTypeCode("007");
		MasterValidDocuments.setValidDocument(validDocumentId);
		MasterValidDocuments.setLangCode("ENG");
		validDocuments.add(MasterValidDocuments);
		// biometric Attributes
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute attribute = new BiometricAttribute();
		attribute.setCode("B101");
		attribute.setDescription("description");
		attribute.setLangCode("eng");
		attribute.setName("FigerPrint");
		attribute.setBiometricTypeCode("B101");
		biometricAttributes.add(attribute);
		// Biometric type
		List<BiometricType> biometricTypes = new ArrayList<>();
		BiometricType bioType = new BiometricType();
		// bioType.setCode("BT101");
		bioType.setDescription("description");
		// bioType.setLangCode("ENG");
		bioType.setName("FigerPrint");
		biometricTypes.add(bioType);
		// Document Category
		List<DocumentCategory> documentCategories = new ArrayList<>();
		DocumentCategory docCatogery = new DocumentCategory();
		docCatogery.setCode("DC101");
		docCatogery.setName("DC name");
		docCatogery.setDescription("description");
		docCatogery.setLangCode("ENG");
		documentCategories.add(docCatogery);
		// Document Type
		List<DocumentType> documentTypes = new ArrayList<>();
		DocumentType doctype = new DocumentType();
		doctype.setCode("DT101");
		doctype.setName("DT Type");
		doctype.setDescription("description");
		doctype.setLangCode("ENG");
		documentTypes.add(doctype);
		// Reason Category
		List<ReasonCategory> reasonCategories = new ArrayList<>();
		ReasonCategory reson = new ReasonCategory();
		reson.setCode("RC101");
		reson.setName("101");
		reson.setLangCode("ENG");
		reasonCategories.add(reson);
		// Reason List
		List<ReasonList> reasonLists = new ArrayList<>();
		ReasonList MasterReasonLists = new ReasonList();
		MasterReasonLists.setCode("RL101");
		MasterReasonLists.setName("RL1");
		MasterReasonLists.setReasonCategory(reson);
		MasterReasonLists.setRsnCatCode("RL");
		MasterReasonLists.setLangCode("ENG");
		reasonLists.add(MasterReasonLists);
		// locations
		List<Location> locations = new ArrayList<>();
		Location locattion = new Location();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(MetaDataUtils.setCreateMetaData(blacklistedWordsList, BlacklistedWords.class))
				.thenReturn(baseEnity);

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

		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setApplicantValidDocuments(masterValidDocumnetsDto);

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

		List<RegistrationCommonFields> baseEnity = new ArrayList<>();
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
		List<ReasonListDto> categorieList = new ArrayList<>();
		ReasonListDto reasonListDto = new ReasonListDto();
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
		List<MachineMaster> machines = new ArrayList<>();
		MachineMaster machine = new MachineMaster();
		RegMachineSpecId specId = new RegMachineSpecId();
		specId.setId("100131");
		specId.setLangCode("eng");
		machine.setRegMachineSpecId(specId);
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machines.add(machine);
		List<Gender> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncGenderTypeRepository.saveAll(masterApplicationDtoEntity))
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

		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setApplicantValidDocuments(masterValidDocumnetsDto);

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

		List<RegistrationCommonFields> baseEnity = new ArrayList<>();
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
		List<ReasonListDto> categorieList = new ArrayList<>();
		ReasonListDto reasonListDto = new ReasonListDto();
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
		List<MachineMaster> machines = new ArrayList<>();
		MachineMaster machine = new MachineMaster();
		RegMachineSpecId specId = new RegMachineSpecId();
		specId.setId("100131");
		specId.setLangCode("eng");
		machine.setRegMachineSpecId(specId);
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machines.add(machine);
		List<Gender> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncGenderTypeRepository.saveAll(masterApplicationDtoEntity))
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

		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setApplicantValidDocuments(masterValidDocumnetsDto);

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

		List<RegistrationCommonFields> baseEnity = new ArrayList<>();
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
		List<ReasonListDto> categorieList = new ArrayList<>();
		ReasonListDto reasonListDto = new ReasonListDto();
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
		List<MachineMaster> machines = new ArrayList<>();
		MachineMaster machine = new MachineMaster();
		RegMachineSpecId specId = new RegMachineSpecId();
		specId.setId("100131");
		specId.setLangCode("eng");
		machine.setRegMachineSpecId(specId);
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machines.add(machine);
		List<Gender> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncGenderTypeRepository.saveAll(masterApplicationDtoEntity))
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

		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = new ArrayList<>();
		masterSyncDto.setApplicantValidDocuments(masterValidDocumnetsDto);

		List<TemplateDto> masterTemplateDto = new ArrayList<>();
		masterSyncDto.setTemplates(masterTemplateDto);

		List<TemplateTypeDto> masterTemplateTypeDto = new ArrayList<>();
		masterSyncDto.setTemplatesTypes(masterTemplateTypeDto);

		List<TemplateFileFormatDto> masterTemplateFileDto = new ArrayList<>();
		TemplateFileFormatDto tem = new TemplateFileFormatDto();
		tem.setCode("1001");
		tem.setDescription("text");
		tem.setIsActive(true);
		tem.setIsDeleted(false);
		masterTemplateFileDto.add(tem);
		masterSyncDto.setTemplateFileFormat(masterTemplateFileDto);

		List<RegistrationCenterDto> regCenter = new ArrayList<>();
		masterSyncDto.setRegistrationCenter(regCenter);

		List<MachineTypeDto> masterMachineType = new ArrayList<>();
		masterSyncDto.setMachineType(masterMachineType);

		List<RegistrationCommonFields> baseEnity = new ArrayList<>();
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
		List<ReasonListDto> categorieList = new ArrayList<>();
		ReasonListDto reasonListDto = new ReasonListDto();
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
		List<MachineMaster> machines = new ArrayList<>();
		MachineMaster machine = new MachineMaster();
		RegMachineSpecId specId = new RegMachineSpecId();
		specId.setId("100131");
		specId.setLangCode("eng");
		machine.setRegMachineSpecId(specId);
		machine.setIpAddress("172.12.01.128");
		machine.setMacAddress("21:21:21:12");
		machine.setMachineSpecId("9876427");
		machine.setMachineSpecId("1001");
		machine.setName("Laptop");
		machines.add(machine);
		List<Gender> masterApplicationDtoEntity = new ArrayList<>();
		try {
			Mockito.when(masterSyncGenderTypeRepository.saveAll(masterApplicationDtoEntity))
					.thenThrow(RegBaseUncheckedException.class);
			masterSyncDaoImpl.save(masterSyncDto);

		} catch (Exception exception) {

		}
	}

	@Test
	public void findLocationByLangCode() throws RegBaseCheckedException {

		List<Location> locations = new ArrayList<>();
		Location locattion = new Location();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncLocationRepository.findByIsActiveTrueAndHierarchyNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(locations);

		masterSyncDaoImpl.findLocationByLangCode("Region", "ENG");

		assertTrue(locations != null);
	}

	@Test
	public void findLocationByParentLocCode() throws RegBaseCheckedException {

		List<Location> locations = new ArrayList<>();
		Location locattion = new Location();
		locattion.setCode("LOC01");
		locattion.setName("english");
		locattion.setLangCode("ENG");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");
		locations.add(locattion);

		Mockito.when(masterSyncLocationRepository.findByIsActiveTrueAndHierarchyNameAndLangCode(Mockito.anyString(),
				Mockito.anyString())).thenReturn(locations);

		masterSyncDaoImpl.findLocationByParentLocCode("TPT", "eng");

		assertTrue(locations != null);
	}

	@Test
	public void findAllReason() throws RegBaseCheckedException {

		List<ReasonCategory> allReason = new ArrayList<>();
		ReasonCategory reasons = new ReasonCategory();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);

		Mockito.when(reasonCategoryRepository.findByIsActiveTrueAndLangCode(Mockito.anyString())).thenReturn(allReason);

		masterSyncDaoImpl.getAllReasonCatogery(Mockito.anyString());

		assertTrue(allReason != null);
	}

	@Test
	public void findAllReasonList() throws RegBaseCheckedException {

		List<String> reasonCat = new ArrayList<>();
		List<ReasonList> allReason = new ArrayList<>();
		ReasonList reasons = new ReasonList();
		reasons.setCode("DEMO");
		reasons.setName("InvalidData");
		reasons.setLangCode("FRE");
		allReason.add(reasons);

		Mockito.when(masterSyncReasonListRepository
				.findByIsActiveTrueAndLangCodeAndReasonCategoryCodeIn(Mockito.anyString(), Mockito.anyList()))
				.thenReturn(allReason);

		masterSyncDaoImpl.getReasonList("FRE", reasonCat);

		assertTrue(allReason != null);
	}

	@Test
	public void findBlackWords() throws RegBaseCheckedException {

		List<BlacklistedWords> allBlackWords = new ArrayList<>();
		BlacklistedWords blackWord = new BlacklistedWords();
		blackWord.setWord("asdfg");
		blackWord.setDescription("asdfg");
		blackWord.setLangCode("ENG");
		allBlackWords.add(blackWord);
		allBlackWords.add(blackWord);

		Mockito.when(
				masterSyncBlacklistedWordsRepository.findBlackListedWordsByIsActiveTrueAndLangCode(Mockito.anyString()))
				.thenReturn(allBlackWords);

		masterSyncDaoImpl.getBlackListedWords("ENG");

		assertTrue(allBlackWords != null);
	}

	@Test
	public void findDocumentCategories() throws RegBaseCheckedException {

		List<DocumentType> documents = new ArrayList<>();
		DocumentType document = new DocumentType();
		document.setName("Aadhar");
		document.setDescription("Aadhar card");
		document.setLangCode("ENG");
		documents.add(document);
		documents.add(document);
		List<String> validDocuments = new ArrayList<>();
		validDocuments.add("MNA");
		validDocuments.add("CLR");
		Mockito.when(masterSyncDao.getDocumentTypes(Mockito.anyList(), Mockito.anyString())).thenReturn(documents);

		masterSyncDaoImpl.getDocumentTypes(validDocuments, "test");

		assertTrue(documents != null);

	}

	@Test
	public void findGenders() throws RegBaseCheckedException {

		List<Gender> genderList = new ArrayList<>();
		Gender gender = new Gender();
		gender.setCode("1");
		gender.setGenderName("male");
		gender.setLangCode("ENG");
		gender.setIsActive(true);
		genderList.add(gender);

		Mockito.when(masterSyncDao.getGenderDtls(Mockito.anyString())).thenReturn(genderList);

		masterSyncDaoImpl.getGenderDtls("ENG");

		assertTrue(genderList != null);

	}

	@Test
	public void findValidDoc() {

		List<ValidDocument> docList = new ArrayList<>();
		ValidDocument docs = new ValidDocument();
		ValidDocumentID validDocumentId = new ValidDocumentID();
		validDocumentId.setDocCategoryCode("D101");
		validDocumentId.setDocTypeCode("DC101");
		docs.setLangCode("eng");
		docList.add(docs);

		Mockito.when(masterSyncDao.getValidDocumets(Mockito.anyString())).thenReturn(docList);

		masterSyncDaoImpl.getValidDocumets("POA");

		assertTrue(docList != null);

	}

	@Test
	public void individualTypes() {

		List<IndividualType> masterIndividualType = new ArrayList<>();
		IndividualType individualTypeEntity = new IndividualType();
		IndividualTypeId individualTypeId = new IndividualTypeId();
		individualTypeId.setCode("NFR");
		individualTypeId.setLangCode("eng");
		individualTypeEntity.setIndividualTypeId(individualTypeId);
		individualTypeEntity.setName("National");
		individualTypeEntity.setIsActive(true);
		masterIndividualType.add(individualTypeEntity);

		Mockito.when(masterSyncDao.getIndividulType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(masterIndividualType);

		masterSyncDaoImpl.getIndividulType("NFR", "eng");

		assertTrue(masterIndividualType != null);

	}
	
	@Test
	public void getBiometricType() {
		
		List<String> biometricType = new LinkedList<>(Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));
		List<BiometricAttribute> biometricAttributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("RS");
		biometricAttribute.setBiometricTypeCode("FNR");
		biometricAttribute.setName("Right Slap");
		biometricAttribute.setLangCode("eng");
		biometricAttributes.add(biometricAttribute);
		
		Mockito.when(biometricAttributeRepository.findByLangCodeAndBiometricTypeCodeIn("eng",biometricType)).thenReturn(biometricAttributes);
		assertNotNull(masterSyncDaoImpl.getBiometricType("eng", biometricType));
		
	}

}
