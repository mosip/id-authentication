package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String code;

	@NotNull
	@Size(min = 1, max = 128)
	private String name;

	@NotNull
    private int hierarchyLevel;

	@NotNull
	@Size(min = 1, max = 64)
	private String hierarchyName;

	@NotNull
	@Size(min = 1, max = 32)
	private String parentLocCode;

	@NotNull
	@Size(min = 1, max = 3)
	private String languageCode;

	@NotNull
    private Boolean isActive;

}
