package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data

public class LocationDto {

	@NotNull
	@Size(min = 1, max = 36)
	@NotEmpty
	@NotBlank
	private String code;

	@NotNull
	@Size(min = 1, max = 128)
	@NotEmpty
	@NotBlank
	private String name;

	@NotNull
	private int hierarchyLevel;

	@NotNull
	@Size(min = 1, max = 64)
	@NotEmpty
	@NotBlank
	private String hierarchyName;

	@NotNull
	@Size(min = 1, max = 32)
	@NotEmpty
	@NotBlank
	private String parentLocCode;

	@NotNull
	@Size(min = 1, max = 3)
	@NotBlank
	@NotEmpty
	private String langCode;

	@NotNull
	private Boolean isActive;

}
