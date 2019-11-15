package io.mosip.registration.entity;

import java.sql.Blob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;

/**
 * This Entity Class contains the Mosip device services details
 * from master sync
 * 
 * @author Taleev.Aalam
 * @version 1.0
 */


@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "mosip_device_service", schema = "reg")
public class MosipDeviceService extends RegistrationCommonFields {


	@Id
	@Column(name="id",length=36, nullable=false)
	private String id;
	
	@Column(name="sw_binary_hash", nullable=false)
	private Blob swBinaryHash;
	
	@Column(name="sw_version",length=64, nullable=false)
	private String swVersion;
	
	@Column(name="dprovider_id",length=36, nullable=false)
	private String dProviderId;
	
	@Column(name="dtype_code",length=36, nullable=false)
	private String dtypeCode;
	
	@Column(name="dstype_code",length=36, nullable=false)
	private String dsTypeCode;
	
	@Column(name="make",length=36, nullable=false)
	private String make;
	
	@Column(name="model",length=36, nullable=false)
	private String model;

	@Column(name="sw_cr_dtimes")
	private Timestamp swCrDtimes;
	
	@Column(name="sw_expiry_dtimes")
	private Timestamp swExpiryDtimes;
	
	@Column(name="is_active", nullable=false)
	private boolean isActive;
	
	@Column(name="is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
}
