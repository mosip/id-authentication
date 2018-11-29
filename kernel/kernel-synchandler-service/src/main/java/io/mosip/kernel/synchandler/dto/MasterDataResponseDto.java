package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.Data;

/**
 * masterdata sync handler response dto
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 *
 */
@Data
public class MasterDataResponseDto {
	private List<HolidayDto> holidays;
	private List<RegistrationCenterDto> registrationCenter;
	private List<DocumentCategoryDto> documentCategories;
	private List<DocumentTypeDto> documentTypes;
	private List<TemplateFileFormatDto> templateTypes;
	private List<TemplateDto> templates;
	private List<ReasonCategoryDto> reasonCategory;
	private List<ReasonListDto> packetOnHoldReasons;
	private List<ReasonListDto> packetRejectionReasons;
	private List<BlacklistedWordsDto> blackListedWords;
	private List<LocationDto> locations;
	private List<BiometricAttributeDto> biometricattributes;
	private List<ApplicationDto> applications;
	private List<IdTypeDto> idTypes;
	private List<BiometricTypeDto> biometricTypes;
	private List<TitleDto> titles;
	private List<GenderDto> genders;
	private List<DeviceSpecificationDto> deviceSpecifications;
	private List<DeviceDto> devices;

}
