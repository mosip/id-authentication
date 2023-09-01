package io.mosip.authentication.common.service.entity;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import io.mosip.authentication.core.util.CryptoUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.JSONObject;

@NoArgsConstructor
@Data
@Table(name = "policy_data", schema = "ida")
@Entity
public class PolicyData {

	@Id
	@NotNull
	@Column(name = "policy_id")
	private String policyId;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "policy_data")
	private byte[] policy;

	@NotNull
	@Column(name = "policy_name")
	private String policyName;

	@NotNull
	@Column(name = "policy_status")
	private String policyStatus;

	@NotNull
	@Column(name = "policy_description")
	private String policyDescription;

	@NotNull
	@Column(name = "policy_commence_on")
	private LocalDateTime policyCommenceOn;

	@NotNull
	@Column(name = "policy_expires_on")
	private LocalDateTime policyExpiresOn;

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

	private static final ObjectMapper OBJECT_MAPPER;
	
	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.registerModule(new AfterburnerModule());
	}

	public JSONObject getPolicy() {
		
		try {
			return OBJECT_MAPPER.readValue(CryptoUtil.decodeBase64Url(new String(this.policy)), JSONObject.class);
		} catch (IOException e) {
			// This block will never be executed
			//e.printStackTrace();
			return null;
		}
	}

	public void setPolicy(JSONObject policy) {
		this.policy = CryptoUtil.encodeBase64Url(policy.toJSONString().getBytes()).getBytes();
	}
}
