package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;


/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
@Table(name = "uin_auth_lock", schema = "ida")
@Entity
public class AuthtypeLock {

	@Id
	@NotNull
	private String uin;

	@NotNull
	@Column(name = "uin_hash")
	private String hashedUin;

	@NotNull
	@Column(name = "auth_type_code")
	private String authtypecode;

	@NotNull
	@Column(name = "lock_request_datetime")
	private LocalDateTime lockrequestDTtimes;

	@NotNull
	@Column(name = "lock_start_datetime")
	private LocalDateTime lockstartDTtimes;

	@Column(name = "lock_end_datetime")
	private LocalDateTime lockendDTtimes;
	
	@NotNull
	@Column(name = "status_code")
	private String statuscode;

	@NotNull
	@Column(name = "cr_by")
	private String createdBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;

}
