package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.ApplicantValidDocumentDto;
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.mastersync.AppAuthenticationMethodDto;
import io.mosip.registration.dto.mastersync.AppDetailDto;
import io.mosip.registration.dto.mastersync.AppRolePriorityDto;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BiometricTypeDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DeviceDto;
import io.mosip.registration.dto.mastersync.DeviceSpecificationDto;
import io.mosip.registration.dto.mastersync.DeviceTypeDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.DocumentTypeDto;
import io.mosip.registration.dto.mastersync.GenderDto;
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
import io.mosip.registration.dto.mastersync.ScreenAuthorizationDto;
import io.mosip.registration.dto.mastersync.ScreenDetailDto;
import io.mosip.registration.dto.mastersync.SyncJobDefDto;
import io.mosip.registration.dto.mastersync.TemplateDto;
import io.mosip.registration.dto.mastersync.TemplateFileFormatDto;
import io.mosip.registration.dto.mastersync.TemplateTypeDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.dto.mastersync.ValidDocumentDto;
import io.mosip.registration.entity.AppAuthenticationMethod;
import io.mosip.registration.entity.AppDetail;
import io.mosip.registration.entity.AppRolePriority;
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.entity.BiometricAttribute;
import io.mosip.registration.entity.BiometricType;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.entity.CenterMachine;
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
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCenterUser;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.RegDeviceSpec;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegMachineSpec;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationCenterType;
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
import io.mosip.registration.entity.id.CenterMachineId;
import io.mosip.registration.entity.id.RegCenterUserId;
import io.mosip.registration.entity.id.RegCentreMachineDeviceId;
import io.mosip.registration.entity.id.RegistartionCenterId;
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
import io.mosip.registration.repositories.ValidDocumentRepository;
import io.mosip.registration.util.mastersync.MetaDataUtils;

