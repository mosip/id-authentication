package io.mosip.authentication.common.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Table(name = "partner_mapping", schema = "ida")
@Entity
@IdClass(PartnerMapping.Compositeclass.class)
public class PartnerMapping {

	@Id
	@NotNull
	@Column(name = "partner_id")
	private String partnerId;

	@Id
	@NotNull
	@Column(name = "policy_id")
	private String policyId;

	@Id
	@NotNull
	@Column(name = "api_key_id")
	private String apiKeyId;

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

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "partner_id", referencedColumnName = "partner_id", insertable = false, updatable = false)
	private PartnerData partnerData;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "policy_id", referencedColumnName = "policy_id", insertable = false, updatable = false)
	private PolicyData policyData;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "api_key_id", referencedColumnName = "api_key_id", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private ApiKeyData apiKeyData;

	@Data
	static class Compositeclass implements Serializable {

		private static final long serialVersionUID = 1L;

		private String partnerId;

		private String policyId;

		private String apiKeyId;
	}
}
