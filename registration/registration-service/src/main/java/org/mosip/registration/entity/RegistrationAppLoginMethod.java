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
@Table(schema = "reg", name = "app_login_method")
public class RegistrationAppLoginMethod {
	@EmbeddedId
	private RegistrationAppLoginMethodID pk_applm_usr_id;

	@Column(name = "method_seq", nullable = true, updatable = false)
	private int methodSeq;
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
