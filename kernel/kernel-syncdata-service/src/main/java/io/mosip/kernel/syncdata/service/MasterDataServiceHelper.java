package io.mosip.kernel.syncdata.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.dto.ApplicationDto;
import io.mosip.kernel.syncdata.dto.BiometricAttributeDto;
import io.mosip.kernel.syncdata.dto.BiometricTypeDto;
import io.mosip.kernel.syncdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.syncdata.dto.DeviceDto;
import io.mosip.kernel.syncdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.syncdata.dto.DeviceTypeDto;
import io.mosip.kernel.syncdata.dto.DocumentCategoryDto;
import io.mosip.kernel.syncdata.dto.DocumentTypeDto;
import io.mosip.kernel.syncdata.dto.GenderDto;
import io.mosip.kernel.syncdata.dto.HolidayDto;
import io.mosip.kernel.syncdata.dto.IdTypeDto;
import io.mosip.kernel.syncdata.dto.LanguageDto;
import io.mosip.kernel.syncdata.dto.LocationDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.syncdata.dto.ReasonListDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.syncdata.dto.TemplateDto;
import io.mosip.kernel.syncdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.syncdata.dto.TemplateTypeDto;
import io.mosip.kernel.syncdata.dto.TitleDto;
import io.mosip.kernel.syncdata.dto.ValidDocumentDto;
import io.mosip.kernel.syncdata.entity.Application;
import io.mosip.kernel.syncdata.entity.BiometricAttribute;
import io.mosip.kernel.syncdata.entity.BiometricType;
import io.mosip.kernel.syncdata.entity.BlacklistedWords;
import io.mosip.kernel.syncdata.entity.Device;
import io.mosip.kernel.syncdata.entity.DeviceSpecification;
import io.mosip.kernel.syncdata.entity.DeviceType;
import io.mosip.kernel.syncdata.entity.DocumentCategory;
import io.mosip.kernel.syncdata.entity.DocumentType;
import io.mosip.kernel.syncdata.entity.Gender;
import io.mosip.kernel.syncdata.entity.Holiday;
import io.mosip.kernel.syncdata.entity.IdType;
import io.mosip.kernel.syncdata.entity.Language;
import io.mosip.kernel.syncdata.entity.Location;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.entity.MachineSpecification;
import io.mosip.kernel.syncdata.entity.MachineType;
import io.mosip.kernel.syncdata.entity.ReasonCategory;
import io.mosip.kernel.syncdata.entity.ReasonList;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;
import io.mosip.kernel.syncdata.entity.RegistrationCenterType;
import io.mosip.kernel.syncdata.entity.Template;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;
import io.mosip.kernel.syncdata.entity.TemplateType;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.ValidDocument;
import io.mosip.kernel.syncdata.exception.MasterDataServiceException;
import io.mosip.kernel.syncdata.repository.ApplicationRepository;
import io.mosip.kernel.syncdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.syncdata.repository.BiometricTypeRepository;
import io.mosip.kernel.syncdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.syncdata.repository.DeviceRepository;
import io.mosip.kernel.syncdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.syncdata.repository.DeviceTypeRepository;
import io.mosip.kernel.syncdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.syncdata.repository.DocumentTypeRepository;
import io.mosip.kernel.syncdata.repository.GenderRepository;
import io.mosip.kernel.syncdata.repository.HolidayRepository;
import io.mosip.kernel.syncdata.repository.IdTypeRepository;
import io.mosip.kernel.syncdata.repository.LanguageRepository;
import io.mosip.kernel.syncdata.repository.LocationRepository;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.syncdata.repository.MachineTypeRepository;
import io.mosip.kernel.syncdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.syncdata.repository.ReasonListRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.syncdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.syncdata.repository.TemplateRepository;
import io.mosip.kernel.syncdata.repository.TemplateTypeRepository;
import io.mosip.kernel.syncdata.repository.TitleRepository;
import io.mosip.kernel.syncdata.repository.ValidDocumentRepository;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * Sync handler masterData service helper
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Component
public class MasterDataServiceHelper {
	@Autowired
	private MapperUtils mapper;
	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private MachineRepository machineRepository;
	@Autowired
	private MachineTypeRepository machineTypeRepository;
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;
	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TemplateFileFormatRepository templateFileFormatRepository;
	@Autowired
	private ReasonCategoryRepository reasonCategoryRepository;
	@Autowired
	private HolidayRepository holidayRepository;
	@Autowired
	private BlacklistedWordsRepository blacklistedWordsRepository;
	@Autowired
	private BiometricTypeRepository biometricTypeRepository;
	@Autowired
	private BiometricAttributeRepository biometricAttributeRepository;
	@Autowired
	private TitleRepository titleRepository;
	@Autowired
	private LanguageRepository languageRepository;
	@Autowired
	private GenderRepository genderTypeRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	@Autowired
	private IdTypeRepository idTypeRepository;
	@Autowired
	private DeviceSpecificationRepository deviceSpecificationRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private TemplateTypeRepository templateTypeRepository;
	@Autowired
	private MachineSpecificationRepository machineSpecificationRepository;
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;
	@Autowired
	private ValidDocumentRepository validDocumentRepository;
	@Autowired
	private ReasonListRepository reasonListRepository;

