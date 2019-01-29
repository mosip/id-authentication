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

import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyPermissionsID;
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
@IdClass(LicenseKeyPermissionsID.class)
@Table(schema = "master", name = "lkey_permissions")
public class LicenseKeyPermissions {
	/**
	 * Attributes of the primary key : TSP ID, License Key, Permission.
	 */
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "tsp_id", column = @Column(name = "tsp_id", nullable = false)),
			@AttributeOverride(name = "lkey", column = @Column(name = "lkey", nullable = false)),
			@AttributeOverride(name = "permission", column = @Column(name = "permission", nullable = false)) })
	private String tspId;
	private String lKey;
	private String permission;
	/**
	 * The license permission created at.
	 */
	@Column(name = "cr_at", nullable = false)
	private LocalDateTime createdAt;
	/**
	 * Mapping to {@link LicenseKey}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "tsp_id", referencedColumnName = "tsp_id", insertable = false, updatable = false),
			@JoinColumn(name = "lkey", referencedColumnName = "lkey", insertable = false, updatable = false), })
	private LicenseKey licenseKey;
}
