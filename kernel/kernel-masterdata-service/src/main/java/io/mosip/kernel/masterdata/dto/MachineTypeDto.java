package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class MachineTypeDto {
	
	private String code;

	private String langCode;

	private String name;

	private String description;
	private Boolean isActive;

}