	/**
	 * Method to fetch machine details by machine id
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated time-stamp
	 * @return list of {@link MachineDto}
	 */
	@Async
	public CompletableFuture<List<MachineDto>> getMachines(String machineId, LocalDateTime lastUpdated) {
		List<Machine> machineDetailList = new ArrayList<>();
		List<MachineDto> machineDetailDtoList = null;
		try {
			if (lastUpdated != null)
				machineDetailList = machineRepository.findAllLatestCreatedUpdateDeleted(machineId, lastUpdated);
			else
				machineDetailList = machineRepository.findMachineById(machineId);

		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!machineDetailList.isEmpty()) {
			machineDetailDtoList = mapper.mapMachineListDto(machineDetailList);
		}

		return CompletableFuture.completedFuture(machineDetailDtoList);
	}

	/**
	 * Method to fetch machine type
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastupdated timestamp
	 * @return list of {@link MachineType}
	 */
	@Async
	public CompletableFuture<List<MachineTypeDto>> getMachineType(String machineId, LocalDateTime lastUpdated) {
		List<MachineTypeDto> machineTypeList = null;
		List<MachineType> machineTypes = null;
		try {
			if (lastUpdated != null)
				machineTypes = machineTypeRepository.findLatestByMachineId(machineId, lastUpdated);
			else
				machineTypes = machineTypeRepository.findAllByMachineId(machineId);

		} catch (

		DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.MACHINE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (machineTypes != null && !machineTypes.isEmpty())
			machineTypeList = mapper.mapMachineType(machineTypes);
		return CompletableFuture.completedFuture(machineTypeList);

	}

	/**
	 * Method to fetch machine specification
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastupdated timestamp
	 * @return list of {@link MachineSpecificationDto}
	 */
	@Async
	public CompletableFuture<List<MachineSpecificationDto>> getMachineSpecification(String machineId,
			LocalDateTime lastUpdated) {
		List<MachineSpecification> machineSpecification = null;
		List<MachineSpecificationDto> machineSpecificationDto = null;

		try {
			if (machineId != null) {
				if (lastUpdated != null)
					machineSpecification = machineSpecificationRepository.findLatestByMachineId(machineId, lastUpdated);
				else
					machineSpecification = machineSpecificationRepository.findByMachineId(machineId);
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (machineSpecification != null && !machineSpecification.isEmpty())
			machineSpecificationDto = mapper.mapMachineSpecification(machineSpecification);

		return CompletableFuture.completedFuture(machineSpecificationDto);
	}

	/**
	 * Method to fetch registration center detail
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link RegistrationCenterDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterDto>> getRegistrationCenter(String machineId,
			LocalDateTime lastUpdated) {
		List<RegistrationCenterDto> registrationCenterList = new ArrayList<>();
		List<RegistrationCenter> list = null;
		try {
			if (lastUpdated != null)
				list = registrationCenterRepository.findLatestRegistrationCenterByMachineId(machineId, lastUpdated);
			else
				list = registrationCenterRepository.findRegistrationCenterByMachineId(machineId);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (list != null && !list.isEmpty()) {
			registrationCenterList = mapper.mapRegistrationCenter(list);
		}

		return CompletableFuture.completedFuture(registrationCenterList);
	}

	/**
	 * Method to fetch registration center type
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link RegistrationCenterTypeDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterTypeDto>> getRegistrationCenterType(String machineId,
			LocalDateTime lastUpdated) {
		List<RegistrationCenterTypeDto> registrationCenterTypes = new ArrayList<>();
		List<RegistrationCenterType> registrationCenterType = null;
		try {
			if (lastUpdated != null)
				registrationCenterType = registrationCenterTypeRepository
						.findLatestRegistrationCenterTypeByMachineId(machineId, lastUpdated);
			else
				registrationCenterType = registrationCenterTypeRepository
						.findRegistrationCenterTypeByMachineId(machineId);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.REG_CENTER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (registrationCenterType != null && !registrationCenterType.isEmpty())
			registrationCenterTypes = mapper.mapAll(registrationCenterType, RegistrationCenterTypeDto.class);

		return CompletableFuture.completedFuture(registrationCenterTypes);
	}

	/**
	 * Method to fetch applications
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link ApplicationDto}
	 */
	@Async
	public CompletableFuture<List<ApplicationDto>> getApplications(LocalDateTime lastUpdated) {
		List<ApplicationDto> applications = null;
		List<Application> applicationList = null;
		try {
			if (lastUpdated != null)
				applicationList = applicationRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				applicationList = applicationRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(applicationList.isEmpty())) {
			applications = mapper.mapAll(applicationList, ApplicationDto.class);
		}
		return CompletableFuture.completedFuture(applications);
	}

	/**
	 * Method to fetch templates
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link TemplateDto}
	 */
	@Async
	public CompletableFuture<List<TemplateDto>> getTemplates(LocalDateTime lastUpdated) {
		List<TemplateDto> templates = null;
		List<Template> templateList = null;
		try {
			if (lastUpdated != null) {
				templateList = templateRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			} else {
				templateList = templateRepository.findAll();
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (templateList != null && !templateList.isEmpty()) {
			templates = mapper.mapAll(templateList, TemplateDto.class);
		}
		return CompletableFuture.completedFuture(templates);
	}

	/**
	 * Method to fetch template format types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link TemplateFileFormatDto}
	 */
	@Async
	public CompletableFuture<List<TemplateFileFormatDto>> getTemplateFileFormats(LocalDateTime lastUpdated) {
		List<TemplateFileFormatDto> templateFormats = null;
		List<TemplateFileFormat> templateTypes = null;
		try {
			if (lastUpdated != null) {
				templateTypes = templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			} else {
				templateTypes = templateFileFormatRepository.findAllTemplateFormat();
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.TEMPLATE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		templateFormats = mapper.mapAll(templateTypes, TemplateFileFormatDto.class);
		return CompletableFuture.completedFuture(templateFormats);
	}

	/**
	 * Method to fetch reason-category
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link PostReasonCategoryDto}
	 */
	@Async
	public CompletableFuture<List<PostReasonCategoryDto>> getReasonCategory(LocalDateTime lastUpdated) {
		List<PostReasonCategoryDto> reasonCategories = null;
		List<ReasonCategory> reasons = null;
		try {
			if (lastUpdated != null) {
				reasons = reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			} else {
				reasons = reasonCategoryRepository.findAllReasons();
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.REASON_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (reasons != null && !reasons.isEmpty()) {
			reasonCategories = mapper.mapAll(reasons, PostReasonCategoryDto.class);
		}
		return CompletableFuture.completedFuture(reasonCategories);
	}

	/**
	 * Method to fetch Reason List
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link ReasonListDto}
	 */
	@Async
	public CompletableFuture<List<ReasonListDto>> getReasonList(LocalDateTime lastUpdated) {
		List<ReasonListDto> reasonList = null;
		List<ReasonList> reasons = null;
		try {
			if (lastUpdated != null)
				reasons = reasonListRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				reasons = reasonListRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.REASON_LIST_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (reasons != null && !reasons.isEmpty())
			reasonList = mapper.mapAll(reasons, ReasonListDto.class);

		return CompletableFuture.completedFuture(reasonList);
	}

	/**
	 * Method to fetch Holidays
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param machineId
	 *            machine id
	 * @return list of {@link HolidayDto}
	 */
	@Async
	public CompletableFuture<List<HolidayDto>> getHolidays(LocalDateTime lastUpdated, String machineId) {
		List<HolidayDto> holidayList = null;
		List<Holiday> holidays = null;
		try {
			if (lastUpdated != null) {
				holidays = holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(machineId, lastUpdated);
			} else {
				holidays = holidayRepository.findAllByMachineId(machineId);
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (holidays != null && !holidays.isEmpty())
			holidayList = mapper.mapHolidays(holidays);

		return CompletableFuture.completedFuture(holidayList);
	}

	/**
	 * Method to fetch blacklisted words
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link BlacklistedWordsDto}
	 */
	@Async
	public CompletableFuture<List<BlacklistedWordsDto>> getBlackListedWords(LocalDateTime lastUpdated) {
		List<BlacklistedWordsDto> blacklistedWords = null;
		List<BlacklistedWords> words = null;

		try {
			if (lastUpdated != null) {
				words = blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			} else {
				words = blacklistedWordsRepository.findAll();
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (words != null && !words.isEmpty()) {
			blacklistedWords = mapper.mapAll(words, BlacklistedWordsDto.class);
		}

		return CompletableFuture.completedFuture(blacklistedWords);
	}

	/**
	 * Method to fetch biometric types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link BiometricTypeDto}
	 */
	@Async
	public CompletableFuture<List<BiometricTypeDto>> getBiometricTypes(LocalDateTime lastUpdated) {
		List<BiometricTypeDto> biometricTypeDtoList = null;
		List<BiometricType> biometricTypesList = null;
		try {
			if (lastUpdated != null)
				biometricTypesList = biometricTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				biometricTypesList = biometricTypeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypeDtoList = mapper.mapAll(biometricTypesList, BiometricTypeDto.class);
		}
		return CompletableFuture.completedFuture(biometricTypeDtoList);
	}

	/**
	 * Method to fetch biometric attributes
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link BiometricAttributeDto}
	 */
	@Async
	public CompletableFuture<List<BiometricAttributeDto>> getBiometricAttributes(LocalDateTime lastUpdated) {
		List<BiometricAttributeDto> biometricAttrList = null;
		List<BiometricAttribute> biometricAttrs = null;
		try {

			if (lastUpdated != null)
				biometricAttrs = biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				biometricAttrs = biometricAttributeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.BIOMETRIC_ATTR_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (biometricAttrs != null && !biometricAttrs.isEmpty()) {
			biometricAttrList = mapper.mapAll(biometricAttrs, BiometricAttributeDto.class);
		}
		return CompletableFuture.completedFuture(biometricAttrList);
	}

	/**
	 * Method to fetch titles
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link TitleDto}
	 */
	@Async
	public CompletableFuture<List<TitleDto>> getTitles(LocalDateTime lastUpdated) {
		List<TitleDto> titleList = null;
		List<Title> titles = null;
		try {
			if (lastUpdated != null)
				titles = titleRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				titles = titleRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (titles != null && !titles.isEmpty()) {
			titleList = mapper.maptitles(titles);
		}
		return CompletableFuture.completedFuture(titleList);

	}

	/**
	 * Method to fetch languages
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link LanguageDto}
	 */
	@Async
	public CompletableFuture<List<LanguageDto>> getLanguages(LocalDateTime lastUpdated) {
		List<LanguageDto> languageList = null;
		List<Language> languages = null;
		try {
			if (lastUpdated != null)
				languages = languageRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				languages = languageRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (languages != null && !languages.isEmpty()) {
			languageList = mapper.mapAll(languages, LanguageDto.class);
		}
		return CompletableFuture.completedFuture(languageList);
	}

	/**
	 * Method to fetch genders
	 * 
	 * @param lastUpdated
	 *            lastUpdated
	 * @return list of {@link GenderDto}
	 */
	@Async
	public CompletableFuture<List<GenderDto>> getGenders(LocalDateTime lastUpdated) {
		List<GenderDto> genderDto = null;
		List<Gender> genderType = null;

		try {
			if (lastUpdated != null)
				genderType = genderTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				genderType = genderTypeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.GENDER_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(genderType.isEmpty())) {
			genderDto = mapper.mapAll(genderType, GenderDto.class);
		}
		return CompletableFuture.completedFuture(genderDto);
	}

	/**
	 * Method to fetch devices
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link DeviceDto}
	 */
	@Async
	public CompletableFuture<List<DeviceDto>> getDevices(String machineId, LocalDateTime lastUpdated) {
		List<Device> devices = null;
		List<DeviceDto> deviceList = null;
		try {
			if (lastUpdated != null)
				devices = deviceRepository.findLatestDevicesByMachineId(machineId, lastUpdated);
			else
				devices = deviceRepository.findDeviceByMachineId(machineId);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.DEVICES_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (devices != null && !devices.isEmpty())
			deviceList = mapper.mapAll(devices, DeviceDto.class);
		return CompletableFuture.completedFuture(deviceList);
	}

	/**
	 * Method to fetch document category
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link DocumentCategoryDto}
	 */
	@Async
	public CompletableFuture<List<DocumentCategoryDto>> getDocumentCategories(LocalDateTime lastUpdated) {
		List<DocumentCategoryDto> documentCategoryList = null;
		List<DocumentCategory> documentCategories = null;
		try {
			if (lastUpdated != null)
				documentCategories = documentCategoryRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				documentCategories = documentCategoryRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (documentCategories != null && !documentCategories.isEmpty())
			documentCategoryList = mapper.mapAll(documentCategories, DocumentCategoryDto.class);

		return CompletableFuture.completedFuture(documentCategoryList);
	}

	/**
	 * Method to fetch document type
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link DocumentTypeDto}
	 */
	@Async
	public CompletableFuture<List<DocumentTypeDto>> getDocumentTypes(LocalDateTime lastUpdated) {
		List<DocumentTypeDto> documentTypeList = null;
		List<DocumentType> documentTypes = null;
		try {
			if (lastUpdated != null)
				documentTypes = documentTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				documentTypes = documentTypeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (documentTypes != null && !documentTypes.isEmpty())
			documentTypeList = mapper.mapAll(documentTypes, DocumentTypeDto.class);

		return CompletableFuture.completedFuture(documentTypeList);
	}

	/**
	 * Method to fetch id types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link IdTypeDto}
	 */
	@Async
	public CompletableFuture<List<IdTypeDto>> getIdTypes(LocalDateTime lastUpdated) {
		List<IdTypeDto> idTypeList = null;
		List<IdType> idTypes = null;
		try {
			if (lastUpdated != null)
				idTypes = idTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				idTypes = idTypeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (idTypes != null && !idTypes.isEmpty())
			idTypeList = mapper.mapAll(idTypes, IdTypeDto.class);
		return CompletableFuture.completedFuture(idTypeList);
	}

	/**
	 * Method to fetch device specification
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link DeviceSpecificationDto}}
	 */
	@Async
	public CompletableFuture<List<DeviceSpecificationDto>> getDeviceSpecifications(String machineId,
			LocalDateTime lastUpdated) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			if (lastUpdated != null)
				deviceSpecificationList = deviceSpecificationRepository.findLatestDeviceTypeByMachineId(machineId,
						lastUpdated);
			else
				deviceSpecificationList = deviceSpecificationRepository.findDeviceTypeByMachineId(machineId);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					MasterDataErrorCode.DEVICE_SPECIFICATION_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty())
			deviceSpecificationDtoList = mapper.mapDeviceSpecification(deviceSpecificationList);
		return CompletableFuture.completedFuture(deviceSpecificationDtoList);

	}

	/**
	 * Method to fetch locations
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link LocationDto}
	 */
	@Async
	public CompletableFuture<List<LocationDto>> getLocationHierarchy(LocalDateTime lastUpdated) {
		List<LocationDto> responseList = null;
		List<Location> locations = null;
		try {
			if (lastUpdated != null)
				locations = locationRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				locations = locationRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!locations.isEmpty()) {
			responseList = mapper.mapAll(locations, LocationDto.class);
		}
		return CompletableFuture.completedFuture(responseList);
	}

	/**
	 * Method to fetch template types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link TemplateTypeDto}
	 */
	@Async
	public CompletableFuture<List<TemplateTypeDto>> getTemplateTypes(LocalDateTime lastUpdated) {
		List<TemplateTypeDto> templateTypeList = null;
		List<TemplateType> templateTypes = null;
		try {
			if (lastUpdated != null)
				templateTypes = templateTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				templateTypes = templateTypeRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.TEMPLATE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (templateTypes != null && !templateTypes.isEmpty())
			templateTypeList = mapper.mapAll(templateTypes, TemplateTypeDto.class);

		return CompletableFuture.completedFuture(templateTypeList);
	}

	/**
	 * Method to fetch device type
	 * 
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link DeviceTypeDto}
	 */
	@Async
	public CompletableFuture<List<DeviceTypeDto>> getDeviceType(String machineId, LocalDateTime lastUpdated) {
		List<DeviceTypeDto> deviceTypeList = null;
		List<DeviceType> deviceTypes = null;
		try {
			if (lastUpdated != null)
				deviceTypes = deviceTypeRepository.findLatestDeviceTypeByMachineId(machineId, lastUpdated);
			else
				deviceTypes = deviceTypeRepository.findDeviceTypeByMachineId(machineId);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (deviceTypes != null && !deviceTypes.isEmpty()) {
			deviceTypeList = mapper.mapAll(deviceTypes, DeviceTypeDto.class);
		}
		return CompletableFuture.completedFuture(deviceTypeList);
	}

	/**
	 * Method to fetch document mapping
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @return list of {@link ValidDocumentDto}
	 */
	@Async
	public CompletableFuture<List<ValidDocumentDto>> getValidDocuments(LocalDateTime lastUpdated) {
		List<ValidDocumentDto> validDocumentList = null;
		List<ValidDocument> validDocuments = null;
		try {
			if (lastUpdated != null)
				validDocuments = validDocumentRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			else
				validDocuments = validDocumentRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}

		if (validDocuments != null && !validDocuments.isEmpty()) {
			validDocumentList = mapper.mapAll(validDocuments, ValidDocumentDto.class);
		}
		return CompletableFuture.completedFuture(validDocumentList);
	}
}
