package io.mosip.kernel.lkeymanager.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO class for license key fetch response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class LicenseKeyFetchResponseDto {
	/**
	 * List of mapped permissions.
	 */
	private List<String> mappedPermissions;
}
