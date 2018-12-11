package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineTypeDto {
	
	private String code;

	private String langCode;

	private String name;

	private String description;
	private Boolean isActive;

}
