package io.mosip.registration.entity;

import java.sql.Blob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "registered_device_master", schema = "reg")
public class RegisteredDevice extends RegistrationCommonFields {

	@Id
	@Column(name="code",length=36,nullable=false)
	private String code	;
	
	@Column(name="dtype_code",length=36,nullable=false)
	private String deviceTypeCode;
	
	@Column(name="dstype_code",length=36,nullable=false)
	private String deviceSubTypeCode;
	
	@Column(name="status_code",length=64)
	private String statusCode;
	
	@Column(name="device_id",length=256,nullable=false)
	private String deviceId;
	
	@Column(name="device_sub_id",length=256)
	private String deviceSubId;

	@Column(name="digital_id",length=1024,nullable=false)
	private String digitalId;
	
	@Column(name="serial_number",length=64,nullable=false)
	private String serialNumber;
	
	@Column(name="provider_id",length=36,nullable=false)	
	private String providerId;
	
	@Column(name="provider_name",length=128)
	private String providerName;
	
	@Column(name="purpose",length=64,nullable=false)
	private String purpose;
	
	@Column(name="firmware",length=128)
	private String firmware;
	
	@Column(name="make",length=36,nullable=false)
	private String make;
	
	@Column(name="model",length=36,nullable=false)
	private String model;
	
	@Column(name="expiry_date")
	private Timestamp expiryDate;
	
	@Column(name="certification_level",length=3)
	private String certificationLevel;
	
	@Column(name="foundational_trust_signature",length=512)
	private String foundationalTrustSignature;
	
	@Column(name="foundational_trust_certificate",length=128)
	private Blob foundationalTrustCertificate;
	
	@Column(name="dprovider_signature",length=512)
	private String deviceProviderSignature;

	@Column(name="is_deleted")
	private boolean isDeleted;

	@Column(name="del_dtimes")
	private Timestamp deletedDateTimes;
}
