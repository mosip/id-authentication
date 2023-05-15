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
@Table(name = "kyc_token_store", schema = "ida")
@Entity
public class KycTokenData {
	
	@Id
	@NotNull
	@Column(name = "id")
	private String kycTokenId;

	@NotNull
	@Column(name = "id_vid_hash")
	private String idVidHash;

	@NotNull
	@Column(name = "kyc_token")
	private String kycToken;

	@NotNull
	@Column(name = "psu_token")
	private String psuToken;

	@NotNull
	@Column(name = "oidc_client_id")
	private String oidcClientId;

	@NotNull
	@Column(name = "request_trn_id")
	private String requestTransactionId;
	
	@NotNull
	@Column(name = "token_issued_dtimes")
	private LocalDateTime tokenIssuedDateTime;

	@NotNull
	@Column(name = "auth_req_dtimes")
	private LocalDateTime authReqDateTime;

	@NotNull
	@Column(name = "kyc_token_status")
	private String kycTokenStatus;

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
