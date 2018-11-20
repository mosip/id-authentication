package io.mosip.kernel.masterdata.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDto {
	/**
	 * Field for Device code
	 */
	
	private String code;
	private String langCode;
	
	private String name;
	/**
	 * Field for language code
	 */
	
	
	private String description;

}
