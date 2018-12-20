package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

	@NotNull
	@Size(min = 1, max = 16)
	private String code;

	@NotNull
	@Size(min = 1, max = 64)
	private String titleName;

	@Size(min = 1, max = 128)
	private String titleDescription;

	@NotNull
	private Boolean isActive;

	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

}
