package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeData {

	@NotNull
	@Size(min = 1, max = 36)
	private String code;

	@NotNull
	@Size(min = 1, max = 64)
	private String name;

	@Size(min = 1, max = 128)
	private String description;

	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;

}
