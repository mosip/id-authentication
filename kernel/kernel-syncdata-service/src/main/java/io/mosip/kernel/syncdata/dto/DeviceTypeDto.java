package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Device Type DTO
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDto {

	private String code;

	private String langCode;

	private String name;

	private String description;
	private Boolean isActive;
}
