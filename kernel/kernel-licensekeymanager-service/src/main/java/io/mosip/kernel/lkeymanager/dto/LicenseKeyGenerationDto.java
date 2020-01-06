package io.mosip.kernel.lkeymanager.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * DTO class to provide input request for generation of license key.
 * 
 * @author Sagar Mahapatra
 * @since 1.0
 *
 */
@Data
public class LicenseKeyGenerationDto {
	/**
	 * The TSP ID against which the license key is to be generated.
	 */
	private String tspId;
	/**
	 * The time at which the license key will expire.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime licenseExpiryTime;
}
