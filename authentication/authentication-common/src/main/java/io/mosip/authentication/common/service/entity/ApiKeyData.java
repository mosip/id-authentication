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
@Table(name = "api_key_data", schema = "ida")
@Entity
public class ApiKeyData {

	@Id
	@NotNull
	@Column(name = "api_key_id")
	private String apiKeyId;

	@NotNull
	@Column(name = "api_key_commence_on")
	private LocalDateTime apiKeyCommenceOn;

	@NotNull
	@Column(name = "api_key_expires_on")
	private LocalDateTime apiKeyExpiresOn;
	
	@NotNull
	@Column(name = "api_key_status")
	private String apiKeyStatus;

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
