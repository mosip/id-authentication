package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data


public class ValidDocumentDto {

	@NotNull
	@Size(min = 1, max = 36)
	private String docTypeCode;

	@NotNull
	@Size(min = 1, max = 36)
	private String docCategoryCode;

	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;
}
