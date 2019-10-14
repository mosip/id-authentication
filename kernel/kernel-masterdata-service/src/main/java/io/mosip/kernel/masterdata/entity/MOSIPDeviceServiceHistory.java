package io.mosip.kernel.masterdata.entity;



import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 *
 */

@Data
@Entity
@Table(name="mosip_device_service_h", schema="master")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MOSIPDeviceServiceHistory extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4926799412583761896L;
	
	@Id
	@Column(name="id", nullable=false, length=36)
	private String id;

	@Column(name="sw_version", nullable = false, length = 64, unique=true)
	private String swVersion;
	
	/** The d provider id. */
	@Column(name = "dprovider_id", nullable=false, length=36)
	private String deviceProviderId;

	/** The d type code. */
	@Column(name = "dstype_code", nullable=false, length=36)
	private String regDeviceSubCode;

	/** The ds type code. */
	@Column(name = "dtype_code", nullable= false, length=36)
	private String regDeviceTypeCode;
	
	@Column(name="make",nullable = false,length = 36, unique=true)
	private String make;
	
	@Column(name="model", nullable = false, length = 36, unique=true)
	private String model;
	
	@Column(name = "sw_cr_dtimes", nullable = false)
	private LocalDateTime swCreateDateTime;
	
	@Column(name = "sw_expiry_dtimes", nullable = false)
	private LocalDateTime swExpiryDateTime;
	
	@Column(name = "sw_binary_hash", nullable = false)
	private byte[] swBinaryHash;
	
	@Column(name="eff_dtimes", nullable = false)
	private LocalDateTime effectDateTime;
	
	
	
}

