package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MasterSyncDao;
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
import io.mosip.registration.entity.mastersync.MasterLocation;
import io.mosip.registration.entity.mastersync.MasterMachine;
import io.mosip.registration.entity.mastersync.MasterMachineSpecification;
import io.mosip.registration.entity.mastersync.MasterMachineType;
import io.mosip.registration.entity.mastersync.MasterReasonCategory;
import io.mosip.registration.entity.mastersync.MasterReasonList;
import io.mosip.registration.entity.mastersync.MasterRegistrationCenter;
import io.mosip.registration.entity.mastersync.MasterTemplate;
import io.mosip.registration.entity.mastersync.MasterTemplateFileFormat;
import io.mosip.registration.entity.mastersync.MasterTemplateType;
import io.mosip.registration.entity.mastersync.MasterTitle;
import io.mosip.registration.entity.mastersync.MasterValidDocument;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncJobRepository;
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
import io.mosip.registration.util.mastersync.MetaDataUtils;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class MasterSyncDaoImpl implements MasterSyncDao {

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobRepository syncStatusRepository;

	/** Object for Sync Application Repository. */
	@Autowired
	MasterSyncApplicationRepository masterSyncApplicationRepository;

	/** Object for Sync Biometric Attribute Repository. */
	@Autowired
	MasterSyncBiometricAttributeRepository masterSyncBiometricAttributeRepository;

	/** Object for Sync Biometric Type Repository. */
	@Autowired
	MasterSyncBiometricTypeRepository masterSyncBiometricTypeRepository;

	/** Object for Sync Blacklisted Words Repository. */
	@Autowired
	MasterSyncBlacklistedWordsRepository masterSyncBlacklistedWordsRepository;

	/** Object for Sync Device Repository. */
	@Autowired
	MasterSyncDeviceRepository masterSyncDeviceRepository;

	/** Object for Sync Device Specification Repository. */
	@Autowired
	MasterSyncDeviceSpecificationRepository masterSyncDeviceSpecificationRepository;

	/** Object for Sync Device Type Repository. */
	@Autowired
	MasterSyncDeviceTypeRepository masterSyncDeviceTypeRepository;

	/** Object for Sync Document Category Repository. */
	@Autowired
	MasterSyncDocumentCategoryRepository masterSyncDocumentCategoryRepository;

	/** Object for Sync Document Type Repository. */
	@Autowired
	MasterSyncDocumentTypeRepository masterSyncDocumentTypeRepository;

	/** Object for Sync Gender Type Repository. */
	@Autowired
	MasterSyncGenderRepository masterSyncGenderRepository;

	/** Object for Sync Holiday Repository. */
	@Autowired
	MasterSyncHolidayRepository masterSyncHolidayRepository;

	/** Object for Sync Id Type Repository. */
	@Autowired
	MasterSyncIdTypeRepository masterSyncIdTypeRepository;

	/** Object for Sync Language Repository. */
	@Autowired
	MasterSyncLanguageRepository masterSyncLanguageRepository;

	/** Object for Sync Location Repository. */
	@Autowired
	MasterSyncLocationRepository masterSyncLocationRepository;

	/** Object for Sync Machine Repository. */
	@Autowired
	MasterSyncMachineRepository masterSyncMachineRepository;

	/** Object for Sync Machine Specification Repository. */
	@Autowired
	MasterSyncMachineSpecificationRepository masterSyncMachineSpecificationRepository;

	/** Object for Sync Machine Type Repository. */
	@Autowired
	MasterSyncMachineTypeRepository masterSyncMachineTypeRepository;

	/** Object for Sync Reason Category Repository. */
	@Autowired
	MasterSyncReasonCategoryRepository masterSyncReasonCategoryRepository;

	/** Object for Sync Reason List Repository. */
	@Autowired
	MasterSyncReasonListRepository masterSyncReasonListRepository;

	/** Object for Sync Registration Center Repository. */
	@Autowired
	MasterSyncRegistrationCenterRepository masterSyncRegistrationCenterRepository;

	/** Object for Sync Registration Center Type Repository. */
	@Autowired
	MasterSyncRegistrationCenterTypeRepository masterSyncRegistrationCenterTypeRepository;

	/** Object for Sync Template File Format Repository. */
	@Autowired
	MasterSyncTemplateFileFormatRepository masterSyncTemplateFileFormatRepository;

	/** Object for Sync Template Repository. */
	@Autowired
	MasterSyncTemplateRepository masterSyncTemplateRepository;

	/** Object for Sync Template Type Repository. */
	@Autowired
	MasterSyncTemplateTypeRepository masterSyncTemplateTypeRepository;

	/** Object for Sync Title Repository. */
	@Autowired
	MasterSyncTitleRepository masterSyncTitleRepository;

	/** Object for Sync Valid Document Repository. */
	@Autowired
	MasterSyncValidDocumentRepository masterSyncValidDocumentRepository;

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
	public SyncControl getMasterSyncStatus(String synccontrol) {

		SyncControl syncControlResonse = null;

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"DAO findByID method started");

		try {
			// find the user
			syncControlResonse = syncStatusRepository.findBySyncJobId(synccontrol);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_JOD_DETAILS,
					runtimeException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
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
	public String insertMasterSyncData(MasterDataResponseDto masterSyncDto) throws RegBaseUncheckedException{

		List<RegistrationCenterDto> regCenter = masterSyncDto.getRegistrationCenter();
		List<MachineDto> masterMachineDto = masterSyncDto.getMachineDetails();
		List<MachineSpecificationDto> masterMachineSpecDto = masterSyncDto.getMachineSpecification();
		List<MachineTypeDto> masterMachineTypeDto = masterSyncDto.getMachineType();
		List<DeviceDto> masterDeviceDto = masterSyncDto.getDevices();
		List<DeviceTypeDto> masterDeviceTypeDto = masterSyncDto.getDeviceTypes();
		List<DeviceSpecificationDto> masterDeviceSpecificDto = masterSyncDto.getDeviceSpecifications();
		List<HolidayDto> masterHolidaysDto = masterSyncDto.getHolidays();
		List<DocumentCategoryDto> masterDocumnetCategoryDto = masterSyncDto.getDocumentCategories();
		List<DocumentTypeDto> masterDocumnetTypeDto = masterSyncDto.getDocumentTypes();
		List<ValidDocumentDto> masterValidDocumnetsDto = masterSyncDto.getValidDocumentMapping();
		List<TemplateDto> masterTemplateDto = masterSyncDto.getTemplates();
		List<TemplateTypeDto> masterTemplateTypeDto = masterSyncDto.getTemplatesTypes();
		List<TemplateFileFormatDto> masterTemplateFileDto = masterSyncDto.getTemplateFileFormat();
		List<PostReasonCategoryDto> masterPostReasonCategoryDto = masterSyncDto.getReasonCategory();
		List<MasterReasonListDto> masterReasonListDto = masterSyncDto.getReasonList();
		List<BlacklistedWordsDto> masterBlackListedWordsDto = masterSyncDto.getBlackListedWords();
		List<LocationDto> masterLocationDto = masterSyncDto.getLocationHierarchy();
		List<BiometricAttributeDto> masterBiometricAttributeDto = masterSyncDto.getBiometricattributes();
		List<BiometricTypeDto> masterBiometricTypeDto = masterSyncDto.getBiometricTypes();
		List<ApplicationDto> masterApplicationDto = masterSyncDto.getApplications();
		List<IdTypeDto> masterIdTypeDto = masterSyncDto.getIdTypes();
		List<TitleDto> masterTitleDto = masterSyncDto.getTitles();
		List<GenderDto> masterGenderDto = masterSyncDto.getGenders();

		String sucessResponse = "";

		try {

			if (null != masterApplicationDto) {
				List<MasterApplication> masterApplicationDtoEntity = MetaDataUtils
						.setCreateMetaData(masterApplicationDto, MasterApplication.class);
				masterSyncApplicationRepository.saveAll(masterApplicationDtoEntity);
			}
			if (null != masterBiometricTypeDto) {
				List<MasterBiometricType> masterBiometricTypeDtoEntity = MetaDataUtils
						.setCreateMetaData(masterBiometricTypeDto, MasterBiometricType.class);
				masterSyncBiometricTypeRepository.saveAll(masterBiometricTypeDtoEntity);
			}
			if (null != masterBiometricAttributeDto) {
				List<MasterBiometricAttribute> masterBiometricAttributeDtoEntity = MetaDataUtils
						.setCreateMetaData(masterBiometricAttributeDto, MasterBiometricAttribute.class);
				masterSyncBiometricAttributeRepository.saveAll(masterBiometricAttributeDtoEntity);
			}
			if (null != masterBlackListedWordsDto) {
				List<MasterBlacklistedWords> blacklistedWordsEntity = MetaDataUtils
						.setCreateMetaData(masterBlackListedWordsDto, MasterBlacklistedWords.class);
				masterSyncBlacklistedWordsRepository.saveAll(blacklistedWordsEntity);
			}
			if (null != masterDeviceTypeDto) {
				List<MasterDeviceType> masterDeviceTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterDeviceTypeDto,
						MasterDeviceType.class);
				masterSyncDeviceTypeRepository.saveAll(masterDeviceTypeDtoEntity);
			}
			if (null != masterDeviceSpecificDto) {
				List<MasterDeviceSpecification> masterDeviceSpecificDtoEntity = MetaDataUtils
						.setCreateMetaData(masterDeviceSpecificDto, MasterDeviceSpecification.class);
				masterSyncDeviceSpecificationRepository.saveAll(masterDeviceSpecificDtoEntity);
			}
			if (null != masterDeviceDto) {
				List<MasterDevice> masterDeviceDtoEntity = MetaDataUtils.setCreateMetaData(masterDeviceDto,
						MasterDevice.class);
				masterSyncDeviceRepository.saveAll(masterDeviceDtoEntity);
			}
			if (null != masterDocumnetCategoryDto) {
				List<MasterDocumentCategory> masterDocumnetCategoryDtoEntity = MetaDataUtils
						.setCreateMetaData(masterDocumnetCategoryDto, MasterDocumentCategory.class);
				masterSyncDocumentCategoryRepository.saveAll(masterDocumnetCategoryDtoEntity);
			}
			if (null != masterDocumnetTypeDto) {
				List<MasterDocumentType> masterDocumnetTypeDtoEntity = MetaDataUtils
						.setCreateMetaData(masterDocumnetTypeDto, MasterDocumentType.class);
				masterSyncDocumentTypeRepository.saveAll(masterDocumnetTypeDtoEntity);
			}
			if (null != masterGenderDto) {
				List<MasterGender> masterGenderDtoEntity = MetaDataUtils.setCreateMetaData(masterGenderDto,
						MasterGender.class);
				masterSyncGenderRepository.saveAll(masterGenderDtoEntity);
			}
			if (null != masterHolidaysDto) {
				List<MasterHoliday> masterHolidaysDtoEntity = MetaDataUtils.setCreateMetaData(masterHolidaysDto,
						MasterHoliday.class);
				masterSyncHolidayRepository.saveAll(masterHolidaysDtoEntity);
			}
			if (null != masterIdTypeDto) {
				List<MasterIdType> masterIdTypeDtoEntity = MetaDataUtils.setCreateMetaData(masterIdTypeDto,
						MasterIdType.class);
				masterSyncIdTypeRepository.saveAll(masterIdTypeDtoEntity);
			}
			if (null != masterLocationDto) {
				List<MasterLocation> masterLocationDtoEntity = MetaDataUtils.setCreateMetaData(masterLocationDto,
						MasterLocation.class);
				masterSyncLocationRepository.saveAll(masterLocationDtoEntity);
			}
			if (null != masterMachineTypeDto) {
				List<MasterMachineType> masterMachineTypeDtoEntity = MetaDataUtils
						.setCreateMetaData(masterMachineTypeDto, MasterMachineType.class);
				masterSyncMachineTypeRepository.saveAll(masterMachineTypeDtoEntity);
			}
			if (null != masterMachineSpecDto) {
				List<MasterMachineSpecification> masterMachineSpecDtoEntity = MetaDataUtils
						.setCreateMetaData(masterMachineSpecDto, MasterMachineSpecification.class);
				masterSyncMachineSpecificationRepository.saveAll(masterMachineSpecDtoEntity);
			}
			if (null != masterMachineDto) {
				List<MasterMachine> masterMachineDtoEntity = MetaDataUtils.setCreateMetaData(masterMachineDto,
						MasterMachine.class);
				masterSyncMachineRepository.saveAll(masterMachineDtoEntity);
			}
			if (null != masterPostReasonCategoryDto) {
				List<MasterReasonCategory> masterReasonCategoryDtoEntity = MetaDataUtils
						.setCreateMetaData(masterPostReasonCategoryDto, MasterReasonCategory.class);
				masterSyncReasonCategoryRepository.saveAll(masterReasonCategoryDtoEntity);
			}
			if (null != masterReasonListDto) {
				List<MasterReasonList> masterReasonListDtoEntity = MetaDataUtils.setCreateMetaData(masterReasonListDto,
						MasterReasonList.class);
				masterSyncReasonListRepository.saveAll(masterReasonListDtoEntity);
			}
			if (null != regCenter) {
				List<MasterRegistrationCenter> registrationCenterEntity = MetaDataUtils.setCreateMetaData(regCenter,
						MasterRegistrationCenter.class);
				// masterSyncRegistrationCenterRepository.saveAll(registrationCenterEntity);
			}
			if (null != masterTemplateFileDto) {
				List<MasterTemplateFileFormat> masterTemplateFileDtoEntity = MetaDataUtils
						.setCreateMetaData(masterTemplateFileDto, MasterTemplateFileFormat.class);
				masterSyncTemplateFileFormatRepository.saveAll(masterTemplateFileDtoEntity);
			}
			if (null != masterTemplateDto) {
				List<MasterTemplate> masterTemplateDtoEntity = MetaDataUtils.setCreateMetaData(masterTemplateDto,
						MasterTemplate.class);
				masterSyncTemplateRepository.saveAll(masterTemplateDtoEntity);
			}
			if (null != masterTemplateTypeDto) {
				List<MasterTemplateType> masterTemplateTypeDtoEntity = MetaDataUtils
						.setCreateMetaData(masterTemplateTypeDto, MasterTemplateType.class);
				masterSyncTemplateTypeRepository.saveAll(masterTemplateTypeDtoEntity);
			}
			if (null != masterTitleDto) {
				List<MasterTitle> masterTitleDtoEntity = MetaDataUtils.setCreateMetaData(masterTitleDto,
						MasterTitle.class);
				//masterSyncTitleRepository.saveAll(masterTitleDtoEntity);
			}
			if (null != masterValidDocumnetsDto) {
				List<MasterValidDocument> masterValidDocumnetsDtoEntity = MetaDataUtils
						.setCreateMetaData(masterValidDocumnetsDto, MasterValidDocument.class);
				masterSyncValidDocumentRepository.saveAll(masterValidDocumnetsDtoEntity);
			}

		} catch (DataAccessException dataAccessException) {

			sucessResponse = RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO;
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION + sucessResponse,
					dataAccessException.getMessage());
		} catch (NullPointerException nullPointerException) {

			sucessResponse = RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO;
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION + sucessResponse,
					nullPointerException.getMessage());
		} catch (RegBaseUncheckedException regBaseUncheckedException) {

			sucessResponse = RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO;
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION + sucessResponse,
					regBaseUncheckedException.getMessage());
		} catch (RuntimeException runtimeException) {

			sucessResponse = RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO;
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION + sucessResponse,
					runtimeException.getMessage());
		}
		return sucessResponse;
	}

}
