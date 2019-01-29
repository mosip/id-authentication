package io.mosip.kernel.lkeymanager.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyID;
import lombok.Data;

/**
 * Entity class for License key.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@IdClass(LicenseKeyID.class)
@Table(schema = "master", name = "lkey")
public class LicenseKey {
	/**
	 * Attributes of the primary key : TSP ID, License Key.
	 */
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "tsp_id", column = @Column(name = "tsp_id", nullable = false)),
			@AttributeOverride(name = "lkey", column = @Column(name = "lkey", nullable = false)) })
	private String tspId;
	private String lKey;
	/**
	 * The license created by.
	 */
	@Column(name = "cr_by")
	private String createdBy;
	/**
	 * The license created at.
	 */
	@Column(name = "cr_at")
	private LocalDateTime createdAt;
	/**
	 * Mapping to {@link LicenseKeyPermissions}.
	 */
	@OneToMany(mappedBy = "licenseKey", fetch = FetchType.LAZY)
	private List<LicenseKeyPermissions> licenseKeyPermissions;
}
