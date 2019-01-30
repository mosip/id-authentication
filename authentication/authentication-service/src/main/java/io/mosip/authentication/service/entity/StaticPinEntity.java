package io.mosip.authentication.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author Prem Kumar
 * 
 * This class Instantiates a new Static Pin entity.
 */
@Data
@Entity
@Table(name="static_pin", schema="ida")
public class StaticPinEntity {
	/** The pin. */
	@NotNull
	@Column(name = "pin", nullable = false)
	private String pin;
	
	/** The uin */
	@Id
	@NotNull
	@Column(name = "uin", unique = true, nullable = false)
	private String uin;
	
	/** The generated on. */
	@Column(name = "generated_dtimes")
	private Date generatedOn;
	
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
	private String updatedBy;

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
