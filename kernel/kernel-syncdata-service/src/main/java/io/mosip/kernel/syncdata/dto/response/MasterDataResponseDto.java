package io.mosip.kernel.syncdata.dto.response;

import java.util.List;

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
import lombok.Data;

/**
 * masterdata sync handler response dto
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
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

}
