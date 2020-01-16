package io.mosip.authentication.common.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
@NoArgsConstructor
public class StaticPinHistory {
	/** The variable to hold StaticPin. */
	@Column(name = "pin", nullable = false)
	private String pin;

	/** The variable to hold uin */
	@Id
	@Column(name = "uin", unique = true, nullable = false)
	private String uin;

	/** To check StaticPin is active. */
	@Column(name = "is_active")
	private boolean isActive;

	/** The variable to hold created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The variable to hold created date. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdOn;

	/** The variable to hold updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The variable to hold updated on. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedOn;

	/** To check StaticPin is deleted. */
	@Column(name = "is_deleted")
	private boolean isDeleted;

	/** The variable to hold deleted on. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedOn;

	/** The variable to hold effective date */
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
