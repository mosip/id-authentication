package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO class for Device specification get operation
 * 
 * @author Uday
 * @version 1.0.0
 */
@Data
@AllArgsConstructor

public class DeviceSpecificationResponseDto {
	private List<DeviceSpecificationDto> devicespecifications;
}
