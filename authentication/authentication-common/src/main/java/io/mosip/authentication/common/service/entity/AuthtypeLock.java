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
import lombok.NoArgsConstructor;

/**
 * The Class AuthtypeLock - Entity class for table uin_auth_lock.
 *
 * @author Dinesh Karuppiah.T
 */
@NoArgsConstructor
@Data
@Table(name = "uin_auth_lock", schema = "ida")
@Entity
@IdClass(AuthtypeLock.Compositeclass.class)
public class AuthtypeLock {

	@Id
	@NotNull
	@Column(name = "token_id")
	private String token;

	@Id
	@NotNull
	@Column(name = "auth_type_code")
	private String authtypecode;

	@Id
	@NotNull
	@Column(name = "lock_request_datetime")
	private LocalDateTime lockrequestDTtimes;

	@NotNull
	@Column(name = "lock_start_datetime")
	private LocalDateTime lockstartDTtimes;

	@Column(name = "lock_end_datetime")
	private LocalDateTime lockendDTtimes;

	@Column(name = "unlock_expiry_datetime")
	private LocalDateTime unlockExpiryDTtimes;

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

	/**
	 * Instantiates a new compositeclass.
	 */
	@Data
	static class Compositeclass implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String token;
		
		private String authtypecode;
		
		private LocalDateTime lockrequestDTtimes;
	}
	
	/**
	 * The constructor used in retrieval of the specific fields.
	 * 
	 * @param authtypecode
	 * @param statuscode
	 */
	public AuthtypeLock(String authtypecode,  String statuscode, LocalDateTime unlockExpiryDTtimes) {
		this.authtypecode = authtypecode;
		this.statuscode = statuscode;
		this.unlockExpiryDTtimes = unlockExpiryDTtimes;
	}
	
	

}
