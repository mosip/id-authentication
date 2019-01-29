package io.mosip.kernel.lkeymanager.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermissions;
import lombok.Data;

/**
 * ID class for {@link LicenseKeyPermissions}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Embeddable
@Data
public class LicenseKeyPermissionsID implements Serializable {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -2416988903449810629L;
	/**
	 * The TSP ID.
	 */
	@Column(name = "tsp_id", nullable = false)
	private String tspId;
	/**
	 * The License key.
	 */
	@Column(name = "lkey", nullable = false)
	private String lKey;
	/**
	 * The permission for the license key.
	 */
	@Column(name = "permission", nullable = false)
	private String permission;
}
