package org.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(schema = "reg", name = "user_pwd")
public class RegistrationUserPassword {
	@EmbeddedId
	private RegistrationUserPasswordID registrationUserPasswordID;

	@Column(name = "pwd_expiry_dtimes", nullable = false, updatable = false)
	private OffsetDateTime pwdExpiryDtimes;
	@Column(name = "status_code", length = 64, nullable = true, updatable = false)
	private String statusCode;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;
	@Column(name = "is_active", nullable = false, updatable = false)
	@Type(type = "true_false")
	private boolean isActive;
	@Column(name = "cr_by", length = 24, nullable = false, updatable = false)
	private String crBy;
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	private OffsetDateTime crDtimes;
	@Column(name = "upd_by", length = 24, nullable = true, updatable = false)
	private String updBy;
	@Column(name = "upd_dtimes", nullable = true, updatable = false)
	private OffsetDateTime updDtimes;

}
