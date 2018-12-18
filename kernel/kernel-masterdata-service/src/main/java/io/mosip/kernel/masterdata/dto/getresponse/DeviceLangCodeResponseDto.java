
package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import lombok.Data;


@Data


public class DeviceLangCodeResponseDto {
	List<DeviceLangCodeDtypeDto> devices;
}
