package io.mosip.kernel.syncdata.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.dto.AppAuthenticationMethodDto;
import io.mosip.kernel.syncdata.dto.AppDetailDto;
import io.mosip.kernel.syncdata.dto.AppRolePriorityDto;
import io.mosip.kernel.syncdata.dto.ApplicantValidDocumentDto;
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
import io.mosip.kernel.syncdata.dto.IndividualTypeDto;
import io.mosip.kernel.syncdata.dto.LanguageDto;
import io.mosip.kernel.syncdata.dto.LocationDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.syncdata.dto.ProcessListDto;
import io.mosip.kernel.syncdata.dto.ReasonListDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDeviceHistoryDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterMachineHistoryDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserHistoryDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.kernel.syncdata.dto.ScreenAuthorizationDto;
import io.mosip.kernel.syncdata.dto.ScreenDetailDto;
import io.mosip.kernel.syncdata.dto.SyncJobDefDto;
import io.mosip.kernel.syncdata.dto.TemplateDto;
import io.mosip.kernel.syncdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.syncdata.dto.TemplateTypeDto;
import io.mosip.kernel.syncdata.dto.TitleDto;
import io.mosip.kernel.syncdata.dto.ValidDocumentDto;
import io.mosip.kernel.syncdata.entity.AppAuthenticationMethod;
import io.mosip.kernel.syncdata.entity.AppDetail;
import io.mosip.kernel.syncdata.entity.AppRolePriority;
import io.mosip.kernel.syncdata.entity.ApplicantValidDocument;
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
import io.mosip.kernel.syncdata.entity.IndividualType;
import io.mosip.kernel.syncdata.entity.Language;
import io.mosip.kernel.syncdata.entity.Location;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.entity.MachineSpecification;
import io.mosip.kernel.syncdata.entity.MachineType;
import io.mosip.kernel.syncdata.entity.ProcessList;
import io.mosip.kernel.syncdata.entity.ReasonCategory;
import io.mosip.kernel.syncdata.entity.ReasonList;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterType;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.syncdata.entity.ScreenAuthorization;
import io.mosip.kernel.syncdata.entity.ScreenDetail;
import io.mosip.kernel.syncdata.entity.Template;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;
import io.mosip.kernel.syncdata.entity.TemplateType;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.ValidDocument;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.repository.AppAuthenticationMethodRepository;
import io.mosip.kernel.syncdata.repository.AppDetailRepository;
import io.mosip.kernel.syncdata.repository.AppRolePriorityRepository;
import io.mosip.kernel.syncdata.repository.ApplicantValidDocumentRespository;
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
import io.mosip.kernel.syncdata.repository.IndividualTypeRepository;
import io.mosip.kernel.syncdata.repository.LanguageRepository;
import io.mosip.kernel.syncdata.repository.LocationRepository;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.syncdata.repository.MachineTypeRepository;
import io.mosip.kernel.syncdata.repository.ProcessListRepository;
import io.mosip.kernel.syncdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.syncdata.repository.ReasonListRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserMachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.repository.ScreenAuthorizationRepository;
import io.mosip.kernel.syncdata.repository.ScreenDetailRepository;
import io.mosip.kernel.syncdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.syncdata.repository.TemplateRepository;
import io.mosip.kernel.syncdata.repository.TemplateTypeRepository;
import io.mosip.kernel.syncdata.repository.TitleRepository;
import io.mosip.kernel.syncdata.repository.ValidDocumentRepository;
import io.mosip.kernel.syncdata.service.SyncJobDefService;

