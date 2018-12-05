package io.mosip.kernel.synchandler.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
