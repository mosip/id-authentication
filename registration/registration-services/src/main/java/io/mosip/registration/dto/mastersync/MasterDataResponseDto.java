package io.mosip.registration.dto.mastersync;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.mosip.registration.dto.ApplicantValidDocumentDto;
import io.mosip.registration.dto.IndividualTypeDto;
import lombok.Data;

/**
 * The Class for MasterDataResponseDto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
	private List<TemplateDto> templates;
	private List<TemplateTypeDto> templatesTypes;
	private List<TemplateFileFormatDto> templateFileFormat;
	private List<PostReasonCategoryDto> reasonCategory;
	private List<ReasonListDto> reasonList;
	private List<BlacklistedWordsDto> blackListedWords;
	private List<LocationDto> locationHierarchy;
	private List<BiometricAttributeDto> biometricattributes;
	private List<BiometricTypeDto> biometricTypes;
	private List<ApplicationDto> applications;
	private List<IdTypeDto> idTypes;
	private List<TitleDto> titles;
	private List<GenderDto> genders;
	private List<LanguageDto> languages;
	private List<RegistrationCenterMachineDto> registrationCenterMachines;
	private List<RegistrationCenterDeviceDto> registrationCenterDevices;
	private List<RegistrationCenterMachineDeviceDto> registrationCenterMachineDevices;
	private List<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachines;
	private List<RegistrationCenterUserDto> registrationCenterUsers;
	private List<ApplicantValidDocumentDto> applicantValidDocuments;
	private List<IndividualTypeDto> individualTypes;
	private List<ValidDocumentDto> validDocumentMapping;
	private List<AppAuthenticationMethodDto> appAuthenticationMethods;
	private List<AppDetailDto> appDetails;
	private List<AppRolePriorityDto> appRolePriorities;
	private List<ScreenAuthorizationDto> screenAuthorizations;
	private List<ProcessListDto> processList;
	private List<SyncJobDefDto> syncJobDefinitions;
	private List<ScreenDetailDto> screenDetails;
	private List<RegisteredDeviceMasterDto> registeredDevices;
	private List<RegisteredDeviceTypeDto> registeredDeviceTypes;
	private List<RegisteredSubDeviceTypeDto> deviceSubTypeDPMs;
	private List<MosipDeviceServiceDto> deviceServices;
	private List<FoundationalTrustProviderDto> functionalTrustProviders;
	private List<DeviceProviderDto> deviceProviders;
}
