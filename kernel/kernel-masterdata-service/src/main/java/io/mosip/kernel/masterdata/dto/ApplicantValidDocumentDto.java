package io.mosip.kernel.masterdata.dto;

import java.util.Collection;

import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryAndTypeResponseDto;
import lombok.Data;

@Data
public class ApplicantValidDocumentDto {
	private String appTypeCode;
	private String langCode;
	private Boolean isActive;
	private Collection<DocumentCategoryAndTypeResponseDto> documentCategories;
}
