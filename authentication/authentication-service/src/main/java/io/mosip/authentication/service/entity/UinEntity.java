package io.mosip.authentication.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;



/**
 * This class UinEntity for uin table.
 */
@Entity
@Data
@Table(name="uin", schema="ida")
public class UinEntity {

	/** The id. */
	@Id
	@NotNull
	@Column(name = "uin", nullable = false)
	String uin;

	/** The uin. */
	@NotNull
	@Column(name = "uin_ref_id", unique = true, nullable = false)
	String uinRefId;

	/** The is active. */
	@Column(name = "is_active")
	boolean isActive;

	/** The created by. */
	@Column(name = "cr_by")
	String createdBy;

	/** The created on. */
	@Column(name = "cr_dtimes")
	Date createdOn;

	/** The updated by. */
	@Column(name = "upd_by")
	String updatedBy;

	/** The updated on. */
	@Column(name = "upd_dtimes")
	Date updatedOn;

	/** The is deleted. */
	@Column(name = "is_deleted")
	boolean isDeleted;

	/** The deleted on. */
	@Column(name = "del_dtimes")
	Date deletedOn;

}
