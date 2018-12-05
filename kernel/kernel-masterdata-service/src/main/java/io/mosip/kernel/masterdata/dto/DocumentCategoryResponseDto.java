package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCategoryResponseDto {
	private List<DocumentCategoryDto> documentcategories;
}
