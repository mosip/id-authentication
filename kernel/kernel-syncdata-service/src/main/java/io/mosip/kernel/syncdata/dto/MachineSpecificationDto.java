package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MachineSpecificationDto extends BaseDto{
	private String id;
	private String name;
	private String brand;
	private String model;
	private String machineTypeCode;
	private String minDriverversion;
	private String description;
	private String langCode;
	private Boolean isActive;

}
