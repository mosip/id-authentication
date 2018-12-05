package io.mosip.kernel.synchandler.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.synchandler.constant.MasterDataErrorCode;
import io.mosip.kernel.synchandler.dto.ApplicationDto;
import io.mosip.kernel.synchandler.dto.BiometricAttributeDto;
import io.mosip.kernel.synchandler.dto.BiometricTypeDto;
import io.mosip.kernel.synchandler.dto.BlacklistedWordsDto;
import io.mosip.kernel.synchandler.dto.DeviceDto;
import io.mosip.kernel.synchandler.dto.DeviceSpecificationDto;
import io.mosip.kernel.synchandler.dto.DeviceTypeDto;
import io.mosip.kernel.synchandler.dto.DocumentCategoryDto;
import io.mosip.kernel.synchandler.dto.DocumentTypeDto;
import io.mosip.kernel.synchandler.dto.GenderDto;
import io.mosip.kernel.synchandler.dto.HolidayDto;
import io.mosip.kernel.synchandler.dto.IdTypeDto;
import io.mosip.kernel.synchandler.dto.LanguageDto;
import io.mosip.kernel.synchandler.dto.LocationDto;
import io.mosip.kernel.synchandler.dto.MachineDto;
import io.mosip.kernel.synchandler.dto.MachineSpecificationDto;
import io.mosip.kernel.synchandler.dto.MachineTypeDto;
import io.mosip.kernel.synchandler.dto.PostReasonCategoryDto;
import io.mosip.kernel.synchandler.dto.ReasonListDto;
import io.mosip.kernel.synchandler.dto.RegistrationCenterDto;
import io.mosip.kernel.synchandler.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.synchandler.dto.TemplateDto;
import io.mosip.kernel.synchandler.dto.TemplateFileFormatDto;
import io.mosip.kernel.synchandler.dto.TemplateTypeDto;
import io.mosip.kernel.synchandler.dto.TitleDto;
import io.mosip.kernel.synchandler.dto.ValidDocumentDto;
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
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
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
import io.mosip.kernel.synchandler.utils.MapperUtils;

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

	public List<MachineDto> getMachines(String id, LocalDateTime lastUpdated) {
		List<Machine> machineDetailList = new ArrayList<>();
		List<MachineDto> machineDetailDtoList = null;
		try {
			if (lastUpdated != null)
				machineDetailList = machineRepository.findAllLatestCreatedUpdateDeleted(id, lastUpdated);
			else
				machineDetailList = machineRepository.findMachineById(id);

		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!machineDetailList.isEmpty()) {
			machineDetailDtoList = mapper.mapMachineListDto(machineDetailList);
		}

		return machineDetailDtoList;
	}

	public List<MachineTypeDto> getMachineType(String machineId, LocalDateTime lastUpdated) {
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
		return machineTypeList;

	}

	public List<MachineSpecificationDto> getMachineSpecification(String machineId, LocalDateTime lastUpdated) {
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

		return machineSpecificationDto;
	}

	public List<RegistrationCenterDto> getRegistrationCenter(String machineId, LocalDateTime lastUpdated) {
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

		return registrationCenterList;
	}

	public List<RegistrationCenterTypeDto> getRegistrationCenterType(String machineId, LocalDateTime lastUpdated) {
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

		return registrationCenterTypes;
	}

	public List<ApplicationDto> getApplications(LocalDateTime lastUpdated) {
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
		return applications;
	}

	public List<TemplateDto> getTemplates(LocalDateTime lastUpdated) {
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
		return templates;
	}

	public List<TemplateFileFormatDto> getTemplateFileFormats(LocalDateTime lastUpdated) {
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
		return templateFormats;
	}

	public List<PostReasonCategoryDto> getReasonCategory(LocalDateTime lastUpdated) {
		List<PostReasonCategoryDto> reasonCategories = null;
		List<ReasonCategory> reasons = null;
		try {
			if (lastUpdated != null) {
				reasons = reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(lastUpdated);
			} else {
				reasons = reasonCategoryRepository.findAll();
			}
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.REASON_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (reasons != null && !reasons.isEmpty()) {
			reasonCategories = mapper.mapAll(reasons, PostReasonCategoryDto.class);
		}
		return reasonCategories;
	}

	public List<ReasonListDto> getReasonList(LocalDateTime lastUpdated) {
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

		return reasonList;
	}

	public List<HolidayDto> getHolidays(LocalDateTime lastUpdated, String machineId) {
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

		return holidayList;
	}

	public List<BlacklistedWordsDto> getBlackListedWords(LocalDateTime lastUpdated) {
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

		return blacklistedWords;
	}

	public List<BiometricTypeDto> getBiometricTypes(LocalDateTime lastUpdated) {
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
		return biometricTypeDtoList;
	}

	public List<BiometricAttributeDto> getBiometricAttributes(LocalDateTime lastUpdated) {
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
		return biometricAttrList;
	}

	public List<TitleDto> getTitles(LocalDateTime lastUpdated) {
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
		return titleList;

	}

	public List<LanguageDto> getLanguages(LocalDateTime lastUpdated) {
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
		return languageList;
	}

	public List<GenderDto> getGenders(LocalDateTime lastUpdated) {
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
		return genderDto;
	}

	public List<DeviceDto> getDevices(String machineId, LocalDateTime lastUpdated) {
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
		return deviceList;
	}

	public List<DocumentCategoryDto> getDocumentCategories(LocalDateTime lastUpdated) {
		List<DocumentCategoryDto> ducumentCategoryList = null;
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
			ducumentCategoryList = mapper.mapAll(documentCategories, DocumentCategoryDto.class);

		return ducumentCategoryList;
	}

	public List<DocumentTypeDto> getDocumentTypes(LocalDateTime lastUpdated) {
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

		return documentTypeList;
	}

	public List<IdTypeDto> getIdTypes(LocalDateTime lastUpdated) {
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
		return idTypeList;
	}

	public List<DeviceSpecificationDto> getDeviceSpecifications(String machineId, LocalDateTime lastUpdated) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			if (lastUpdated != null)
				deviceSpecificationList = deviceSpecificationRepository.findlatestDeviceTypeByMachineId(machineId,
						lastUpdated);
			else
				deviceSpecificationList = deviceSpecificationRepository.findAll();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					MasterDataErrorCode.DEVICE_SPECIFICATION_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty())
			deviceSpecificationDtoList = mapper.mapDeviceSpecification(deviceSpecificationList);
		return deviceSpecificationDtoList;

	}

	public List<LocationDto> getLocationHierarchy(LocalDateTime lastUpdated) {
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
		return responseList;
	}

	public List<TemplateTypeDto> getTemplateTypes(LocalDateTime lastUpdated) {
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

		return templateTypeList;
	}

	public List<DeviceTypeDto> getDeviceType(String machineId, LocalDateTime lastUpdated) {
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
		return deviceTypeList;
	}

	public List<ValidDocumentDto> getValidDocuments(LocalDateTime lastUpdated) {
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
		return validDocumentList;
	}
}
