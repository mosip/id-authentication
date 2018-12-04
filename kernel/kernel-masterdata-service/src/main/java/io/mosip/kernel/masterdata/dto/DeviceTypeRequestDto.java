
package io.mosip.kernel.masterdata.dto;

/**
 * Request DTO for Device Type 
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeRequestDto {

	private String id;
	private String ver;
	private String timestamp;
	private DeviceTypeDtoData request;

}
