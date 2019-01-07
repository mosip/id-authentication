package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public class MasterDataResponseDto {
	
	private List<RegistrationCenterDto> registrationCenter;
	private List<RegistrationCenterTypeDto> registrationCenterTypes;
	private List<MachineDto> machineDetails;
	private List<MachineSpecificationDto> machineSpecification;
	private List<MachineTypeDto> machineType;
	private List<DeviceDto> devices;
	private List<DeviceTypeDto> deviceTypes;
	private List<DeviceSpecificationDto> deviceSpecifications;
	private List<HolidayDto> holidays;
	private List<DocumentCategoryDto> documentCategories;
	private List<DocumentTypeDto> documentTypes;
	private List<ValidDocumentDto> validDocumentMapping;
	private List<TemplateDto> templates;
	private List<TemplateTypeDto> templatesTypes;
	private List<TemplateFileFormatDto> templateFileFormat;
	private List<PostReasonCategoryDto> reasonCategory;
	private List<MasterReasonListDto> reasonList;
	private List<BlacklistedWordsDto> blackListedWords;
	private List<LocationDto> locationHierarchy;
	private List<BiometricAttributeDto> biometricattributes;
	private List<BiometricTypeDto> biometricTypes;
	private List<ApplicationDto> applications;
	private List<IdTypeDto> idTypes;
	private List<TitleDto> titles;
	private List<GenderDto> genders;
	private List<LanguageDto> languages;

	/**
	 * @return the registrationCenter
	 */
	public List<RegistrationCenterDto> getRegistrationCenter() {
		return registrationCenter;
	}

	/**
	 * @param registrationCenter the registrationCenter to set
	 */
	public void setRegistrationCenter(List<RegistrationCenterDto> registrationCenter) {
		this.registrationCenter = registrationCenter;
	}

	/**
	 * @return the registrationCenterTypes
	 */
	public List<RegistrationCenterTypeDto> getRegistrationCenterTypes() {
		return registrationCenterTypes;
	}

	/**
	 * @param registrationCenterTypes the registrationCenterTypes to set
	 */
	public void setRegistrationCenterTypes(List<RegistrationCenterTypeDto> registrationCenterTypes) {
		this.registrationCenterTypes = registrationCenterTypes;
	}

	/**
	 * @return the machineDetails
	 */
	public List<MachineDto> getMachineDetails() {
		return machineDetails;
	}

	/**
	 * @param machineDetails the machineDetails to set
	 */
	public void setMachineDetails(List<MachineDto> machineDetails) {
		this.machineDetails = machineDetails;
	}

	/**
	 * @return the machineSpecification
	 */
	public List<MachineSpecificationDto> getMachineSpecification() {
		return machineSpecification;
	}

	/**
	 * @param machineSpecification the machineSpecification to set
	 */
	public void setMachineSpecification(List<MachineSpecificationDto> machineSpecification) {
		this.machineSpecification = machineSpecification;
	}

	/**
	 * @return the machineType
	 */
	public List<MachineTypeDto> getMachineType() {
		return machineType;
	}

	/**
	 * @param machineType the machineType to set
	 */
	public void setMachineType(List<MachineTypeDto> machineType) {
		this.machineType = machineType;
	}

	/**
	 * @return the devices
	 */
	public List<DeviceDto> getDevices() {
		return devices;
	}

	/**
	 * @param devices the devices to set
	 */
	public void setDevices(List<DeviceDto> devices) {
		this.devices = devices;
	}

	/**
	 * @return the deviceTypes
	 */
	public List<DeviceTypeDto> getDeviceTypes() {
		return deviceTypes;
	}

	/**
	 * @param deviceTypes the deviceTypes to set
	 */
	public void setDeviceTypes(List<DeviceTypeDto> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

	/**
	 * @return the deviceSpecifications
	 */
	public List<DeviceSpecificationDto> getDeviceSpecifications() {
		return deviceSpecifications;
	}

	/**
	 * @param deviceSpecifications the deviceSpecifications to set
	 */
	public void setDeviceSpecifications(List<DeviceSpecificationDto> deviceSpecifications) {
		this.deviceSpecifications = deviceSpecifications;
	}

	/**
	 * @return the holidays
	 */
	public List<HolidayDto> getHolidays() {
		return holidays;
	}

	/**
	 * @param holidays the holidays to set
	 */
	public void setHolidays(List<HolidayDto> holidays) {
		this.holidays = holidays;
	}

	/**
	 * @return the documentCategories
	 */
	public List<DocumentCategoryDto> getDocumentCategories() {
		return documentCategories;
	}

	/**
	 * @param documentCategories the documentCategories to set
	 */
	public void setDocumentCategories(List<DocumentCategoryDto> documentCategories) {
		this.documentCategories = documentCategories;
	}

	/**
	 * @return the documentTypes
	 */
	public List<DocumentTypeDto> getDocumentTypes() {
		return documentTypes;
	}

	/**
	 * @param documentTypes the documentTypes to set
	 */
	public void setDocumentTypes(List<DocumentTypeDto> documentTypes) {
		this.documentTypes = documentTypes;
	}

	/**
	 * @return the validDocumentMapping
	 */
	public List<ValidDocumentDto> getValidDocumentMapping() {
		return validDocumentMapping;
	}

	/**
	 * @param validDocumentMapping the validDocumentMapping to set
	 */
	public void setValidDocumentMapping(List<ValidDocumentDto> validDocumentMapping) {
		this.validDocumentMapping = validDocumentMapping;
	}

	/**
	 * @return the templates
	 */
	public List<TemplateDto> getTemplates() {
		return templates;
	}

	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(List<TemplateDto> templates) {
		this.templates = templates;
	}

	/**
	 * @return the templatesTypes
	 */
	public List<TemplateTypeDto> getTemplatesTypes() {
		return templatesTypes;
	}

	/**
	 * @param templatesTypes the templatesTypes to set
	 */
	public void setTemplatesTypes(List<TemplateTypeDto> templatesTypes) {
		this.templatesTypes = templatesTypes;
	}

	/**
	 * @return the templateFileFormat
	 */
	public List<TemplateFileFormatDto> getTemplateFileFormat() {
		return templateFileFormat;
	}

	/**
	 * @param templateFileFormat the templateFileFormat to set
	 */
	public void setTemplateFileFormat(List<TemplateFileFormatDto> templateFileFormat) {
		this.templateFileFormat = templateFileFormat;
	}

	/**
	 * @return the reasonCategory
	 */
	public List<PostReasonCategoryDto> getReasonCategory() {
		return reasonCategory;
	}

	/**
	 * @param reasonCategory the reasonCategory to set
	 */
	public void setReasonCategory(List<PostReasonCategoryDto> reasonCategory) {
		this.reasonCategory = reasonCategory;
	}

	/**
	 * @return the reasonList
	 */
	public List<MasterReasonListDto> getReasonList() {
		return reasonList;
	}

	/**
	 * @param reasonList the reasonList to set
	 */
	public void setReasonList(List<MasterReasonListDto> reasonList) {
		this.reasonList = reasonList;
	}

	/**
	 * @return the blackListedWords
	 */
	public List<BlacklistedWordsDto> getBlackListedWords() {
		return blackListedWords;
	}

	/**
	 * @param blackListedWords the blackListedWords to set
	 */
	public void setBlackListedWords(List<BlacklistedWordsDto> blackListedWords) {
		this.blackListedWords = blackListedWords;
	}

	/**
	 * @return the locationHierarchy
	 */
	public List<LocationDto> getLocationHierarchy() {
		return locationHierarchy;
	}

	/**
	 * @param locationHierarchy the locationHierarchy to set
	 */
	public void setLocationHierarchy(List<LocationDto> locationHierarchy) {
		this.locationHierarchy = locationHierarchy;
	}

	/**
	 * @return the biometricattributes
	 */
	public List<BiometricAttributeDto> getBiometricattributes() {
		return biometricattributes;
	}

	/**
	 * @param biometricattributes the biometricattributes to set
	 */
	public void setBiometricattributes(List<BiometricAttributeDto> biometricattributes) {
		this.biometricattributes = biometricattributes;
	}

	/**
	 * @return the biometricTypes
	 */
	public List<BiometricTypeDto> getBiometricTypes() {
		return biometricTypes;
	}

	/**
	 * @param biometricTypes the biometricTypes to set
	 */
	public void setBiometricTypes(List<BiometricTypeDto> biometricTypes) {
		this.biometricTypes = biometricTypes;
	}

	/**
	 * @return the applications
	 */
	public List<ApplicationDto> getApplications() {
		return applications;
	}

	/**
	 * @param applications the applications to set
	 */
	public void setApplications(List<ApplicationDto> applications) {
		this.applications = applications;
	}

	/**
	 * @return the idTypes
	 */
	public List<IdTypeDto> getIdTypes() {
		return idTypes;
	}

	/**
	 * @param idTypes the idTypes to set
	 */
	public void setIdTypes(List<IdTypeDto> idTypes) {
		this.idTypes = idTypes;
	}

	/**
	 * @return the titles
	 */
	public List<TitleDto> getTitles() {
		return titles;
	}

	/**
	 * @param titles the titles to set
	 */
	public void setTitles(List<TitleDto> titles) {
		this.titles = titles;
	}

	/**
	 * @return the genders
	 */
	public List<GenderDto> getGenders() {
		return genders;
	}

	/**
	 * @param genders the genders to set
	 */
	public void setGenders(List<GenderDto> genders) {
		this.genders = genders;
	}

	/**
	 * @return the languages
	 */
	public List<LanguageDto> getLanguages() {
		return languages;
	}

	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(List<LanguageDto> languages) {
		this.languages = languages;
	}

}
