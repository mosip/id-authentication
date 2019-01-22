package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * UserPassword entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_pwd")
@Getter
@Setter
public class UserPassword extends RegistrationCommonFields {

	@OneToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private UserDetail userDetail;

	@Id
	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "pwd")
	private String pwd;
	@Column(name = "pwd_expiry_dtimes")
	private Timestamp pwdExpiryDtimes;
	@Column(name = "status_code")
	private String statusCode;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

}
