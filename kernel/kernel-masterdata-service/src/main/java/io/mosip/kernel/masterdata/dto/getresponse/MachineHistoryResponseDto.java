
package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import lombok.Data;

/**
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data


public class MachineHistoryResponseDto {
	private List<MachineHistoryDto> machineHistoryDetails;
}

