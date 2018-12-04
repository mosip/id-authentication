package io.mosip.kernel.masterdata.dto;

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
public class MachineSpecIdAndId {
	
	/**
	 * Field for machine id
	 */
	private String id;
	/**
	 * Field for machine specification Id
	 */
	private String machineSpecId;

}
