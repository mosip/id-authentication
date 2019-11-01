package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "reg_device_sub_type", schema = "reg")
public class RegisteredSubDeviceType extends RegistrationCommonFields {

	@Id
	@Column(name="code",length=36,nullable=false)
	private String code;
	
	@Column(name="dtyp_code",length=36,nullable=false)
	private String dtypCode;
	
	@Column(name="name",length=64,nullable=false)
	private String name;
	
	@Column(name="descr",length=512,nullable=false)
	private String descr;
	
	@Column(name="is_active", nullable=false)
	private boolean isActive;
	
	@Column(name="is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

}
