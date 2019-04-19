package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.Data;

/**
 * Response Dto for valid list of document categories with their document types
 * 
 * @author Neha Sinha
 * @since 1.0.0
 *
 */
@Data
public class ValidDocCategoryAndDocTypeResponseDto {
	
	private List<ValidDocCategoryDto> documentcategories;
}
