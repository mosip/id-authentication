package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
@Data
public class UserDetailsDto {

	@NotBlank
	@Size(min = 1, max = 36)
	private String id;

	@NotBlank
	@ValidLangCode
	@Size(min = 1, max = 3)
	private String langCode;

	@Size(min = 1, max = 28)
	private String uin;

	@NotBlank
	@Size(min = 1, max = 64)
	private String name;

	@NotBlank
	@Size(min = 0, max = 16)
	private String email;

	@NotBlank
	@Size(min = 0, max = 16)
	private String mobile;

	@NotBlank
	@Size(min = 1, max = 16)
	private String statusCode;
	
	@NotNull
	private Boolean isActive;

}
