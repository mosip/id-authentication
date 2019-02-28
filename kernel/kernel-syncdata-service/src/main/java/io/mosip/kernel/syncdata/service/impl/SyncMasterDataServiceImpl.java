package io.mosip.kernel.syncdata.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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
import io.mosip.kernel.syncdata.dto.TemplateDto;
import io.mosip.kernel.syncdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.syncdata.dto.TemplateTypeDto;
import io.mosip.kernel.syncdata.dto.TitleDto;
import io.mosip.kernel.syncdata.dto.ValidDocumentDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.service.SyncMasterDataService;
import io.mosip.kernel.syncdata.utils.SyncMasterDataServiceHelper;

/**
 * Masterdata sync handler service impl
 * 
 * @author Abhishek Kumar
 * @author Srinivasan
 * @since 1.0.0
 */
@Service
public class SyncMasterDataServiceImpl implements SyncMasterDataService {

	@Autowired
	SyncMasterDataServiceHelper serviceHelper;

	@Autowired
	MachineRepository machineRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.synchandler.service.MasterDataService#syncData(java.lang.
	 * String, java.time.LocalDate)
	 */

	@Override
	public MasterDataResponseDto syncData(String machineId, LocalDateTime lastUpdated, LocalDateTime currentTimeStamp)
			throws InterruptedException, ExecutionException {
		List<Machine> machines = null;
		try {
			machines = machineRepository.findByMachineIdAndIsActive(machineId);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(MasterDataErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					ex.getMessage());
		}
		if (machines.isEmpty()) {
			throw new DataNotFoundException(MasterDataErrorCode.MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		MasterDataResponseDto response = new MasterDataResponseDto();
		CompletableFuture<List<MachineDto>> machineDetails = null;
		CompletableFuture<List<ApplicationDto>> applications = null;
		CompletableFuture<List<RegistrationCenterTypeDto>> registrationCenterTypes = null;
		CompletableFuture<List<RegistrationCenterDto>> registrationCenters = null;
		CompletableFuture<List<TemplateDto>> templates = null;
		CompletableFuture<List<TemplateFileFormatDto>> templateFileFormats = null;
		CompletableFuture<List<PostReasonCategoryDto>> reasonCategory = null;
		CompletableFuture<List<HolidayDto>> holidays = null;
		CompletableFuture<List<BlacklistedWordsDto>> blacklistedWords = null;
		CompletableFuture<List<BiometricTypeDto>> biometricTypes = null;
		CompletableFuture<List<BiometricAttributeDto>> biometricAttributes = null;
		CompletableFuture<List<TitleDto>> titles = null;
		CompletableFuture<List<LanguageDto>> languages = null;
		CompletableFuture<List<GenderDto>> genders = null;
		CompletableFuture<List<DeviceDto>> devices = null;
		CompletableFuture<List<DocumentCategoryDto>> documentCategories = null;
		CompletableFuture<List<DocumentTypeDto>> documentTypes = null;
		CompletableFuture<List<IdTypeDto>> idTypes = null;
		CompletableFuture<List<DeviceSpecificationDto>> deviceSpecifications = null;
		CompletableFuture<List<LocationDto>> locationHierarchy = null;
		CompletableFuture<List<MachineSpecificationDto>> machineSpecification = null;
		CompletableFuture<List<MachineTypeDto>> machineType = null;
		CompletableFuture<List<TemplateTypeDto>> templateTypes = null;
		CompletableFuture<List<DeviceTypeDto>> deviceTypes = null;
		CompletableFuture<List<ValidDocumentDto>> validDocumentsMapping = null;
		CompletableFuture<List<ReasonListDto>> reasonList = null;

		CompletableFuture<List<RegistrationCenterMachineDto>> registrationCenterMachines = null;
		CompletableFuture<List<RegistrationCenterDeviceDto>> registrationCenterDevices = null;
		CompletableFuture<List<RegistrationCenterMachineDeviceDto>> registrationCenterMachineDevices = null;
		CompletableFuture<List<RegistrationCenterUserMachineMappingDto>> registrationCenterUserMachines = null;
		CompletableFuture<List<RegistrationCenterUserDto>> registrationCenterUsers = null;
		CompletableFuture<List<RegistrationCenterUserHistoryDto>> registrationCenterUserHistoryList = null;
		CompletableFuture<List<RegistrationCenterUserMachineMappingHistoryDto>> registrationCenterUserMachineMappingHistoryList = null;
		CompletableFuture<List<RegistrationCenterMachineDeviceHistoryDto>> registrationCenterMachineDeviceHistoryList = null;
		CompletableFuture<List<RegistrationCenterDeviceHistoryDto>> registrationCenterDeviceHistoryList = null;
		CompletableFuture<List<RegistrationCenterMachineHistoryDto>> registrationCenterMachineHistoryList = null;

		applications = serviceHelper.getApplications(lastUpdated, currentTimeStamp);
		machineDetails = serviceHelper.getMachines(machineId, lastUpdated, currentTimeStamp);
		registrationCenters = serviceHelper.getRegistrationCenter(machineId, lastUpdated, currentTimeStamp);
		registrationCenterTypes = serviceHelper.getRegistrationCenterType(machineId, lastUpdated, currentTimeStamp);
		templates = serviceHelper.getTemplates(lastUpdated, currentTimeStamp);
		templateFileFormats = serviceHelper.getTemplateFileFormats(lastUpdated, currentTimeStamp);
		reasonCategory = serviceHelper.getReasonCategory(lastUpdated, currentTimeStamp);
		holidays = serviceHelper.getHolidays(lastUpdated, machineId, currentTimeStamp);
		blacklistedWords = serviceHelper.getBlackListedWords(lastUpdated, currentTimeStamp);
		biometricTypes = serviceHelper.getBiometricTypes(lastUpdated, currentTimeStamp);
		biometricAttributes = serviceHelper.getBiometricAttributes(lastUpdated, currentTimeStamp);
		titles = serviceHelper.getTitles(lastUpdated, currentTimeStamp);
		languages = serviceHelper.getLanguages(lastUpdated, currentTimeStamp);
		genders = serviceHelper.getGenders(lastUpdated, currentTimeStamp);
		devices = serviceHelper.getDevices(machineId, lastUpdated, currentTimeStamp);
		documentCategories = serviceHelper.getDocumentCategories(lastUpdated, currentTimeStamp);
		documentTypes = serviceHelper.getDocumentTypes(lastUpdated, currentTimeStamp);
		idTypes = serviceHelper.getIdTypes(lastUpdated, currentTimeStamp);
		deviceSpecifications = serviceHelper.getDeviceSpecifications(machineId, lastUpdated, currentTimeStamp);
		locationHierarchy = serviceHelper.getLocationHierarchy(lastUpdated, currentTimeStamp);
		machineSpecification = serviceHelper.getMachineSpecification(machineId, lastUpdated, currentTimeStamp);
		machineType = serviceHelper.getMachineType(machineId, lastUpdated, currentTimeStamp);
		templateTypes = serviceHelper.getTemplateTypes(lastUpdated, currentTimeStamp);
		deviceTypes = serviceHelper.getDeviceType(machineId, lastUpdated, currentTimeStamp);
		validDocumentsMapping = serviceHelper.getValidDocuments(lastUpdated, currentTimeStamp);
		reasonList = serviceHelper.getReasonList(lastUpdated, currentTimeStamp);

		
		//List<RegistrationCenterMachineDto> registrationCenterMachineDto = registrationCenterMachines.get();

		String regId = getRegistrationCenterId(registrationCenters.get());
		registrationCenterMachines = serviceHelper.getRegistrationCenterMachines(regId, lastUpdated,
				currentTimeStamp);
		registrationCenterDevices = serviceHelper.getRegistrationCenterDevices(regId, lastUpdated, currentTimeStamp);
		registrationCenterMachineDevices = serviceHelper.getRegistrationCenterMachineDevices(regId, lastUpdated,
				currentTimeStamp);
		registrationCenterUserMachines = serviceHelper.getRegistrationCenterUserMachines(regId, lastUpdated,
				currentTimeStamp);
		registrationCenterUsers = serviceHelper.getRegistrationCenterUsers(regId, lastUpdated, currentTimeStamp);
		registrationCenterUserHistoryList = serviceHelper.getRegistrationCenterUserHistory(regId, lastUpdated,
				currentTimeStamp);
		registrationCenterUserMachineMappingHistoryList = serviceHelper.getRegistrationCenterUserMachineMapping(regId,
				lastUpdated, currentTimeStamp);
		registrationCenterMachineDeviceHistoryList = serviceHelper
				.getRegistrationCenterMachineDeviceHistoryDetails(regId, lastUpdated, currentTimeStamp);
		registrationCenterDeviceHistoryList = serviceHelper.getRegistrationCenterDeviceHistoryDetails(regId,
				lastUpdated, currentTimeStamp);
		registrationCenterMachineHistoryList = serviceHelper.getRegistrationCenterMachineHistoryDetails(regId,
				lastUpdated, currentTimeStamp);

		CompletableFuture.allOf(machineDetails, applications, registrationCenterTypes, registrationCenters, templates,
				templateFileFormats, reasonCategory, reasonList, holidays, blacklistedWords, biometricTypes,
				biometricAttributes, titles, languages, devices, documentCategories, documentTypes, idTypes,
				deviceSpecifications, locationHierarchy, machineSpecification, machineType, templateTypes, deviceTypes,
				validDocumentsMapping, registrationCenterMachines, registrationCenterDevices,
				registrationCenterMachineDevices, registrationCenterUserMachines, registrationCenterUsers,
				registrationCenterUserHistoryList, registrationCenterUserMachineMappingHistoryList,
				registrationCenterMachineDeviceHistoryList, registrationCenterDeviceHistoryList,
				registrationCenterMachineHistoryList).join();

		response.setMachineDetails(machineDetails.get());
		response.setApplications(applications.get());
		response.setRegistrationCenterTypes(registrationCenterTypes.get());
		response.setRegistrationCenter(registrationCenters.get());
		response.setTemplates(templates.get());
		response.setTemplateFileFormat(templateFileFormats.get());
		response.setReasonCategory(reasonCategory.get());
		response.setReasonList(reasonList.get());
		response.setHolidays(holidays.get());
		response.setBlackListedWords(blacklistedWords.get());
		response.setBiometricTypes(biometricTypes.get());
		response.setBiometricattributes(biometricAttributes.get());
		response.setTitles(titles.get());
		response.setLanguages(languages.get());
		response.setGenders(genders.get());
		response.setDevices(devices.get());
		response.setDocumentCategories(documentCategories.get());
		response.setDocumentTypes(documentTypes.get());
		response.setIdTypes(idTypes.get());
		response.setDeviceSpecifications(deviceSpecifications.get());
		response.setLocationHierarchy(locationHierarchy.get());
		response.setMachineSpecification(machineSpecification.get());
		response.setMachineType(machineType.get());
		response.setTemplatesTypes(templateTypes.get());
		response.setDeviceTypes(deviceTypes.get());
		response.setValidDocumentMapping(validDocumentsMapping.get());

		response.setRegistrationCenterMachines(registrationCenterMachines.get());
		response.setRegistrationCenterDevices(registrationCenterDevices.get());
		response.setRegistrationCenterMachineDevices(registrationCenterMachineDevices.get());
		response.setRegistrationCenterUserMachines(registrationCenterUserMachines.get());
		response.setRegistrationCenterUsers(registrationCenterUsers.get());
		response.setRegistrationCenterUserHistory(registrationCenterUserHistoryList.get());
		response.setRegistrationCenterUserMachineMappingHistory(registrationCenterUserMachineMappingHistoryList.get());
		response.setRegistrationCenterDeviceHistory(registrationCenterDeviceHistoryList.get());
		response.setRegistrationCenterMachineHistory(registrationCenterMachineHistoryList.get());
		response.setRegistrationCenterMachineDeviceHistory(registrationCenterMachineDeviceHistoryList.get());

		return response;
	}

	private static String getRegistrationCenterId(List<RegistrationCenterDto> registrationCenterDto) {
		String regId = null;
		if (registrationCenterDto != null && !registrationCenterDto.isEmpty()) {
			regId = registrationCenterDto.get(0).getId();
		}
		return regId;
	}
}
