package io.mosip.kernel.lkeymanager.dto;

import lombok.Data;

/**
 * DTO class for license key generation response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class LicenseKeyGenerationResponseDto {
	/**
	 * The license key.
	 */
	private String licenseKey;
}
