package io.mosip.authentication.common.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
@Table(name = "uin_auth_lock", schema = "ida")
@Entity
@IdClass(AuthtypeLock.Compositeclass.class)
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

	@Id
	@NotNull
	@Column(name = "lock_request_datetime")
	private LocalDateTime lockrequestDTtimes;

	@Id
	@NotNull
	@Column(name = "lock_start_datetime")
	private LocalDateTime lockstartDTtimes;

	@Column(name = "lock_end_datetime")
	private LocalDateTime lockendDTtimes;

	@NotNull
	@Column(name = "status_code")
	private String statuscode;

	@NotNull
	@Size(max = 3)
	@Column(name = "lang_code")
	private String langCode;

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

	@Data
	static class Compositeclass implements Serializable {
		private static final long serialVersionUID = 1L;
		private String uin;
		private LocalDateTime lockrequestDTtimes;
		private LocalDateTime lockstartDTtimes;
	}

}