/**
 * The implementation class of {@link MasterSyncDao}
 * 
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class MasterSyncDaoImpl implements MasterSyncDao {

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobControlRepository syncStatusRepository;

	/** Object for Sync Biometric Attribute Repository. */
	@Autowired
	private BiometricAttributeRepository biometricAttributeRepository;

	/** Object for Sync Biometric Type Repository. */
	@Autowired
	private BiometricTypeRepository biometricTypeRepository;

	/** Object for Sync Blacklisted Words Repository. */
	@Autowired
	private BlacklistedWordsRepository blacklistedWordsRepository;

	/** Object for Sync Device Repository. */
	@Autowired
	private DeviceMasterRepository deviceMasterRepository;

	/** Object for Sync Device Specification Repository. */
	@Autowired
	private DeviceSpecificationRepository deviceSpecificationRepository;

	/** Object for Sync Device Type Repository. */
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;

	/** Object for Sync Document Category Repository. */
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	/** Object for Sync Document Type Repository. */
	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	/** Object for Sync Gender Type Repository. */
	@Autowired
	private GenderRepository genderRepository;

	/** Object for Sync Id Type Repository. */
	@Autowired
	private IdTypeRepository idTypeRepository;

	/** Object for Sync Location Repository. */
	@Autowired
	private LocationRepository locationRepository;

	/** Object for Sync Machine Repository. */
	@Autowired
	private MachineMasterRepository machineRepository;

	/** Object for Sync Machine Specification Repository. */
	@Autowired
	private MachineSpecificationRepository machineSpecificationRepository;

	/** Object for Sync Machine Type Repository. */
	@Autowired
	private MachineTypeRepository machineTypeRepository;

	/** Object for Sync Reason Category Repository. */
	@Autowired
	private ReasonCategoryRepository reasonCategoryRepository;

	/** Object for Sync Reason List Repository. */
	@Autowired
	private ReasonListRepository reasonListRepository;

	/** Object for Sync Template File Format Repository. */
	@Autowired
	private TemplateFileFormatRepository templateFileFormatRepository;

	/** Object for Sync Template Repository. */
	@Autowired
	private TemplateRepository templateRepository;

	/** Object for Sync Template Type Repository. */
	@Autowired
	private TemplateTypeRepository templateTypeRepository;

	/** Object for Sync Title Repository. */
	@Autowired
	private TitleRepository titleRepository;

	/** Object for Sync Applicant Valid Document Repository. */
	@Autowired
	private ApplicantValidDocumentRepository applicantValidDocumentRepository;

	/** Object for Sync Valid Document Repository. */
	@Autowired
	private ValidDocumentRepository validDocumentRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private LanguageRepository languageRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private CenterMachineRepository centerMachineRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	/** Object for Sync Individual type Repository. */
	@Autowired
	private IndividualTypeRepository individualTypeRepository;

	/** Object for Sync app authentication Repository. */
	@Autowired
	private AppAuthenticationRepository appAuthenticationRepository;

	/** Object for Sync app role Repository. */
	@Autowired
	private AppRolePriorityRepository appRolePriorityRepository;

	/** Object for Sync app detail Repository. */
	@Autowired
	private AppDetailRepository appDetailRepository;

	/** Object for Sync screen auth Repository. */
	@Autowired
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	/** Object for Sync screen auth Repository. */
	@Autowired
	private ProcessListRepository processListRepository;

	/** Object for screen detail Repository. */
	@Autowired
	private ScreenDetailRepository screenDetailRepository;

	/** Object for Sync screen auth Repository. */
	@Autowired
	private SyncJobDefRepository syncJobDefRepository;
	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getMasterSyncStatus()
	 */
	@Override
	public SyncControl syncJobDetails(String synccontrol) {

		SyncControl syncControlResonse = null;

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"DAO findByID method started");

		try {
			// find the user
			syncControlResonse = syncStatusRepository.findBySyncJobId(synccontrol);

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_JOD_DETAILS,
					runtimeException.getMessage());
		}

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"DAO findByID method ended");

		return syncControlResonse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#save(io.mosip.registration.mastersync
	 * .dto.MasterSyncDto)
	 */
	@Override
	public synchronized String save(MasterDataResponseDto masterSyncDto) {

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				RegistrationConstants.MASTER_SYNC_LOGGER_INFO);

		List<MachineDto> masterMachineDto = masterSyncDto.getMachineDetails();
		List<MachineSpecificationDto> masterMachineSpecDto = masterSyncDto.getMachineSpecification();
		List<MachineTypeDto> masterMachineTypeDto = masterSyncDto.getMachineType();
		List<DeviceDto> masterDeviceDto = masterSyncDto.getDevices();
		List<DeviceTypeDto> masterDeviceTypeDto = masterSyncDto.getDeviceTypes();
		List<DeviceSpecificationDto> masterDeviceSpecificDto = masterSyncDto.getDeviceSpecifications();
		List<DocumentCategoryDto> masterDocumnetCategoryDto = masterSyncDto.getDocumentCategories();
		List<DocumentTypeDto> masterDocumnetTypeDto = masterSyncDto.getDocumentTypes();
		List<ApplicantValidDocumentDto> masterValidDocumnetsDto = masterSyncDto.getApplicantValidDocuments();
		List<TemplateDto> masterTemplateDto = masterSyncDto.getTemplates();
		List<TemplateTypeDto> masterTemplateTypeDto = masterSyncDto.getTemplatesTypes();
		List<TemplateFileFormatDto> masterTemplateFileDto = masterSyncDto.getTemplateFileFormat();
		List<PostReasonCategoryDto> masterPostReasonCategoryDto = masterSyncDto.getReasonCategory();
		List<ReasonListDto> masterReasonListDto = masterSyncDto.getReasonList();
		List<BlacklistedWordsDto> masterBlackListedWordsDto = masterSyncDto.getBlackListedWords();
		List<LocationDto> masterLocationDto = masterSyncDto.getLocationHierarchy();
		List<BiometricAttributeDto> masterBiometricAttributeDto = masterSyncDto.getBiometricattributes();
		List<BiometricTypeDto> masterBiometricTypeDto = masterSyncDto.getBiometricTypes();
		List<IdTypeDto> masterIdTypeDto = masterSyncDto.getIdTypes();
		List<TitleDto> masterTitleDto = masterSyncDto.getTitles();
		List<GenderDto> masterGenderDto = masterSyncDto.getGenders();
		List<LanguageDto> languageDto = masterSyncDto.getLanguages();
		List<RegistrationCenterMachineDto> registrationCenterMachines = masterSyncDto.getRegistrationCenterMachines();
		List<RegistrationCenterDeviceDto> registrationCenterDevices = masterSyncDto.getRegistrationCenterDevices();
		List<RegistrationCenterMachineDeviceDto> registrationCenterMachineDevices = masterSyncDto
				.getRegistrationCenterMachineDevices();
		List<RegistrationCenterUserDto> registrationCenterUsers = masterSyncDto.getRegistrationCenterUsers();
		List<RegistrationCenterDto> registrationCenter = masterSyncDto.getRegistrationCenter();
		List<RegistrationCenterTypeDto> registrationCenterType = masterSyncDto.getRegistrationCenterTypes();
		List<IndividualTypeDto> indiviualType = masterSyncDto.getIndividualTypes();
		List<ValidDocumentDto> masterValidDocuments = masterSyncDto.getValidDocumentMapping();
		List<AppAuthenticationMethodDto> appAuthMethods = masterSyncDto.getAppAuthenticationMethods();
		List<AppDetailDto> appDetails = masterSyncDto.getAppDetails();
		List<AppRolePriorityDto> appRolePriority = masterSyncDto.getAppRolePriorities();
		List<ScreenAuthorizationDto> screenAuth = masterSyncDto.getScreenAuthorizations();
		List<ProcessListDto> processLst = masterSyncDto.getProcessList();
		List<ScreenDetailDto> screenDetailList = masterSyncDto.getScreenDetails();
		List<SyncJobDefDto> syncJobDefList = masterSyncDto.getSyncJobDefinitions();
		String sucessResponse = null;

		try {

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Language details syncing....");

			List<Language> masterLangauge = MetaDataUtils.setCreateMetaData(languageDto, Language.class);
			languageRepository.saveAll(masterLangauge);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Biometric Type details syncing....");

			List<BiometricType> masterBiometricTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterBiometricTypeDto,
					BiometricType.class);
			biometricTypeRepository.saveAll(masterBiometricTypeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Biometric Attribute details syncing....");

			List<BiometricAttribute> masterBiometricAttributeDtoEntity = MetaDataUtils
					.setCreateMetaData(masterBiometricAttributeDto, BiometricAttribute.class);
			biometricAttributeRepository.saveAll(masterBiometricAttributeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Blacklisted Words syncing....");
			List<BlacklistedWords> blacklistedWordsEntity = MetaDataUtils.setCreateMetaData(masterBlackListedWordsDto,
					BlacklistedWords.class);
			blacklistedWordsRepository.saveAll(blacklistedWordsEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Registration Device Type details syncing....");
			List<RegDeviceType> masterDeviceTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterDeviceTypeDto,
					RegDeviceType.class);
			deviceTypeRepository.saveAll(masterDeviceTypeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Registration Device Specification details syncing....");
			List<RegDeviceSpec> masterDeviceSpecificDtoEntity = MetaDataUtils.setCreateMetaData(masterDeviceSpecificDto,
					RegDeviceSpec.class);
			deviceSpecificationRepository.saveAll(masterDeviceSpecificDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Registration Device Master details syncing....");
			List<RegDeviceMaster> masterDeviceDtoEntity = MetaDataUtils.setCreateMetaData(masterDeviceDto,
					RegDeviceMaster.class);
			deviceMasterRepository.saveAll(masterDeviceDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Document Category details syncing....");
			List<DocumentCategory> masterDocumnetCategoryDtoEntity = MetaDataUtils
					.setCreateMetaData(masterDocumnetCategoryDto, DocumentCategory.class);
			documentCategoryRepository.saveAll(masterDocumnetCategoryDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Document Type details syncing....");
			List<DocumentType> masterDocumnetTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterDocumnetTypeDto,
					DocumentType.class);
			documentTypeRepository.saveAll(masterDocumnetTypeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Gender details syncing....");
			List<Gender> masterGenderDtoEntity = MetaDataUtils.setCreateMetaData(masterGenderDto, Gender.class);
			genderRepository.saveAll(masterGenderDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"IdType details syncing....");
			List<IdType> masterIdTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterIdTypeDto, IdType.class);
			idTypeRepository.saveAll(masterIdTypeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Location details syncing....");
			List<Location> masterLocationDtoEntity = MetaDataUtils.setCreateMetaData(masterLocationDto, Location.class);
			locationRepository.saveAll(masterLocationDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Machine Type details syncing....");
			List<MachineType> masterMachineTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterMachineTypeDto,
					MachineType.class);
			machineTypeRepository.saveAll(masterMachineTypeDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Machine Specification details syncing....");
			List<RegMachineSpec> masterMachineSpecDtoEntity = MetaDataUtils.setCreateMetaData(masterMachineSpecDto,
					RegMachineSpec.class);
			machineSpecificationRepository.saveAll(masterMachineSpecDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Machine Master details syncing....");
			List<MachineMaster> masterMachineDtoEntity = MetaDataUtils.setCreateMetaData(masterMachineDto,
					MachineMaster.class);
			machineRepository.saveAll(masterMachineDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Rejection Reason Category details syncing....");
			List<ReasonCategory> masterReasonCategoryDtoEntity = MetaDataUtils
					.setCreateMetaData(masterPostReasonCategoryDto, ReasonCategory.class);
			reasonCategoryRepository.saveAll(masterReasonCategoryDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Rejection Reason List details syncing....");
			List<ReasonList> masterReasonListDtoEntity = MetaDataUtils.setCreateMetaData(masterReasonListDto,
					ReasonList.class);
			reasonListRepository.saveAll(masterReasonListDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Template File Format details syncing....");
			List<TemplateFileFormat> masterTemplateFileDtoEntity = new ArrayList<>();
			if (null != masterTemplateFileDto && !masterTemplateFileDto.isEmpty()) {
				masterTemplateFileDto.forEach(templateFrmat -> {
					TemplateFileFormat templFrmat = new TemplateFileFormat();
					TemplateEmbeddedKeyCommonFields commnFields = new TemplateEmbeddedKeyCommonFields();
					commnFields.setCode(templateFrmat.getCode());
					commnFields.setLangCode(templateFrmat.getLangCode());
					templFrmat.setPkTfftCode(commnFields);
					templFrmat.setDescr(templateFrmat.getDescription());
					templFrmat.setActive(templateFrmat.getIsActive());
					if (SessionContext.isSessionContextAvailable()) {
						templFrmat.setCrBy(SessionContext.userContext().getUserId());
					} else {
						templFrmat.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
					}
					templFrmat.setCrDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
					masterTemplateFileDtoEntity.add(templFrmat);
				});

				templateFileFormatRepository.saveAll(masterTemplateFileDtoEntity);
			}
			
			templateDetailsSync(masterTemplateDto);
			templateTypeDetailsSync(masterTemplateTypeDto);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Title details syncing....");
			List<Title> masterTitleDtoEntity = MetaDataUtils.setCreateMetaData(masterTitleDto, Title.class);
			titleRepository.saveAll(masterTitleDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Applicant Valid Document details syncing....");
			List<ApplicantValidDocument> masterValidDocumnetsDtoEntity = MetaDataUtils
					.setCreateMetaData(masterValidDocumnetsDto, ApplicantValidDocument.class);
			applicantValidDocumentRepository.saveAll(masterValidDocumnetsDtoEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Individual Type details syncing....");
			List<IndividualType> masterIndividualType = MetaDataUtils.setCreateMetaData(indiviualType,
					IndividualType.class);
			individualTypeRepository.saveAll(masterIndividualType);

			registrationCenterDetailsSync(registrationCenter);
			registrationCenterTypeDetailsSync(registrationCenterType);
			
			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"Registration Center Device details syncing....");
			List<RegCenterDevice> masterRegCenterDeviceEntity = MetaDataUtils
					.setCreateMetaData(registrationCenterDevices, RegCenterDevice.class);
			registrationCenterDeviceRepository.saveAll(masterRegCenterDeviceEntity);

			registrationCenterMachineDeviceDetailsSync(registrationCenterMachineDevices);
			
			registrationCenterUserDetailsSync(registrationCenterUsers);
			centerMachineMappingDetailsSync(registrationCenterMachines);
		
			List<ValidDocument> masterValidDocumentsEntity = MetaDataUtils.setCreateMetaData(masterValidDocuments,
					ValidDocument.class);
			validDocumentRepository.saveAll(masterValidDocumentsEntity);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"App role priority details syncing....");

			List<AppRolePriority> masterAppRolePriority = MetaDataUtils.setCreateMetaData(appRolePriority,
					AppRolePriority.class);
			appRolePriorityRepository.saveAll(masterAppRolePriority);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"App details syncing....");

			List<AppDetail> masterAppDetails = MetaDataUtils.setCreateMetaData(appDetails, AppDetail.class);
			appDetailRepository.saveAll(masterAppDetails);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"process list details syncing....");

			List<ProcessList> masterProcessList = MetaDataUtils.setCreateMetaData(processLst, ProcessList.class);
			processListRepository.saveAll(masterProcessList);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"App authentication method details syncing....");

			List<AppAuthenticationMethod> masterAppLoginMethod = MetaDataUtils.setCreateMetaData(appAuthMethods,
					AppAuthenticationMethod.class);
			appAuthenticationRepository.saveAll(masterAppLoginMethod);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"screen autherization details syncing....");

			List<ScreenAuthorization> masterScreenAuth = MetaDataUtils.setCreateMetaData(screenAuth,
					ScreenAuthorization.class);
			screenAuthorizationRepository.saveAll(masterScreenAuth);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"screen detail list syncing....");

			List<ScreenDetail> masterScreenDetailList = MetaDataUtils.setCreateMetaData(screenDetailList,
					ScreenDetail.class);
			screenDetailRepository.saveAll(masterScreenDetailList);

			LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					"sync control list details syncing....");

			List<SyncJobDef> masterSyncControlList = MetaDataUtils.setCreateMetaData(syncJobDefList, SyncJobDef.class);
			syncJobDefRepository.saveAll(masterSyncControlList);

			sucessResponse = RegistrationConstants.SUCCESS;

		} catch (Exception runtimeException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			sucessResponse = RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO;

			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION + sucessResponse,
					runtimeException.getMessage());

		}

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Leaving Insert Master Sync Data..");

		return sucessResponse;

	}

	/**
	 * Center machine mapping details sync.
	 *
	 * @param registrationCenterMachines 
	 * 				the registration center machine DTO list.
	 */
	private void centerMachineMappingDetailsSync(List<RegistrationCenterMachineDto> registrationCenterMachines) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Center Machine Mapping details syncing....");
		if (null != registrationCenterMachines && !registrationCenterMachines.isEmpty()) {
			List<CenterMachine> masterRegCenterMachineEntity = new ArrayList<>();
			registrationCenterMachines.forEach(centerMachine -> {
				CenterMachine centerMachn = new CenterMachine();
				CenterMachineId centerMachnId = new CenterMachineId();
				centerMachnId.setCentreId(centerMachine.getRegCenterId());
				centerMachnId.setId(centerMachine.getMachineId());
				centerMachn.setCenterMachineId(centerMachnId);
				centerMachn.setIsActive(centerMachine.getIsActive());
				if (SessionContext.isSessionContextAvailable()) {
					centerMachn.setCrBy(SessionContext.userContext().getUserId());
				} else {
					centerMachn.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				centerMachn.setLangCode(centerMachine.getLangCode());
				centerMachn.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				masterRegCenterMachineEntity.add(centerMachn);
			});
			centerMachineRepository.saveAll(masterRegCenterMachineEntity);
		}
	}

	/**
	 * Registration center user details sync.
	 *
	 * @param registrationCenterUsers 
	 * 				the registration center user DTO list.
	 */
	private void registrationCenterUserDetailsSync(List<RegistrationCenterUserDto> registrationCenterUsers) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Registration Center user details syncing....");
		if (null != registrationCenterUsers && !registrationCenterUsers.isEmpty()) {
		List<RegCenterUser> masterRegCenterUserEntity = new ArrayList<>();
		registrationCenterUsers.forEach(centerUser -> {
			RegCenterUser centerUsr = new RegCenterUser();
			RegCenterUserId userIdMapping = new RegCenterUserId();
			centerUsr.setIsActive(centerUser.getIsActive());
			if (SessionContext.isSessionContextAvailable()) {
				centerUsr.setCrBy(SessionContext.userContext().getUserId());
			} else {
				centerUsr.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
			}
			centerUsr.setLangCode("eng");
			centerUsr.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
			userIdMapping.setRegcntrId(centerUser.getRegCenterId());
			userIdMapping.setUsrId(centerUser.getUserId());
			centerUsr.setRegCenterUserId(userIdMapping);
			masterRegCenterUserEntity.add(centerUsr);
		});
		registrationCenterUserRepository.saveAll(masterRegCenterUserEntity);
		}
	}

	/**
	 * Registration center machine device details sync.
	 *
	 * @param registrationCenterMachineDevices 
	 * 				the registration center machine device DTO list.
	 */
	private void registrationCenterMachineDeviceDetailsSync(List<RegistrationCenterMachineDeviceDto> registrationCenterMachineDevices) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Registration Center Machine Device details syncing....");
		if (null != registrationCenterMachineDevices && !registrationCenterMachineDevices.isEmpty()) {
			List<RegCentreMachineDevice> masterRegCenterMachineDeviceEntity = new ArrayList<>();
			registrationCenterMachineDevices.forEach(centerMachDev -> {
				RegCentreMachineDevice regMachDev = new RegCentreMachineDevice();
				RegCentreMachineDeviceId regMachDevId = new RegCentreMachineDeviceId();
				regMachDevId.setDeviceId(centerMachDev.getDeviceId());
				regMachDevId.setMachineId(centerMachDev.getMachineId());
				regMachDevId.setRegCentreId(centerMachDev.getRegCenterId());
				regMachDev.setRegCentreMachineDeviceId(regMachDevId);
				regMachDev.setIsActive(centerMachDev.getIsActive());
				if (SessionContext.isSessionContextAvailable()) {
					regMachDev.setCrBy(SessionContext.userContext().getUserId());
				} else {
					regMachDev.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				regMachDev.setLangCode(centerMachDev.getLangCode());
				regMachDev.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				masterRegCenterMachineDeviceEntity.add(regMachDev);
			});
			registrationCenterMachineDeviceRepository.saveAll(masterRegCenterMachineDeviceEntity);
		}
	}

	/**
	 * Registration center type details sync.
	 *
	 * @param registrationCenterType 
	 * 				the registration center type DTO list.
	 */
	private void registrationCenterTypeDetailsSync(List<RegistrationCenterTypeDto> registrationCenterType) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Registration Center Type details syncing....");
		if (null != registrationCenterType && !registrationCenterType.isEmpty()) {
			List<RegistrationCenterType> regCenterType = new ArrayList<>();
			registrationCenterType.forEach(centerType -> {

				RegistrationCenterType regCentrType = new RegistrationCenterType();
				regCentrType.setCode(centerType.getCode());
				regCentrType.setName(centerType.getName());
				regCentrType.setDescr(centerType.getDescr());
				regCentrType.setLangCode(centerType.getLangCode());
				regCentrType.setIsActive(centerType.getIsActive());
				if (SessionContext.isSessionContextAvailable()) {
					regCentrType.setCrBy(SessionContext.userContext().getUserId());
				} else {
					regCentrType.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				regCentrType.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				regCenterType.add(regCentrType);
			});
			registrationCenterTypeRepository.saveAll(regCenterType);
		}
	}

	/**
	 * Registration center details sync.
	 *
	 * @param registrationCenter 
	 * 				the registration center DTO list.
	 */
	private void registrationCenterDetailsSync(List<RegistrationCenterDto> registrationCenter) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Registration Center details syncing....");
		if (null != registrationCenter && !registrationCenter.isEmpty()) {
			List<RegistrationCenter> regCentr = new ArrayList<>();
			registrationCenter.forEach(regCenter -> {
				RegistrationCenter regCen = new RegistrationCenter();
				RegistartionCenterId cenId = new RegistartionCenterId();
				cenId.setId(regCenter.getId());
				cenId.setLangCode(regCenter.getLangCode());
				regCen.setRegistartionCenterId(cenId);
				regCen.setAddrLine1(regCenter.getAddressLine1());
				regCen.setAddrLine2(regCenter.getAddressLine2());
				regCen.setAddrLine3(regCenter.getAddressLine3());
				regCen.setCenterName(regCenter.getName());
				regCen.setCenterStartTime(Time.valueOf(regCenter.getCenterStartTime()));
				regCen.setCenterEndTime(Time.valueOf(regCenter.getCenterEndTime()));
				regCen.setCntrTypCode(regCenter.getCenterTypeCode());
				regCen.setContactPerson(regCenter.getContactPerson());
				regCen.setContactPhone(regCenter.getContactPhone());
				regCen.setHolidayLocCode(regCenter.getHolidayLocationCode());
				regCen.setLatitude(regCenter.getLatitude());
				regCen.setLongitude(regCenter.getLongitude());
				regCen.setLunchStartTime(Time.valueOf(regCenter.getLunchStartTime()));
				regCen.setLunchEndTime(Time.valueOf(regCenter.getLunchEndTime()));
				regCen.setWorkingHours(regCenter.getWorkingHours());
				regCen.setLocationCode(regCenter.getLocationCode());
				regCen.setNumberOfKiosks(regCenter.getNumberOfKiosks().intValue());
				regCen.setPerKioskProcessTime(Time.valueOf(regCenter.getPerKioskProcessTime()));
				regCen.setTimeZone(regCenter.getTimeZone());
				if (SessionContext.isSessionContextAvailable()) {
					regCen.setCrBy(SessionContext.userContext().getUserId());
				} else {
					regCen.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				regCen.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				regCen.setIsActive(regCenter.getIsActive());
				regCentr.add(regCen);
			});

			registrationCenterRepository.saveAll(regCentr);

		}
	}

	/**
	 * Template type details sync.
	 *
	 * @param masterTemplateTypeDto 
	 * 				the master template type DTO list.
	 */
	private void templateTypeDetailsSync(List<TemplateTypeDto> masterTemplateTypeDto) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Template Type details syncing....");
		if (null != masterTemplateTypeDto && !masterTemplateTypeDto.isEmpty()) {
			List<TemplateType> masterTemplateTypeDtoEntity = new ArrayList<>();
			masterTemplateTypeDto.forEach(templateType -> {
				TemplateType tempType = new TemplateType();
				TemplateEmbeddedKeyCommonFields commnFields = new TemplateEmbeddedKeyCommonFields();
				commnFields.setCode(templateType.getCode());
				commnFields.setLangCode(templateType.getLangCode());
				tempType.setPkTmpltCode(commnFields);
				tempType.setActive(templateType.getIsActive());
				tempType.setDescr(templateType.getDescription());
				if (SessionContext.isSessionContextAvailable()) {
					tempType.setCrBy(SessionContext.userContext().getUserId());
				} else {
					tempType.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				tempType.setCrDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				masterTemplateTypeDtoEntity.add(tempType);
			});

			templateTypeRepository.saveAll(masterTemplateTypeDtoEntity);
		}
	}

	/**
	 * Template details sync.
	 *
	 * @param masterTemplateDto 
	 * 				the master template DTO list.
	 */
	private void templateDetailsSync(List<TemplateDto> masterTemplateDto) {
		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"Template details syncing....");
		if (null != masterTemplateDto && !masterTemplateDto.isEmpty()) {
			List<Template> templetList = new ArrayList<>();
			masterTemplateDto.forEach(templet -> {

				Template templete = new Template();
				templete.setId(templet.getId());
				templete.setFileFormatCode(templet.getFileFormatCode());
				templete.setFileTxt(templet.getFileText());
				templete.setDescr(templet.getDescription());
				templete.setActive(templet.getIsActive());
				templete.setName(templet.getName());
				templete.setModuleName(templet.getModuleName());
				templete.setTemplateTypCode(templet.getTemplateTypeCode());
				templete.setModel(templet.getModel());
				templete.setModuleId(templet.getModuleId());
				templete.setLangCode(templet.getLangCode());
				if (SessionContext.isSessionContextAvailable()) {
					templete.setCrBy(SessionContext.userContext().getUserId());
				} else {
					templete.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				}
				templete.setCrDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				templetList.add(templete);
			});

			templateRepository.saveAll(templetList);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#findLocationByLangCode(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<Location> findLocationByLangCode(String hierarchyCode, String langCode) {
		return locationRepository.findByIsActiveTrueAndHierarchyNameAndLangCode(hierarchyCode, langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#findLocationByParentLocCode(java.lang
	 * .String)
	 */
	@Override
	public List<Location> findLocationByParentLocCode(String parentLocCode, String langCode) {
		return locationRepository.findByIsActiveTrueAndParentLocCodeAndLangCode(parentLocCode, langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getAllReasonCatogery()
	 */
	@Override
	public List<ReasonCategory> getAllReasonCatogery(String langCode) {
		return reasonCategoryRepository.findByIsActiveTrueAndLangCode(langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getReasonList(java.util.List)
	 */
	@Override
	public List<ReasonList> getReasonList(String langCode, List<String> reasonCat) {
		return reasonListRepository.findByIsActiveTrueAndLangCodeAndReasonCategoryCodeIn(langCode, reasonCat);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#getBlackListedWords(java.lang.String)
	 */
	@Override
	public List<BlacklistedWords> getBlackListedWords(String langCode) {
		return blacklistedWordsRepository.findBlackListedWordsByIsActiveTrueAndLangCode(langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getDocumentCategories(java.lang.
	 * String)
	 */
	@Override
	public List<DocumentType> getDocumentTypes(List<String> docCode, String langCode) {
		return documentTypeRepository.findByIsActiveTrueAndLangCodeAndCodeIn(langCode, docCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getGenderDtls(java.lang.String)
	 */
	@Override
	public List<Gender> getGenderDtls(String langCode) {

		return genderRepository.findByIsActiveTrueAndLangCode(langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#getValidDocumets(java.lang.String)
	 */
	@Override
	public List<ValidDocument> getValidDocumets(String docCategoryCode) {
		return validDocumentRepository.findByIsActiveTrueAndDocCategoryCode(docCategoryCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#getIndividulType(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<IndividualType> getIndividulType(String code, String langCode) {
		return individualTypeRepository.findByIndividualTypeIdCodeAndIndividualTypeIdLangCodeAndIsActiveTrue(code,
				langCode);
	}

	public List<SyncJobDef> getSyncJobs() {
		return syncJobDefRepository.findAllByIsActiveTrue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getBiometricType(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<BiometricAttribute> getBiometricType(String langCode, List<String> biometricType) {
		return biometricAttributeRepository.findByLangCodeAndBiometricTypeCodeIn(langCode, biometricType);
	}

	public List<Language> getActiveLanguages() {

		return languageRepository.findAllByIsActiveTrue();
	}

	public List<Gender> getGenders() {
		return genderRepository.findAllByIsActiveTrue();
	}

	public List<DocumentCategory> getDocumentCategory() {
		return documentCategoryRepository.findAllByIsActiveTrue();
	}

	public List<Location> getLocationDetails() {
		return locationRepository.findAllByIsActiveTrue();
	}

}
