package io.mosip.kernel.syncdata.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSpecificationDto extends BaseDto{
	private String id;
	private String name;
	private String brand;
	private String model;
	private String deviceTypeCode;
	private String minDriverversion;
	private String description;
	private String langCode;
	private Boolean isActive;
}
