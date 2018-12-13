package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto extends BaseDto {

	@NotNull
	@Size(min = 1, max = 3)
	private String code;

	private String name;

	private String description;

	private String langCode;

	private Boolean isActive;

}
