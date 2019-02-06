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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyTspMapID;
import lombok.Data;

/**
 * Entity class for License key and TSP ID mapping.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@IdClass(LicenseKeyTspMapID.class)
@Table(schema = "master", name = "tsp_licensekey_map")
public class LicenseKeyTspMap {
	/**
	 * Attributes of the primary key : TSP ID, License Key.
	 */
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "tsp_id", column = @Column(name = "tsp_id", nullable = false, length = 36)),
			@AttributeOverride(name = "license_key", column = @Column(name = "license_key", nullable = false, length = 255)) })
	private String tspId;
	private String lKey;
	/**
	 * The active state of licensekey-tsp mapping.
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;
	/**
	 * The map created by.
	 */
	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;
	/**
	 * The map created at.
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTimes;
	/**
	 * The map updated by.
	 */
	@Column(name = "upd_by", length = 32)
	private String updatedBy;
	/**
	 * The map updated at.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDTimes;
	/**
	 * The deletion state of map.
	 */
	@Column(name = "is_deleted")
	private boolean isDeleted;
	/**
	 * The deletion time of map.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTimes;
	/**
	 * One To One mapping.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "license_key", insertable = false, updatable = false) })
	private LicenseKeyList licenseKeyList;
}
