package io.mosip.kernel.lkeymanager.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class to represent the license key list table.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@Table(schema = "master", name = "licensekey_list")
public class LicenseKeyList {
	/**
	 * The generated unique license key.
	 */
	@Id
	@Column(name = "license_key", nullable = false, length = 255)
	private String licenseKey;
	/**
	 * The active state of license key.
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;
	/**
	 * The license key expires at.
	 */
	@Column(name = "expiry_dtime")
	private LocalDateTime expiryDateTimes;

	/**
	 * The license key created at.
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdAt;
	/**
	 * The license key created by.
	 */
	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;
	/**
	 * The license key created at.
	 */
	@Column(name = "created_dtime")
	private LocalDateTime createdDateTimes;
	/**
	 * The license key updated by.
	 */
	@Column(name = "upd_by", length = 32)
	private String updatedBy;
	/**
	 * The license key updated at.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTimes;
	/**
	 * The deletion state of license key.
	 */
	@Column(name = "is_deleted")
	private boolean isDeleted;
	/**
	 * The license key deleted at.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTimes;

	/**
	 * LicenseKeyList-LicenseKeyPermission Mapping.
	 */
	@OneToMany(mappedBy = "licenseKeyListReference", fetch = FetchType.LAZY)
	private List<LicenseKeyPermission> licenseKeyPermissions;

	/**
	 * LicenseKeyList-LicenseKeyTestMap Mapping.
	 */
	@OneToOne(mappedBy = "licenseKeyList", fetch = FetchType.LAZY)
	private LicenseKeyTspMap licenseKeyTspMap;
}
