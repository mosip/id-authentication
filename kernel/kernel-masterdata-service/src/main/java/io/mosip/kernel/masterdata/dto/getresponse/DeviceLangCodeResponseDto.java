
package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
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
public class DeviceLangCodeResponseDto {
	List<DeviceLangCodeDtypeDto> devices;
}
