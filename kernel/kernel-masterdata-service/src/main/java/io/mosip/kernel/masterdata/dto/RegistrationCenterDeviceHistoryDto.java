/**
 * 
 *
 */

package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Response dto for Registration Center Device History
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Data
public class RegistrationCenterDeviceHistoryDto {

	/**
	 * Field for reg center id
	 */
	private String regCenterId;
	/**
	 * Field for device id
	 */
	private String deviceId;
	/**
	 * Field for is active
	 */
	private Boolean isActive;

	/**
	 * Field to hold Effective Date and time
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime effectivetimes;

}
