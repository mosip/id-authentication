package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Table(name = "oidc_client_data", schema = "ida")
@Entity
public class OIDCClientData {
	
	@Id
	@NotNull
	@Column(name = "oidc_client_id")
	private String clientId;

	@NotNull
	@Column(name = "oidc_client_name")
	private String clientName;

	@NotNull
	@Column(name = "oidc_client_status")
	private String clientStatus;

	@NotNull
	@Column(name = "user_claims")
	private String userClaims;

	@NotNull
	@Column(name = "auth_context_refs")
	private String authContextRefs;

	@NotNull
	@Column(name = "client_auth_methods")
	private String clientAuthMethods;

	@NotNull
	@Column(name = "partner_id")
	private String partnerId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "partner_id", referencedColumnName = "partner_id", insertable = false, updatable = false)
	private PartnerData partnerData;
	
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

	public String[] getAuthContextRefs() {
		return this.authContextRefs.split(",");
	}

	public void setAuthContextRefs(String[] authContextRefs) {
		this.authContextRefs = String.join(",", authContextRefs);
	}

	public String[] getUserClaims() {
		return this.userClaims.split(",");
	}

	public void setUserClaims(String[] userClaims) {
		this.userClaims = String.join(",", userClaims);
	}

	public String[] getClientAuthMethods() {
		return this.clientAuthMethods.split(",");
	}

	public void setClientAuthMethods(String[] clientAuthMethods) {
		this.clientAuthMethods = String.join(",", clientAuthMethods);
	}

}
