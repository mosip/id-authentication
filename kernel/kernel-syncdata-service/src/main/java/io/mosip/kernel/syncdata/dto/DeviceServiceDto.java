package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceServiceDto extends BaseDto {

	/** The id. */

	private String id;

	/** The sw binary hash. */

	private byte[] swBinaryHash;

	/** The sw version. */

	private String swVersion;

	/** The d provider id. */

	private String dProviderId;

	/** The device type code. */

	private String dTypeCode;

	/** The ds type code. */

	private String dsTypeCode;

	/** The make. */

	private String make;

	/** The model. */

	private String model;

	/** The sw created time. */

	private LocalDateTime swCreatedTime;

	/** The sw expiry time. */

	private LocalDateTime swExpiryTime;
}
