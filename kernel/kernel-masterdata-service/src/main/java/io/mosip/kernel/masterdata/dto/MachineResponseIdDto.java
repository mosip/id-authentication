package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO to return single DTO of machineDto for ID and LangCode
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data


public class MachineResponseIdDto {
	private MachineDto machineDto;
}
