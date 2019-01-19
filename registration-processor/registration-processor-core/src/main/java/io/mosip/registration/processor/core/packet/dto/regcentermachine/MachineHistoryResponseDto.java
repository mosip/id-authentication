package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new machine history response dto.
 *
 * @param machineHistoryDetails the machine history details
 */
@AllArgsConstructor

/**
 * Instantiates a new machine history response dto.
 */
@NoArgsConstructor
public class MachineHistoryResponseDto {
	
	/** The machine history details. */
	private List<MachineHistoryDto> machineHistoryDetails;
}
