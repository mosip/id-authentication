package io.mosip.kernel.masterdata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSpecificationDto {
	private String id;
	private String name;
	private String brand;
	private String model;
	private String deviceTypeCode;
	private String minDriverversion;
	private String description;
	private String langCode;
	private Boolean isActive;
	//private DeviceTypeDto deviceTypeDto;
}
