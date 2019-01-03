package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data

public class PostReasonCategoryDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -845601642085487726L;

	@NotBlank
	@Size(min = 1, max = 36)
	private String code;

	@NotBlank
	@Size(min = 1, max = 64)
	private String name;

	@NotBlank
	@Size(min = 1, max = 128)
	private String description;

	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;

}
