package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for Device Detail
 * 
 * @author Megha Tanga
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {

	/**
	 * Field for device id
	 */
	@NotNull
	@Size(min = 1, max = 36)
	private String code;
	/**
	 * Field for device name
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String name;
	/**
	 * Field for device serial number
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String serialNum;
	/**
	 * Field for device device specification Id
	 */
	@NotNull
	@Size(min = 1, max = 36)
	private String deviceSpecId;
	/**
	 * Field for device mac address
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String macAddress;
	/**
	 * Field for device ip address
	 */
	private String ipAddress;
	/**
	 * Field for language code
	 */
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;
	/**
	 * Field for is active
	 */
	@NotNull
	private Boolean isActive;

	private LocalDateTime validityEndDate;

}
