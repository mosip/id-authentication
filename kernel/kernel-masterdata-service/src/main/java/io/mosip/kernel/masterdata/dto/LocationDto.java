package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.mosip.kernel.masterdata.validator.CustomIntegerDeserializer;
import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data

public class LocationDto {

	@Size(min = 1, max = 36)
	@NotBlank
	private String code;

	@Size(min = 1, max = 128)
	@NotBlank
	private String name;

	@JsonDeserialize(using=CustomIntegerDeserializer.class)
	@Range(min=0)
    private int hierarchyLevel;

	@Size(min = 1, max = 64)
	@NotBlank
	private String hierarchyName;

	
	private String parentLocCode;

	@ValidLangCode
	@Size(min = 1, max = 3)
	@NotBlank
	private String langCode;

	@NotNull
	private Boolean isActive;

}
