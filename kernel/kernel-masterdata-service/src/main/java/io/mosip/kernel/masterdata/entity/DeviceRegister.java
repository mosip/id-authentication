package io.mosip.kernel.masterdata.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registered_device_master", schema = "master")
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegister {

	@Id
	@Column(name = "code")
	private String deviceCode;

	@Column(name = "type")
	private String type;

	@Column(name = "subtype")
	private String subType;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "device_sub_id")
	private String deviceSubId;

	@Column(name = "provider_id")
	private String deviceProviderId;

	@Column(name = "provider_name")
	private String deviceProviderName;

	@Column(name = "mosip_process")
	private String mosipProcess;

	@Column(name = "firmware")
	private String firmware;

	@Column(name = "make")
	private String deviceMake;

	@Column(name = "model")
	private String deviceModel;

	@Column(name = "expiry_date")
	private LocalDateTime deviceExpiry;

	@Column(name = "certification")
	private byte[] certification;

	@Column(name = "foundational_trust_provider_iD")
	private String foundationalTrustProviderID;

	@Column(name = "foundational_trust_signature")
	private String foundationalTrustSignature;

	@Column(name = "foundational_trust_certificate")
	private byte[] foundationTrustCertificate;

	@Column(name = "dpsignature")
	private String dpSignature;

	@Column(name = "cr_by", nullable = false, length = 256)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by", length = 256)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;
}
