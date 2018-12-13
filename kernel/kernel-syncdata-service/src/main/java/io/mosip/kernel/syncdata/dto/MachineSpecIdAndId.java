package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MachineSpecIdAndId extends BaseDto{
	
	/**
	 * Field for machine id
	 */
	private String id;
	/**
	 * Field for machine specification Id
	 */
	private String machineSpecId;

}
