package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the reg_manual_verification database table.
 * 
 */
@Embeddable
public class ManualVerificationPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="matched_ref_id")
	private String matchedRefId;

	@Column(name="matched_ref_type")
	private String matchedRefType;

	public ManualVerificationPKEntity() {
	}
	public String getRegId() {
		return this.regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getMatchedRefId() {
		return this.matchedRefId;
	}
	public void setMatchedRefId(String matchedRefId) {
		this.matchedRefId = matchedRefId;
	}
	public String getMatchedRefType() {
		return this.matchedRefType;
	}
	public void setMatchedRefType(String matchedRefType) {
		this.matchedRefType = matchedRefType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ManualVerificationPKEntity)) {
			return false;
		}
		ManualVerificationPKEntity castOther = (ManualVerificationPKEntity)other;
		return 
			this.regId.equals(castOther.regId)
			&& this.matchedRefId.equals(castOther.matchedRefId)
			&& this.matchedRefType.equals(castOther.matchedRefType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.matchedRefId.hashCode();
		hash = hash * prime + this.matchedRefType.hashCode();
		
		return hash;
	}
}