package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
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
	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	private String name;

	@Range(min = 0)
	private short hierarchyLevel;

	@Size(min = 1, max = 64)
	@NotBlank
	private String hierarchyName;

	private String parentLocCode;

	@Size(min = 1, max = 3)
	@NotBlank
	private String langCode;

	@NotNull
	private Boolean isActive;

}
