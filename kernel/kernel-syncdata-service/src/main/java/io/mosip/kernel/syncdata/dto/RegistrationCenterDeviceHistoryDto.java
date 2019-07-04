/**
 * 
 *
 */

package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response dto for Registration Center Device History
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterDeviceHistoryDto extends BaseDto {

	/**
	 * Field for reg center id
	 */
	private String regCenterId;
	/**
	 * Field for device id
	 */
	private String deviceId;

	/**
	 * Field to hold Effective Date and time
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime effectivetimes;

}
