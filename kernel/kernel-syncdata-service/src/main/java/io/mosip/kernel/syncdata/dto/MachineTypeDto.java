package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MachineTypeDto extends BaseDto{
	
	private String code;

	private String langCode;

	private String name;

	private String description;
	private Boolean isActive;

}
