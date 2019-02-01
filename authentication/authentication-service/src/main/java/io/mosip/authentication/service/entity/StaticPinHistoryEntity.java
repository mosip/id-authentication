package io.mosip.authentication.service.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
/**
 *
 * This class Instantiates a new Static Pin History entity.
 * 
 *  @author Prem Kumar
 * 
 */
@Data
@Entity
@IdClass(StaticPinHistoryEntity.IdClass.class)
@Table(name="static_pin_h", schema="ida")
public class StaticPinHistoryEntity {
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
	
	/** The effective date */
	@Id
	@Column(name = "eff_dtimes")
	private Date effectiveDate;
	
	   @Data
	    static class IdClass implements Serializable {
		   private String uin;
			private Date effectiveDate;
	    }
}
