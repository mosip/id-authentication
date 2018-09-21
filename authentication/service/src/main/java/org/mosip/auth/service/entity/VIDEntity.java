package org.mosip.auth.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Table(name="vid", schema="ida")
public class VIDEntity {
  
	@Id
	@NotNull
	@Column(name = "id", nullable = false)
	private String id;

	@NotNull
	@Column(name = "uin_id", unique = true, nullable = false)
	private String refId;

	@Column(name = "generated_dtimes")
	private Date generatedOn;

	@Column(name = "validation_retry_count")
	private int retryCount;
	
	@Column(name = "expiry_dtimes")
	private Date expiryDate;

	@Column(name = "is_active")
	private char isActive;

	@Column(name = "cr_by")
	private String correctedBy ;         

	@Column(name = "cr_dtimes")
	private Date correctedDate;

	@Column(name = "upd_by")
	private char updatedBy;

	@Column(name = "upd_dtimes")
	private Date updatedDate;

	@Column(name = "is_deleted")
	private char  isDeleted;
	
	@Column(name = "del_dtimes")
	private Date deletedOn;
}
