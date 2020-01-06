package io.mosip.kernel.lkeymanager.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermission;
import lombok.Data;

/**
 * ID class for {@link LicenseKeyPermission}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Embeddable
@Data
public class LicenseKeyPermissionID implements Serializable {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -2416988903449810629L;
	/**
	 * The License key.
	 */
	@Column(name = "license_key", nullable = false, length = 255)
	private String lKey;
	/**
	 * The permission for the license key.
	 */
	@Column(name = "permission", nullable = false, length = 255)
	private String permission;
}
