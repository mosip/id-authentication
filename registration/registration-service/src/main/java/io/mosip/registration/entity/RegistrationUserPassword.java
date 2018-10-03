package io.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "reg", name = "user_pwd")
public class RegistrationUserPassword extends RegistrationCommonFields {
	@EmbeddedId
	private RegistrationUserPasswordID registrationUserPasswordID;

	@Column(name = "pwd_expiry_dtimes", nullable = false, updatable = false)
	private OffsetDateTime pwdExpiryDtimes;
	@Column(name = "status_code", length = 64, nullable = true, updatable = false)
	private String statusCode;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;
}
