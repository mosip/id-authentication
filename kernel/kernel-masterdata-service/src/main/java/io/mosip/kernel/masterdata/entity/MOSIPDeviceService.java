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
 * The Entity MOSIPDeviceService.
 * 
 * @author Megha Tanga
 */
@Entity
@Table(name = "mosip_device_service", schema = "master")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MOSIPDeviceService extends BaseEntity {

	/** The id. */
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String id;

	/** The sw binary hash. */
	@Column(name = "sw_binary_hash", nullable = false)
	private byte[] swBinaryHash;

	/** The sw version. */
	@Column(name = "sw_version", nullable = false, length = 64)
	private String swVersion;

	/** The d provider id. */
	@Column(name = "dprovider_id", nullable = false, length = 36)
	private String deviceProviderId;

	/** The d type code. */
	@Column(name = "dstype_code", nullable = false, length = 36)
	private String regDeviceSubCode;

	/** The ds type code. */
	@Column(name = "dtype_code", nullable = false, length = 36)
	private String regDeviceTypeCode;

	/** The make. */
	@Column(name = "make", length = 36)
	private String make;

	/** The model. */
	@Column(name = "model", length = 36)
	private String model;

	/** The sw created time. */
	@Column(name = "sw_cr_dtimes")
	private LocalDateTime swCreateDateTime;

	/** The sw expiry time. */
	@Column(name = "sw_expiry_dtimes")
	private LocalDateTime swExpiryDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dprovider_id", referencedColumnName = "id", insertable = false, updatable = false)
	private DeviceProvider deviceProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dtype_code", referencedColumnName = "code", insertable = false, updatable = false)
	private RegistrationDeviceType registrationDeviceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dstype_code", referencedColumnName = "code", insertable = false, updatable = false)
	private RegistrationDeviceSubType registrationDeviceSubType;
}
