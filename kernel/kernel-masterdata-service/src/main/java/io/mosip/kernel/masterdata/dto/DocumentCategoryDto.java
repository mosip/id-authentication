package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for Document Category Detail
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCategoryDto {

	private String code;

	private String name;

	private String description;

	private String langCode;
	private Boolean isActive;

}
