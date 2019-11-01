package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "device_provider", schema = "reg")
public class DeviceProvider extends RegistrationCommonFields {

	@Id
	@Column(name="id",length=36, nullable=false)
	private String id;
	
	@Column(name="vendor_name",length=128, nullable=false)
	private String vendorName;
	
	@Column(name="address",length=512)
	private String address;
	
	@Column(name="email",length=512)
	private String email;
	
	@Column(name="contact_number",length=16)
	private String contactNumber;
	
	@Column(name="certificate_alias",length=36)
	private String certificateAlias;

	
	@Column(name="is_active", nullable=false)
	private boolean isActive;
	
	@Column(name="is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private Timestamp delTime;
}
