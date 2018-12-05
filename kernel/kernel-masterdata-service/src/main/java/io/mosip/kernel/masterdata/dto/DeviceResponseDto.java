
package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDto {
	private List<DeviceDto> devices;
}
