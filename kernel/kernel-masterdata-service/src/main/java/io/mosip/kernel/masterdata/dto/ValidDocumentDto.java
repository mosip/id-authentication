package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data


public class ValidDocumentDto {

	@NotBlank
	@Size(min = 1, max = 36)
	private String docTypeCode;

	@NotBlank
	@Size(min = 1, max = 36)
	private String docCategoryCode;

	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;

	@NotBlank
	private Boolean isActive;
}
