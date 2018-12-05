package io.mosip.kernel.synchandler.dto;

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
public class ResponseRegistrationCenterDeviceDto {

	private String regCenterId;

	private String deviceId;

}
