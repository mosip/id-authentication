package io.mosip.authentication.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 *
 * This class Instantiates a new Static Pin History entity.
 * 
 * @author Prem Kumar
 * 
 */
@Data
@Entity
@IdClass(StaticPinHistory.IdClass.class)
@Table(name = "static_pin_h", schema = "ida")
public class StaticPinHistory {
	/** The pin. */
	@NotNull
	@Column(name = "pin", nullable = false)
	private String pin;

	/** The uin */
	@Id
	@NotNull
	@Column(name = "uin", unique = true, nullable = false)
	private String uin;

	/** The is active. */
	@Column(name = "is_active")
	private boolean isActive;

	/** The corrected by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The corrected date. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdOn;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The updated on. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedOn;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private boolean isDeleted;	

	/** The deleted on. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedOn;

	/** The effective date */
	@Id
	@Column(name = "eff_dtimes")
	private LocalDateTime effectiveDate;

	@Data
	static class IdClass implements Serializable {
		/**
		 * serial Version UID
		 */
		private static final long serialVersionUID = 2506101235122193393L;
		private String uin;
		private LocalDateTime effectiveDate;
	}
}
