package io.mosip.kernel.lkeymanager.dto;

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
}
