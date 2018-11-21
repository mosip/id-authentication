
package io.mosip.kernel.masterdata.dto;

/**
 * Request DTO for Device Type 
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
import java.util.List;

import lombok.Data;

@Data
public class DeviceTypeRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private List<DeviceTypeDto> deviceTypeDtoRequest;
}
