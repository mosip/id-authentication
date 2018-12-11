package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRrgistrationCenterMachineDeviceDto {

	private String regCenterId;

	private String deviceId;

	private String machineId;

}
