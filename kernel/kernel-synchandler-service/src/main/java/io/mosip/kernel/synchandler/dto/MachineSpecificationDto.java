package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineSpecificationDto {
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
