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
@Table(name="uin", schema="ida")
public class UinEntity {	

	@NotNull
	@Column(name = "id", nullable = false)
	String id;

	@Id
	@NotNull
	@Column(name = "uin", unique = true, nullable = false)
	String uin;

	@Column(name = "is_active")
	boolean isActive;

	@Column(name = "cr_by")
	String createdBy;

	@Column(name = "cr_dtimes")
	Date createdOn;

	@Column(name = "upd_by")
	String updatedBy;

	@Column(name = "upd_dtimes")
	Date updatedOn;

	@Column(name = "is_deleted")
	boolean isDeleted;

	@Column(name = "del_dtimes")
	Date deletedOn;

}