/**
 * Sync handler masterData service helper
 * 
 * @author Abhishek Kumar
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class SyncMasterDataServiceHelper {

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
	@Autowired
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	@Autowired
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;
	@Autowired
	private RegistrationCenterUserMachineRepository registrationCenterUserMachineRepository;
	@Autowired
	private RegistrationCenterUserRepository registrationCenterUserRepository;
	@Autowired
	private RegistrationCenterUserHistoryRepository registrationCenterUserHistoryRepository;
	@Autowired
	private RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;
	@Autowired
	private RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepository;
	@Autowired
	private RegistrationCenterDeviceHistoryRepository registrationCenterDeviceHistoryRepository;
	@Autowired
	private RegistrationCenterMachineHistoryRepository registrationCenterMachineHistoryRepository;
	@Autowired
	private ApplicantValidDocumentRespository applicantValidDocumentRepository;
	@Autowired
	private IndividualTypeRepository individualTypeRepository;
	@Autowired
	private AppAuthenticationMethodRepository appAuthenticationMethodRepository;
	@Autowired
	private AppDetailRepository appDetailRepository;
	@Autowired
	private AppRolePriorityRepository appRolePriorityRepository;
	@Autowired
	private ScreenAuthorizationRepository screenAuthorizationRepository;
	@Autowired
	private ProcessListRepository processListRepository;
	@Autowired
	private ScreenDetailRepository screenDetailRepository;
	@Autowired
	private SyncJobDefService syncJobDefService;

	/**
	 * Method to fetch machine details by regCenter id
	 * 
	 * @param regCenterId
	 *            registration center id
	 * @param lastUpdated
	 *            lastUpdated time-stamp
	 * @param currentTimeStamp
	 *            current time stamp
	 * 
	 * @return list of {@link MachineDto} list of machine dto
	 */
	@Async
	public CompletableFuture<List<MachineDto>> getMachines(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<Machine> machineDetailList = new ArrayList<>();
		List<MachineDto> machineDetailDtoList = new ArrayList<>();
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			machineDetailList = machineRepository.findAllLatestCreatedUpdateDeleted(regCenterId, lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (!machineDetailList.isEmpty()) {
			
			//machineDetailDtoList = MapperUtils.mapAll(machineDetailList, MachineDto.class);
          machineDetailList.forEach(machine->{
        	  MachineDto responseDto=new MachineDto();
        	  responseDto.setPublicKey(CryptoUtil.encodeBase64(machine.getPublicKey()));
        	  responseDto.setId(machine.getId());
        	  responseDto.setIpAddress(machine.getIpAddress());
        	  responseDto.setIsActive(machine.getIsActive());
        	  responseDto.setIsDeleted(machine.getIsDeleted());
        	  responseDto.setKeyIndex(machine.getKeyIndex());
        	  responseDto.setLangCode(machine.getLangCode());
        	  responseDto.setMacAddress(machine.getMacAddress());
        	  responseDto.setMachineSpecId(machine.getMachineSpecId());
              responseDto.setName(machine.getName());
              responseDto.setSerialNum(machine.getSerialNum());
              responseDto.setValidityDateTime(machine.getValidityDateTime());
              machineDetailDtoList.add(responseDto);
          });
			
		}

		return CompletableFuture.completedFuture(machineDetailDtoList);
	}

	/**
	 * Method to fetch machine type
	 * 
	 * @param regCenterId
	 *            registration center id
	 * @param lastUpdated
	 *            lastupdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link MachineType}
	 */
	@Async
	public CompletableFuture<List<MachineTypeDto>> getMachineType(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<MachineTypeDto> machineTypeList = null;
		List<MachineType> machineTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			machineTypes = machineTypeRepository.findLatestByRegCenterId(regCenterId, lastUpdated, currentTimeStamp);

		} catch (

		DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.MACHINE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (machineTypes != null && !machineTypes.isEmpty())

			machineTypeList = MapperUtils.mapAll(machineTypes, MachineTypeDto.class);

		return CompletableFuture.completedFuture(machineTypeList);

	}

	/**
	 * Method to fetch machine specification
	 * 
	 * @param regCenterId
	 *            registration center id
	 * @param lastUpdated
	 *            lastupdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link MachineSpecificationDto}
	 */
	@Async
	public CompletableFuture<List<MachineSpecificationDto>> getMachineSpecification(String regCenterId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<MachineSpecification> machineSpecification = null;
		List<MachineSpecificationDto> machineSpecificationDto = null;

		try {
			if (regCenterId != null) {
				if (lastUpdated == null) {
					lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
				}
				machineSpecification = machineSpecificationRepository.findLatestByRegCenterId(regCenterId, lastUpdated,
						currentTimeStamp);

			}
		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (machineSpecification != null && !machineSpecification.isEmpty())

			machineSpecificationDto = MapperUtils.mapAll(machineSpecification, MachineSpecificationDto.class);

		return CompletableFuture.completedFuture(machineSpecificationDto);
	}

	/**
	 * Method to fetch registration center detail.
	 *
	 * @param machineId
	 *            machine id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            the current time stamp
	 * @return list of {@link RegistrationCenterDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterDto>> getRegistrationCenter(String machineId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterDto> registrationCenterList = null;
		List<RegistrationCenter> list = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			list = registrationCenterRepository.findLatestRegistrationCenterByMachineId(machineId, lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (list != null && !list.isEmpty()) {
			registrationCenterList = MapperUtils.mapAll(list, RegistrationCenterDto.class);
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
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterTypeDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterTypeDto>> getRegistrationCenterType(String machineId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterTypeDto> registrationCenterTypes = null;
		List<RegistrationCenterType> registrationCenterType = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterType = registrationCenterTypeRepository
					.findLatestRegistrationCenterTypeByMachineId(machineId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REG_CENTER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (registrationCenterType != null && !registrationCenterType.isEmpty())
			registrationCenterTypes = MapperUtils.mapAll(registrationCenterType, RegistrationCenterTypeDto.class);

		return CompletableFuture.completedFuture(registrationCenterTypes);
	}

	/**
	 * Method to fetch applications
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link ApplicationDto}
	 */
	@Async
	public CompletableFuture<List<ApplicationDto>> getApplications(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<ApplicationDto> applications = null;
		List<Application> applicationList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			applicationList = applicationRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (!(applicationList.isEmpty())) {
			applications = MapperUtils.mapAll(applicationList, ApplicationDto.class);
		}
		return CompletableFuture.completedFuture(applications);
	}

	/**
	 * Method to fetch templates
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link TemplateDto}
	 */
	@Async
	public CompletableFuture<List<TemplateDto>> getTemplates(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<TemplateDto> templates = null;
		List<Template> templateList = null;
		try {

			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			templateList = templateRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (templateList != null && !templateList.isEmpty()) {
			templates = MapperUtils.mapAll(templateList, TemplateDto.class);
		}
		return CompletableFuture.completedFuture(templates);
	}

	/**
	 * Method to fetch template format types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link TemplateFileFormatDto}
	 */
	@Async
	public CompletableFuture<List<TemplateFileFormatDto>> getTemplateFileFormats(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<TemplateFileFormatDto> templateFormats = null;
		List<TemplateFileFormat> templateTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			templateTypes = templateFileFormatRepository.findAllLatestCreatedUpdateDeleted(lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.TEMPLATE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		templateFormats = MapperUtils.mapAll(templateTypes, TemplateFileFormatDto.class);
		return CompletableFuture.completedFuture(templateFormats);
	}

	/**
	 * Method to fetch reason-category
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link PostReasonCategoryDto}
	 */
	@Async
	public CompletableFuture<List<PostReasonCategoryDto>> getReasonCategory(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<PostReasonCategoryDto> reasonCategories = null;
		List<ReasonCategory> reasons = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			reasons = reasonCategoryRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REASON_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (reasons != null && !reasons.isEmpty()) {
			reasonCategories = MapperUtils.mapAll(reasons, PostReasonCategoryDto.class);
		}
		return CompletableFuture.completedFuture(reasonCategories);
	}

	/**
	 * Method to fetch Reason List
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link ReasonListDto}
	 */
	@Async
	public CompletableFuture<List<ReasonListDto>> getReasonList(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<ReasonListDto> reasonList = null;
		List<ReasonList> reasons = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			reasons = reasonListRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REASON_LIST_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (reasons != null && !reasons.isEmpty())
			reasonList = MapperUtils.mapAll(reasons, ReasonListDto.class);

		return CompletableFuture.completedFuture(reasonList);
	}

	/**
	 * Method to fetch Holidays
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @param machineId
	 *            machine id
	 * @return list of {@link HolidayDto}
	 */
	@Async
	public CompletableFuture<List<HolidayDto>> getHolidays(LocalDateTime lastUpdated, String machineId,
			LocalDateTime currentTimeStamp) {
		List<HolidayDto> holidayList = null;
		List<Holiday> holidays = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			holidays = holidayRepository.findAllLatestCreatedUpdateDeletedByMachineId(machineId, lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
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
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link BlacklistedWordsDto}
	 */
	@Async
	public CompletableFuture<List<BlacklistedWordsDto>> getBlackListedWords(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<BlacklistedWordsDto> blacklistedWords = null;
		List<BlacklistedWords> words = null;

		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			words = blacklistedWordsRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (words != null && !words.isEmpty()) {
			blacklistedWords = MapperUtils.mapAll(words, BlacklistedWordsDto.class);
		}

		return CompletableFuture.completedFuture(blacklistedWords);
	}

	/**
	 * Method to fetch biometric types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link BiometricTypeDto}
	 */
	@Async
	public CompletableFuture<List<BiometricTypeDto>> getBiometricTypes(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<BiometricTypeDto> biometricTypeDtoList = null;
		List<BiometricType> biometricTypesList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			biometricTypesList = biometricTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypeDtoList = MapperUtils.mapAll(biometricTypesList, BiometricTypeDto.class);
		}
		return CompletableFuture.completedFuture(biometricTypeDtoList);
	}

	/**
	 * Method to fetch biometric attributes
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link BiometricAttributeDto}
	 */
	@Async
	public CompletableFuture<List<BiometricAttributeDto>> getBiometricAttributes(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<BiometricAttributeDto> biometricAttrList = null;
		List<BiometricAttribute> biometricAttrs = null;
		try {

			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			biometricAttrs = biometricAttributeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.BIOMETRIC_ATTR_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (biometricAttrs != null && !biometricAttrs.isEmpty()) {
			biometricAttrList = MapperUtils.mapAll(biometricAttrs, BiometricAttributeDto.class);
		}
		return CompletableFuture.completedFuture(biometricAttrList);
	}

	/**
	 * Method to fetch titles
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link TitleDto}
	 */
	@Async
	public CompletableFuture<List<TitleDto>> getTitles(LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<TitleDto> titleList = null;
		List<Title> titles = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			titles = titleRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (titles != null && !titles.isEmpty()) {

			titleList = MapperUtils.mapAll(titles, TitleDto.class);
		}
		return CompletableFuture.completedFuture(titleList);

	}

	/**
	 * Method to fetch languages
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link LanguageDto}
	 */
	@Async
	public CompletableFuture<List<LanguageDto>> getLanguages(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<LanguageDto> languageList = null;
		List<Language> languages = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			languages = languageRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (languages != null && !languages.isEmpty()) {
			languageList = MapperUtils.mapAll(languages, LanguageDto.class);
		}
		return CompletableFuture.completedFuture(languageList);
	}

	/**
	 * Method to fetch genders
	 * 
	 * @param lastUpdated
	 *            lastUpdated
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link GenderDto}
	 */
	@Async
	public CompletableFuture<List<GenderDto>> getGenders(LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<GenderDto> genderDto = null;
		List<Gender> genderType = null;

		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			genderType = genderTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.GENDER_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (!(genderType.isEmpty())) {
			genderDto = MapperUtils.mapAll(genderType, GenderDto.class);
		}
		return CompletableFuture.completedFuture(genderDto);
	}

	/**
	 * Method to fetch devices
	 * 
	 * @param regCenterId
	 *            registration center id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link DeviceDto}
	 */
	@Async
	public CompletableFuture<List<DeviceDto>> getDevices(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<Device> devices = null;
		List<DeviceDto> deviceList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			devices = deviceRepository.findLatestDevicesByRegCenterId(regCenterId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DEVICES_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (devices != null && !devices.isEmpty())
			deviceList = MapperUtils.mapAll(devices, DeviceDto.class);
		return CompletableFuture.completedFuture(deviceList);
	}

	/**
	 * Method to fetch document category
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link DocumentCategoryDto}
	 */
	@Async
	public CompletableFuture<List<DocumentCategoryDto>> getDocumentCategories(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<DocumentCategoryDto> documentCategoryList = null;
		List<DocumentCategory> documentCategories = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			documentCategories = documentCategoryRepository.findAllLatestCreatedUpdateDeleted(lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (documentCategories != null && !documentCategories.isEmpty())
			documentCategoryList = MapperUtils.mapAll(documentCategories, DocumentCategoryDto.class);

		return CompletableFuture.completedFuture(documentCategoryList);
	}

	/**
	 * Method to fetch document type
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link DocumentTypeDto}
	 */
	@Async
	public CompletableFuture<List<DocumentTypeDto>> getDocumentTypes(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<DocumentTypeDto> documentTypeList = null;
		List<DocumentType> documentTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			documentTypes = documentTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (documentTypes != null && !documentTypes.isEmpty())
			documentTypeList = MapperUtils.mapAll(documentTypes, DocumentTypeDto.class);

		return CompletableFuture.completedFuture(documentTypeList);
	}

	/**
	 * Method to fetch id types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link IdTypeDto}
	 */
	@Async
	public CompletableFuture<List<IdTypeDto>> getIdTypes(LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<IdTypeDto> idTypeList = null;
		List<IdType> idTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			idTypes = idTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.ID_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (idTypes != null && !idTypes.isEmpty())
			idTypeList = MapperUtils.mapAll(idTypes, IdTypeDto.class);
		return CompletableFuture.completedFuture(idTypeList);
	}

	/**
	 * Method to fetch device specification
	 * 
	 * @param regCenterId
	 *            registration center id
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link DeviceSpecificationDto}}
	 */
	@Async
	public CompletableFuture<List<DeviceSpecificationDto>> getDeviceSpecifications(String regCenterId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			deviceSpecificationList = deviceSpecificationRepository.findLatestDeviceTypeByRegCenterId(regCenterId,
					lastUpdated, currentTimeStamp);
		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DEVICE_SPECIFICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty())
			deviceSpecificationDtoList = MapperUtils.mapAll(deviceSpecificationList, DeviceSpecificationDto.class);
		return CompletableFuture.completedFuture(deviceSpecificationDtoList);

	}

	/**
	 * Method to fetch locations
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link LocationDto}
	 */
	@Async
	public CompletableFuture<List<LocationDto>> getLocationHierarchy(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<LocationDto> responseList = null;
		List<Location> locations = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}

			locations = locationRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (!locations.isEmpty()) {
			responseList = MapperUtils.mapAll(locations, LocationDto.class);
		}
		return CompletableFuture.completedFuture(responseList);
	}

	/**
	 * Method to fetch template types
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link TemplateTypeDto}
	 */
	@Async
	public CompletableFuture<List<TemplateTypeDto>> getTemplateTypes(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<TemplateTypeDto> templateTypeList = null;
		List<TemplateType> templateTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			templateTypes = templateTypeRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.TEMPLATE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (templateTypes != null && !templateTypes.isEmpty())
			templateTypeList = MapperUtils.mapAll(templateTypes, TemplateTypeDto.class);

		return CompletableFuture.completedFuture(templateTypeList);
	}

	/**
	 * Gets the device type.
	 *
	 * @param regCenterId
	 *            the reg center id
	 * @param lastUpdated
	 *            the last updated
	 * @param currentTimeStamp
	 *            the current time stamp
	 * @return {@link DeviceTypeDto}
	 */
	@Async
	public CompletableFuture<List<DeviceTypeDto>> getDeviceType(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<DeviceTypeDto> deviceTypeList = null;
		List<DeviceType> deviceTypes = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			deviceTypes = deviceTypeRepository.findLatestDeviceTypeByRegCenterId(regCenterId, lastUpdated,
					currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (deviceTypes != null && !deviceTypes.isEmpty()) {
			deviceTypeList = MapperUtils.mapAll(deviceTypes, DeviceTypeDto.class);
		}
		return CompletableFuture.completedFuture(deviceTypeList);
	}

	/**
	 * Method to fetch document mapping
	 * 
	 * @param lastUpdated
	 *            lastUpdated timestamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link ValidDocumentDto}
	 */
	@Async
	public CompletableFuture<List<ValidDocumentDto>> getValidDocuments(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp) {
		List<ValidDocumentDto> validDocumentList = null;
		List<ValidDocument> validDocuments = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			validDocuments = validDocumentRepository.findAllLatestCreatedUpdateDeleted(lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

		if (validDocuments != null && !validDocuments.isEmpty()) {
			validDocumentList = MapperUtils.mapAll(validDocuments, ValidDocumentDto.class);
		}
		return CompletableFuture.completedFuture(validDocumentList);
	}

	/**
	 * 
	 * @param machineId
	 *            - machine id
	 * @param lastUpdated
	 *            - last updated time stamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterMachineDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterMachineDto>> getRegistrationCenterMachines(String machineId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterMachineDto> registrationCenterMachineDtos = null;
		List<RegistrationCenterMachine> registrationCenterMachines = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterMachines = registrationCenterMachineRepository
					.findAllLatestCreatedUpdatedDeleted(machineId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REG_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (registrationCenterMachines != null && !registrationCenterMachines.isEmpty()) {
			registrationCenterMachineDtos = MapperUtils.mapAll(registrationCenterMachines,
					RegistrationCenterMachineDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterMachineDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time stamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterDeviceDto}
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterDeviceDto>> getRegistrationCenterDevices(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterDeviceDto> registrationCenterDeviceDtos = null;
		List<RegistrationCenterDevice> registrationCenterDevices = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterDevices = registrationCenterDeviceRepository
					.findAllLatestByRegistrationCenterCreatedUpdatedDeleted(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REG_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (registrationCenterDevices != null && !registrationCenterDevices.isEmpty()) {
			registrationCenterDeviceDtos = MapperUtils.mapAll(registrationCenterDevices,
					RegistrationCenterDeviceDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterDeviceDtos);
	}

	/**
	 * 
	 * @param regId
	 *            -registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterMachineDeviceDto} - list of
	 *         registration center machine device dto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterMachineDeviceDto>> getRegistrationCenterMachineDevices(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterMachineDeviceDto> registrationCenterMachineDeviceDtos = null;
		List<RegistrationCenterMachineDevice> registrationCenterMachineDevices = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterMachineDevices = registrationCenterMachineDeviceRepository
					.findAllByRegistrationCenterIdCreatedUpdatedDeleted(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_MACHINE_DEVICE_FETCH_EXCEPTION.getErrorCode(), e.getMessage(), e);
		}
		if (registrationCenterMachineDevices != null && !registrationCenterMachineDevices.isEmpty()) {
			registrationCenterMachineDeviceDtos = MapperUtils.mapAll(registrationCenterMachineDevices,
					RegistrationCenterMachineDeviceDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterMachineDeviceDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterUserMachineMappingDto} - list of
	 *         RegistrationCenterUserMachineMappingDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterUserMachineMappingDto>> getRegistrationCenterUserMachines(
			String regId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDtos = null;
		List<RegistrationCenterUserMachine> registrationCenterUserMachines = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterUserMachines = registrationCenterUserMachineRepository
					.findAllByRegistrationCenterIdCreatedUpdatedDeleted(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_USER_MACHINE_DEVICE_FETCH_EXCEPTION.getErrorCode(), e.getMessage(), e);
		}
		if (registrationCenterUserMachines != null && !registrationCenterUserMachines.isEmpty()) {
			registrationCenterUserMachineMappingDtos = MapperUtils.mapAll(registrationCenterUserMachines,
					RegistrationCenterUserMachineMappingDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterUserMachineMappingDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterUserDto} - list of
	 *         RegistrationCenterUserDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterUserDto>> getRegistrationCenterUsers(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterUserDto> registrationCenterUserDtos = null;
		List<RegistrationCenterUser> registrationCenterUsers = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterUsers = registrationCenterUserRepository
					.findAllByRegistrationCenterIdCreatedUpdatedDeleted(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {
			throw new SyncDataServiceException(MasterDataErrorCode.REG_CENTER_USER_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
		if (registrationCenterUsers != null && !registrationCenterUsers.isEmpty()) {
			registrationCenterUserDtos = MapperUtils.mapAll(registrationCenterUsers, RegistrationCenterUserDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterUserDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterUserHistoryDto} - list of
	 *         RegistrationCenterUserHistoryDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterUserHistoryDto>> getRegistrationCenterUserHistory(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterUserHistoryDto> registrationCenterUserHistoryDtos = null;
		List<RegistrationCenterUserHistory> registrationCenterUserHistoryList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterUserHistoryList = registrationCenterUserHistoryRepository
					.findLatestRegistrationCenterUserHistory(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {

			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_USER_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.REG_CENTER_USER_HISTORY_FETCH_EXCEPTION.getErrorMessage() + " "
							+ e.getMessage(), e);
		}
		if (registrationCenterUserHistoryList != null && !registrationCenterUserHistoryList.isEmpty()) {
			registrationCenterUserHistoryDtos = MapperUtils.mapAll(registrationCenterUserHistoryList,
					RegistrationCenterUserHistoryDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterUserHistoryDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterUserMachineMappingHistoryDto} - list
	 *         of RegistrationCenterUserMachineMappingHistoryDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterUserMachineMappingHistoryDto>> getRegistrationCenterUserMachineMapping(
			String regId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenterUserMachineMappingHistoryDtos = null;
		List<RegistrationCenterUserMachineHistory> registrationCenterUserMachineHistoryList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterUserMachineHistoryList = registrationCenterUserMachineHistoryRepository
					.findLatestRegistrationCenterUserMachineHistory(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {

			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_MACHINE_USER_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.REG_CENTER_MACHINE_USER_HISTORY_FETCH_EXCEPTION.getErrorCode() + " "
							+ e.getMessage(), e);
		}
		if (registrationCenterUserMachineHistoryList != null && !registrationCenterUserMachineHistoryList.isEmpty()) {
			registrationCenterUserMachineMappingHistoryDtos = MapperUtils.mapAll(
					registrationCenterUserMachineHistoryList, RegistrationCenterUserMachineMappingHistoryDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterUserMachineMappingHistoryDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterMachineDeviceHistoryDto} - list of
	 *         RegistrationCenterMachineDeviceHistoryDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterMachineDeviceHistoryDto>> getRegistrationCenterMachineDeviceHistoryDetails(
			String regId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterMachineDeviceHistoryDto> registrationCenterMachineDeviceHistoryDtos = null;
		List<RegistrationCenterMachineDeviceHistory> registrationCenterMachineDeviceHistoryList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterMachineDeviceHistoryList = registrationCenterMachineDeviceHistoryRepository
					.findLatestRegistrationCenterMachineDeviceHistory(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {

			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_MACHINE_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.REG_CENTER_MACHINE_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorMessage() + " "
							+ e.getMessage(), e);
		}
		if (registrationCenterMachineDeviceHistoryList != null
				&& !registrationCenterMachineDeviceHistoryList.isEmpty()) {
			registrationCenterMachineDeviceHistoryDtos = MapperUtils.mapAll(registrationCenterMachineDeviceHistoryList,
					RegistrationCenterMachineDeviceHistoryDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterMachineDeviceHistoryDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterDeviceHistoryDto} - list of
	 *         RegistrationCenterDeviceHistoryDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterDeviceHistoryDto>> getRegistrationCenterDeviceHistoryDetails(
			String regId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterDeviceHistoryDto> registrationCenterDeviceHistoryDtos = null;
		List<RegistrationCenterDeviceHistory> registrationCenterDeviceHistoryList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterDeviceHistoryList = registrationCenterDeviceHistoryRepository
					.findLatestRegistrationCenterDeviceHistory(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {

			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.REG_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorMessage() + " "
							+ e.getMessage(), e);
		}
		if (registrationCenterDeviceHistoryList != null && !registrationCenterDeviceHistoryList.isEmpty()) {
			registrationCenterDeviceHistoryDtos = MapperUtils.mapAll(registrationCenterDeviceHistoryList,
					RegistrationCenterDeviceHistoryDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterDeviceHistoryDtos);
	}

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @param lastUpdated
	 *            - last updated time
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link RegistrationCenterMachineHistoryDto} - list of
	 *         RegistrationCenterMachineHistoryDto
	 */
	@Async
	public CompletableFuture<List<RegistrationCenterMachineHistoryDto>> getRegistrationCenterMachineHistoryDetails(
			String regId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp) {
		List<RegistrationCenterMachineHistoryDto> registrationCenterMachineHistoryDtos = null;
		List<RegistrationCenterMachineHistory> registrationCenterMachineHistoryList = null;
		try {
			if (lastUpdated == null) {
				lastUpdated = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			registrationCenterMachineHistoryList = registrationCenterMachineHistoryRepository
					.findLatestRegistrationCenterMachineHistory(regId, lastUpdated, currentTimeStamp);

		} catch (DataAccessException e) {

			throw new SyncDataServiceException(
					MasterDataErrorCode.REG_CENTER_MACHINE_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.REG_CENTER_MACHINE_HISTORY_FETCH_EXCEPTION.getErrorMessage() + " "
							+ e.getMessage(), e);
		}
		if (registrationCenterMachineHistoryList != null && !registrationCenterMachineHistoryList.isEmpty()) {
			registrationCenterMachineHistoryDtos = MapperUtils.mapAll(registrationCenterMachineHistoryList,
					RegistrationCenterMachineHistoryDto.class);
		}
		return CompletableFuture.completedFuture(registrationCenterMachineHistoryDtos);
	}

	/**
	 * 
	 * @param lastUpdatedTime
	 *            - last updated time stamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link ApplicantValidDocumentDto}
	 */
	@Async
	public CompletableFuture<List<ApplicantValidDocumentDto>> getApplicantValidDocument(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<ApplicantValidDocumentDto> applicantValidDocumentDtos = null;
		List<ApplicantValidDocument> applicantValidDocuments = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			applicantValidDocuments = applicantValidDocumentRepository.findAllByTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(
					MasterDataErrorCode.APPLICANT_VALID_DOCUMENT_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.APPLICANT_VALID_DOCUMENT_FETCH_EXCEPTION.getErrorMessage());
		}
		if (applicantValidDocuments != null && !applicantValidDocuments.isEmpty()) {
			applicantValidDocumentDtos = MapperUtils.mapAll(applicantValidDocuments, ApplicantValidDocumentDto.class);
		}
		return CompletableFuture.completedFuture(applicantValidDocumentDtos);
	}

	/**
	 * 
	 * @param lastUpdatedTime
	 *            - last updated time stamp
	 * @param currentTimeStamp
	 *            - current time stamp
	 * @return list of {@link IndividualTypeDto}
	 */
	@Async
	public CompletableFuture<List<IndividualTypeDto>> getIndividualType(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<IndividualType> individualTypes = null;
		List<IndividualTypeDto> individualTypeDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			individualTypes = individualTypeRepository.findAllIndvidualTypeByTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (individualTypes != null && !individualTypes.isEmpty()) {
			individualTypeDtos = MapperUtils.mapAll(individualTypes, IndividualTypeDto.class);
		}
		return CompletableFuture.completedFuture(individualTypeDtos);

	}

	@Async
	public CompletableFuture<List<AppAuthenticationMethodDto>> getAppAuthenticationMethodDetails(
			LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {
		List<AppAuthenticationMethod> appAuthenticationMethods = null;
		List<AppAuthenticationMethodDto> appAuthenticationMethodDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			appAuthenticationMethods = appAuthenticationMethodRepository
					.findByLastUpdatedAndCurrentTimeStamp(lastUpdatedTime, currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(
					MasterDataErrorCode.APP_AUTHORIZATION_METHOD_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.APP_AUTHORIZATION_METHOD_FETCH_EXCEPTION.getErrorMessage());
		}
		if (appAuthenticationMethods != null && !appAuthenticationMethods.isEmpty()) {
			appAuthenticationMethodDtos = MapperUtils.mapAll(appAuthenticationMethods,
					AppAuthenticationMethodDto.class);
		}
		return CompletableFuture.completedFuture(appAuthenticationMethodDtos);

	}

	@Async
	public CompletableFuture<List<AppDetailDto>> getAppDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<AppDetail> appDetails = null;
		List<AppDetailDto> appDetailDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			appDetails = appDetailRepository.findByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.APP_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.APP_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (appDetails != null && !appDetails.isEmpty()) {
			appDetailDtos = MapperUtils.mapAll(appDetails, AppDetailDto.class);
		}
		return CompletableFuture.completedFuture(appDetailDtos);
	}

	@Async
	public CompletableFuture<List<AppRolePriorityDto>> getAppRolePriorityDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<AppRolePriority> appRolePriorities = null;
		List<AppRolePriorityDto> appRolePriorityDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			appRolePriorities = appRolePriorityRepository.findByLastUpdatedAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.APP_ROLE_PRIORITY_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.APP_ROLE_PRIORITY_FETCH_EXCEPTION.getErrorMessage());
		}
		if (appRolePriorities != null && !appRolePriorities.isEmpty()) {
			appRolePriorityDtos = MapperUtils.mapAll(appRolePriorities, AppRolePriorityDto.class);
		}
		return CompletableFuture.completedFuture(appRolePriorityDtos);
	}

	@Async
	public CompletableFuture<List<ScreenAuthorizationDto>> getScreenAuthorizationDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<ScreenAuthorization> screenAuthorizationList = null;
		List<ScreenAuthorizationDto> screenAuthorizationDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			screenAuthorizationList = screenAuthorizationRepository
					.findByLastUpdatedAndCurrentTimeStamp(lastUpdatedTime, currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.SCREEN_AUTHORIZATION_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.SCREEN_AUTHORIZATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (screenAuthorizationList != null && !screenAuthorizationList.isEmpty()) {
			screenAuthorizationDtos = MapperUtils.mapAll(screenAuthorizationList, ScreenAuthorizationDto.class);
		}
		return CompletableFuture.completedFuture(screenAuthorizationDtos);
	}

	@Async
	public CompletableFuture<List<ProcessListDto>> getProcessList(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<ProcessList> processList = null;
		List<ProcessListDto> processListDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			processList = processListRepository.findByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.PROCESS_LIST_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.PROCESS_LIST_FETCH_EXCEPTION.getErrorMessage());
		}
		if (processList != null && !processList.isEmpty()) {
			processListDtos = MapperUtils.mapAll(processList, ProcessListDto.class);
		}
		return CompletableFuture.completedFuture(processListDtos);
	}

	@Async
	public CompletableFuture<List<SyncJobDefDto>> getSyncJobDefDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {

		if (lastUpdatedTime == null) {
			lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
		}
		List<SyncJobDefDto> syncJobDefDtos = syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);
		return CompletableFuture.completedFuture(syncJobDefDtos);
	}

	@Async
	public CompletableFuture<List<ScreenDetailDto>> getScreenDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<ScreenDetail> screenDetails = null;
		List<ScreenDetailDto> screenDetailDtos = null;
		try {
			if (lastUpdatedTime == null) {
				lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
			}
			screenDetails = screenDetailRepository.findByLastUpdatedAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.SCREEN_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.SCREEN_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (screenDetails != null && !screenDetails.isEmpty()) {
			screenDetailDtos = MapperUtils.mapAll(screenDetails, ScreenDetailDto.class);
		}
		return CompletableFuture.completedFuture(screenDetailDtos);
	}

}
