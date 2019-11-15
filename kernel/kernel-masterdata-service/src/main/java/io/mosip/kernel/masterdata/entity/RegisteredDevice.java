package io.mosip.kernel.masterdata.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Registered Device Service DTO
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "registered_device_master", schema = "master")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredDevice extends BaseEntity {

	@Id
	@Column(name = "code", length=36)
	private String code;

	@Column(name = "dtype_code", length=36)
	private String deviceTypeCode;

	@Column(name = "dstype_code", length=36)
	private String devicesTypeCode;

	@Column(name = "status_code", length=64)
	private String statusCode;

	@Column(name = "device_id", length=256)
	private String deviceId;

	@Column(name = "device_sub_id", length=256)
	private String deviceSubId;
	
	@Column(name = "purpose", length=64)
	private String purpose;

	@Column(name = "firmware", length=128)
	private String firmware;
	
	
	//json inner class
	@Column(name = "digital_id", length=1024)
	private String digitalId;

	//inner class
	@Column(name = "serial_number", unique=true, length=64)
	private String serialNo;

	//inner class
	@Column(name = "provider_id", unique=true, length=36)
	private String dpId;

	//inner class
	@Column(name = "provider_name", length=128)
	private String dp;

	//inner class
	@Column(name = "make", length=36)
	private String make;

	//inner class
	@Column(name = "model", length=36)
	private String model;

	@Column(name = "expiry_date")
	private LocalDateTime expiryDate;

	@Column(name = "certification_level", length=3)
	private String certificationLevel;

	@Column(name = "foundational_trust_provider_id", length=36)
	private String foundationalTPId;

	@Column(name = "foundational_trust_signature", length=512)
	private String foundationalTrustSignature;

	@Column(name = "foundational_trust_certificate")
	private byte[] foundationalTrustCertificate;

	@Column(name = "dprovider_signature", length=512)
	private String deviceProviderSignature;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id", referencedColumnName = "id", insertable = false, updatable = false)
	private DeviceProvider deviceProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dtype_code", referencedColumnName = "code", insertable = false, updatable = false)
	private RegistrationDeviceType registrationDeviceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dstype_code", referencedColumnName = "code", insertable = false, updatable = false)
	private RegistrationDeviceSubType registrationDeviceSubType;

}
