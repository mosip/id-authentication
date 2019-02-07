package io.mosip.kernel.lkeymanager.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyPermissionID;
import lombok.Data;

/**
 * Entity class for License key permissions.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@IdClass(LicenseKeyPermissionID.class)
@Table(schema = "master", name = "licensekey_permission")
public class LicenseKeyPermission {
	/**
	 * Composite Primary ID : License Key & Permission.
	 */
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "license_key", column = @Column(name = "license_key", nullable = false, length = 255)),
			@AttributeOverride(name = "permission", column = @Column(name = "permission", nullable = false, length = 512)) })
	private String lKey;
	private String permission;
	/**
	 * The active state of permission.
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;
	/**
	 * The permission created by.
	 */
	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;
	/**
	 * The permission created at.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTimes;
	/**
	 * The permission updated by.
	 */
	@Column(name = "upd_by", length = 32)
	private String updatedBy;
	/**
	 * The permission updated at.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTimes;
	/**
	 * The deletion state of permission.
	 */
	@Column(name = "is_deleted")
	private boolean isDeleted;
	/**
	 * The permission deleted at.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTimes;

	/**
	 * Many to One mapping.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "license_key", referencedColumnName = "license_key", insertable = false, updatable = false), })
	private LicenseKeyList licenseKeyListReference;
}
