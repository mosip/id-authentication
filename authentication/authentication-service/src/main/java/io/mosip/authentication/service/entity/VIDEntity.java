package io.mosip.authentication.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Instantiates a new VID entity.
 * @author Rakesh Roshan
 */
@Data
@Entity
@Table(name="vid", schema="ida")
public class VIDEntity {
  
	/** The id. */
	@Id
	@NotNull
	@Column(name = "id", nullable = false)
	private String id;
	
	/** The uin */
	@NotNull
	@Column(name = "uin", unique = true, nullable = false)
	private String uin;

	/** The generated on. */
	@Column(name = "generated_dtimes")
	private Date generatedOn;

	/** The retry count. */
	@Column(name = "validation_retry_count")
	private int retryCount;
	
	/** The expiry date. */
	@Column(name = "expiry_dtimes")
	private Date expiryDate;

	/** The is active. */
	@Column(name = "is_active")
	private boolean isActive;

	/** The corrected by. */
	@Column(name = "cr_by")
	private String createdBy ;         

	/** The corrected date. */
	@Column(name = "cr_dtimes")
	private Date createdDTimes;

	/** The updated by. */
	@Column(name = "upd_by")
	private char updatedBy;

	/** The updated on. */
	@Column(name = "upd_dtimes")
	private Date updatedOn;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private boolean  isDeleted;
	
	/** The deleted on. */
	@Column(name = "del_dtimes")
	private Date deletedOn;
}
