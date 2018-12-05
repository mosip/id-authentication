package io.mosip.kernel.synchandler.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public MasterDataResponseDto syncData(String machineId, LocalDateTime lastUpdated) {
		MasterDataResponseDto response = new MasterDataResponseDto();
		List<ApplicationDto> applications = null;
		List<MachineDto> machineDetails = null;
		List<RegistrationCenterTypeDto> registrationCenterTypes = null;
		List<RegistrationCenterDto> registrationCenters = null;
		List<TemplateDto> templates = null;
		List<TemplateFileFormatDto> templateFileFormats = null;
		List<PostReasonCategoryDto> reasonCategory = null;
		List<HolidayDto> holidays = null;
		List<BlacklistedWordsDto> blacklistedWords = null;
		List<BiometricTypeDto> biometricTypes = null;
		List<BiometricAttributeDto> biometricAttributes = null;
		List<TitleDto> titles = null;
		List<LanguageDto> languages = null;
		List<GenderDto> genders = null;
		List<DeviceDto> devices = null;
		List<DocumentCategoryDto> documentCategories = null;
		List<DocumentTypeDto> documentTypes = null;
		List<IdTypeDto> idTypes = null;
		List<DeviceSpecificationDto> deviceSpecifications = null;
		List<LocationDto> locationHierarchy = null;
		List<MachineSpecificationDto> machineSpecification = null;
		List<MachineTypeDto> machineType = null;
		List<TemplateTypeDto> templateTypes = null;
		List<DeviceTypeDto> deviceTypes = null;
		List<ValidDocumentDto> validDocumentsMapping = null;
		List<ReasonListDto> reasonList = null;
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
		response.setApplications(applications);
		response.setMachineDetails(machineDetails);
		response.setRegistrationCenterTypes(registrationCenterTypes);
		response.setRegistrationCenter(registrationCenters);
		response.setTemplates(templates);
		response.setTemplateFileType(templateFileFormats);
		response.setReasonCategory(reasonCategory);
		response.setReasonList(reasonList);
		response.setHolidays(holidays);
		response.setBlackListedWords(blacklistedWords);
		response.setBiometricTypes(biometricTypes);
		response.setBiometricattributes(biometricAttributes);
		response.setTitles(titles);
		response.setLanguages(languages);
		response.setGenders(genders);
		response.setDevices(devices);
		response.setDocumentCategories(documentCategories);
		response.setDocumentTypes(documentTypes);
		response.setIdTypes(idTypes);
		response.setDeviceSpecifications(deviceSpecifications);
		response.setLocationHierarchy(locationHierarchy);
		response.setMachineSpecification(machineSpecification);
		response.setMachineType(machineType);
		response.setTemplatesTypes(templateTypes);
		response.setDeviceTypes(deviceTypes);
		response.setValidDocumentMapping(validDocumentsMapping);

		return response;
	}
}
