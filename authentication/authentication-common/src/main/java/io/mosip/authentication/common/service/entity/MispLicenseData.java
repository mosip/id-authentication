package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Table(name = "misp_license_data", schema = "ida")
@Entity
public class MispLicenseData {
	
	@Id
	@NotNull
	@Column(name = "misp_id")
	private String mispId;

	@NotNull
	@Column(name = "license_key")
	private String licenseKey;

	@NotNull
	@Column(name = "misp_commence_on")
	private LocalDateTime mispCommenceOn;

	@NotNull
	@Column(name = "misp_expires_on")
	private LocalDateTime mispExpiresOn;

	@NotNull
	@Column(name = "misp_status")
	private String mispStatus;

	@Column(name = "policy_id")
	private String policyId;
	
	@NotNull
	@Column(name = "cr_by")
	private String createdBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;
}
