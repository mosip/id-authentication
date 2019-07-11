package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

@Data
public class BlackListedWordsUpdateDto {

	@NotBlank
	@Size(min = 1, max = 128)
	private String word;

	@NotBlank
	@Size(min = 1, max = 128)
	private String oldWord;

	@ValidLangCode
	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;

	@Size(min = 1, max = 256)
	private String description;

	@NotNull
	private Boolean isActive;

}