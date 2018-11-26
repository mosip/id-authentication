package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.Data;

/**
 * The dto class for document category list.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class DocumentCategoryListDto {
	private List<DocumentCategoryDto> documentCategories;
}
