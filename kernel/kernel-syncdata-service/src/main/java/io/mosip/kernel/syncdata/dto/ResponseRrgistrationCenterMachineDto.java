package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRrgistrationCenterMachineDto extends BaseDto{

	private String regCenterId;

	private String machineId;

}
