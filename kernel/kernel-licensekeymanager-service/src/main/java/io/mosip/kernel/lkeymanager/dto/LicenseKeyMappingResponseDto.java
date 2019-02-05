package io.mosip.kernel.lkeymanager.dto;

import lombok.Data;

/**
 * DTO class for licensekey-permission mapping response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class LicenseKeyMappingResponseDto {
	/**
	 * The status of the licensekey-permission mapping.
	 */
	private String status;
}
