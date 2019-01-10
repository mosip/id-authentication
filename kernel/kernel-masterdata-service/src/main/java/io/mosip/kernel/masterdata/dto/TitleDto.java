package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * DTO class for fetching titles from masterdata
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data

public class TitleDto {

	@NotBlank
	@Size(min = 1, max = 16)
	private String code;

	@NotBlank
	@Size(min = 1, max = 64)
	private String titleName;

	@Size(min = 1, max = 128)
	private String titleDescription;

	@NotNull
	private Boolean isActive;

	@NotBlank
	@ValidLangCode
	@Size(min = 1, max = 3)
	private String langCode;

}
