package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

	@NotBlank
	private String columnName;

	@NotBlank
	private String type;
}
