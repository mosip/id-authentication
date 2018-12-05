package io.mosip.kernel.synchandler.dto.response;

import java.util.List;

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
	private List<TemplateFileFormatDto> templateFileType;
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

}
