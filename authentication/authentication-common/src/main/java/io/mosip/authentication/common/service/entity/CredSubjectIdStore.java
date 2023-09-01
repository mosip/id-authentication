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
@Table(name = "cred_subject_id_store", schema = "ida")
@Entity
public class CredSubjectIdStore {
	
	@Id
	@NotNull
	@Column(name = "id")
	private String id;

	@NotNull
	@Column(name = "id_vid_hash")
	private String idVidHash;

	@NotNull
	@Column(name = "token_id")
	private String tokenId;

	@NotNull
	@Column(name = "cred_subject_id")
	private String credSubjectId;

	@NotNull
	@Column(name = "csid_key_hash")
	private String csidKeyHash;

	@NotNull
	@Column(name = "oidc_client_id")
	private String oidcClientId;
	
	@NotNull
	@Column(name = "csid_status")
	private String csidStatus;

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
