
package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import lombok.Data;


@Data


public class DeviceResponseDto {
	private List<DeviceDto> devices;
}
