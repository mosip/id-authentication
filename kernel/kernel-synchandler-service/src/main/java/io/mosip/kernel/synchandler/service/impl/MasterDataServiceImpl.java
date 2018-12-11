package io.mosip.kernel.synchandler.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.service.MasterDataService;
import io.mosip.kernel.synchandler.service.MasterDataServiceHelper;

/**
 * masterdata sync handler service impl
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@Service
public class MasterDataServiceImpl implements MasterDataService {

	@Autowired
	MasterDataServiceHelper serviceHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.synchandler.service.MasterDataService#syncData(java.lang.
	 * String, java.time.LocalDate)
	 */

	@Override
	public MasterDataResponseDto syncData(String machineId, LocalDateTime lastUpdated)
			throws InterruptedException, ExecutionException {
		MasterDataResponseDto response = new MasterDataResponseDto();
		CompletableFuture<List<ApplicationDto>> applications = null;
		CompletableFuture<List<MachineDto>> machineDetails = null;
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
		// get data
		applications = serviceHelper.getApplications(lastUpdated);
		machineDetails = serviceHelper.getMachines(machineId, lastUpdated);
		registrationCenters = serviceHelper.getRegistrationCenter(machineId, lastUpdated);
		registrationCenterTypes = serviceHelper.getRegistrationCenterType(machineId, lastUpdated);
		templates = serviceHelper.getTemplates(lastUpdated);
		templateFileFormats = serviceHelper.getTemplateFileFormats(lastUpdated);
		reasonCategory = serviceHelper.getReasonCategory(lastUpdated);
		holidays = serviceHelper.getHolidays(lastUpdated, machineId);
		blacklistedWords = serviceHelper.getBlackListedWords(lastUpdated);
		biometricTypes = serviceHelper.getBiometricTypes(lastUpdated);
		biometricAttributes = serviceHelper.getBiometricAttributes(lastUpdated);
		titles = serviceHelper.getTitles(lastUpdated);
		languages = serviceHelper.getLanguages(lastUpdated);
		genders = serviceHelper.getGenders(lastUpdated);
		devices = serviceHelper.getDevices(machineId, lastUpdated);
		documentCategories = serviceHelper.getDocumentCategories(lastUpdated);
		documentTypes = serviceHelper.getDocumentTypes(lastUpdated);
		idTypes = serviceHelper.getIdTypes(lastUpdated);
		deviceSpecifications = serviceHelper.getDeviceSpecifications(machineId, lastUpdated);
		locationHierarchy = serviceHelper.getLocationHierarchy(lastUpdated);
		machineSpecification = serviceHelper.getMachineSpecification(machineId, lastUpdated);
		machineType = serviceHelper.getMachineType(machineId, lastUpdated);
		templateTypes = serviceHelper.getTemplateTypes(lastUpdated);
		deviceTypes = serviceHelper.getDeviceType(machineId, lastUpdated);
		validDocumentsMapping = serviceHelper.getValidDocuments(lastUpdated);
		reasonList = serviceHelper.getReasonList(lastUpdated);
		// set data

		CompletableFuture.allOf(applications, machineDetails, registrationCenterTypes, registrationCenters, templates,
				templateFileFormats, reasonCategory, reasonList, holidays, blacklistedWords, biometricTypes,
				biometricAttributes, titles, languages, devices, documentCategories, documentTypes, idTypes,
				deviceSpecifications, locationHierarchy, machineSpecification, machineType, templateTypes, deviceTypes,
				validDocumentsMapping).join();

		response.setMachineDetails(machineDetails.get());
		response.setApplications(applications.get());
		response.setRegistrationCenterTypes(registrationCenterTypes.get());
		response.setRegistrationCenter(registrationCenters.get());
		response.setTemplates(templates.get());
		response.setTemplateFileType(templateFileFormats.get());
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

		return response;
	}
}
