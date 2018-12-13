package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ValidDocumentDto extends BaseDto{

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
