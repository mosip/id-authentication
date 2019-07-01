package io.mosip.kernel.masterdata.dto.response;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterResponseDto {
	@NotBlank
	private String fieldID;
	@NotBlank
	private String fieldValue;
}
