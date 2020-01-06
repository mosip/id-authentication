package io.mosip.kernel.lkeymanager.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO class to map license key to a set of permissions.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class LicenseKeyMappingDto {
	/**
	 * The TSP ID to which the set of permissions will be granted.
	 */
	private String tspId;
	/**
	 * The License Key for that TSP ID.
	 */
	private String licenseKey;
	/**
	 * The list of permissions to be given.
	 */
	private List<String> permissions;
}
