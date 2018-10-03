package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "reg", name = "app_login_method")
public class RegistrationAppLoginMethod extends RegistrationCommonFields {
	@EmbeddedId
	private RegistrationAppLoginMethodID pk_applm_usr_id;

	@Column(name = "method_seq", nullable = true, updatable = false)
	private int methodSeq;
}
