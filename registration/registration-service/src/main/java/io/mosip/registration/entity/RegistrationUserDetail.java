package io.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(schema="reg", name = "user_detail")
public class RegistrationUserDetail extends RegistrationCommonFields {	
	@Id
	@Column(name="id", length=64, nullable=false, updatable=false)
	private String id;
	@Column(name="name", length=64, nullable=false, updatable=false)
	private String name;
	@Column(name="email", length=64, nullable=true, updatable=false)
	private String email;
	@Column(name="mobile", length=16, nullable=true, updatable=false)
	private String mobile;
	@Column(name="cntr_id", length=28, nullable=false, updatable=false)
	private String cntrId;
	@Column(name="lang_code", length=3, nullable=false, updatable=false)
	private String langCode;
	@Column(name="last_login_dtimes", nullable=true, updatable=false)
	private OffsetDateTime lastLoginDtimes;
	@Column(name="last_login_method", length=64, nullable=true, updatable=false)
	private String lastLoginMethod;
	
	@Column(name="is_deleted", nullable=false, updatable=false)
	@Type(type= "true_false")
	private boolean isDeleted;
	@Column(name="del_dtimes", nullable=true, updatable=false)
	private OffsetDateTime delDtimes;
}
