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
@Table(name = "ident_binding_cert_store", schema = "ida")
@Entity
public class IdentityBindingCertificateStore {
	
	@Id
	@NotNull
	@Column(name = "cert_id")
	private String certId;

	@NotNull
	@Column(name = "id_vid_hash")
	private String idVidHash;

	@NotNull
	@Column(name = "token_id")
	private String token;

	@NotNull
	@Column(name = "certificate_data")
	private String certificateData;

	@NotNull
	@Column(name = "public_key_hash")
	private String publicKeyHash;

	@NotNull
	@Column(name = "cert_thumbprint")
	private String certThumbprint;

	@NotNull
	@Column(name = "partner_name")
	private String partnerName;
	
	@NotNull
	@Column(name = "auth_factor")
	private String authFactor;
	
	@NotNull
	@Column(name = "cert_expire")
	private LocalDateTime certExpireDateTime;
	
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
