package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.Data;

@Data
public class DocumentCategoryListDto {
	private List<DocumentCategoryDto> documentCategories;
}
