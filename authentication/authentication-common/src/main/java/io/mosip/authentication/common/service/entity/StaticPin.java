package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class Instantiates a new Static Pin entity.
 * 
 * @author Prem Kumar
 * 
 *
 */
@Data
@Entity
@Table(name = "static_pin", schema = "ida")
@AllArgsConstructor
@NoArgsConstructor
public class StaticPin {
	/** The pin. */
	@Column(name = "pin", nullable = false)
	private String pin;

	/** The uin */
	@Id
	@Column(name = "uin", unique = true, nullable = false)
	private String uin;

	/** The is active. */
	@Column(name = "is_active")
	private boolean isActive;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The created date. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdOn;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The updated on. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedOn;

	/** The StaticPin is deleted or not. */
	@Column(name = "is_deleted")
	private boolean isDeleted;

	/** The StaticPin deleted on. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedOn;
}
