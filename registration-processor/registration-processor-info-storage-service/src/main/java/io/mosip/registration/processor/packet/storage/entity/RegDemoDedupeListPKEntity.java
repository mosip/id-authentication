package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RegDemoDedupeListPKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "matched_reg_id")
	private String matchedRegId;

	@Column(name = "regtrn_id", insertable = false, updatable = false)
	private String regtrnId;

	public RegDemoDedupeListPKEntity() {
	}

	public String getMatchedRegId() {
		return this.matchedRegId;
	}

	public void setMatchedRegId(String matchedRegId) {
		this.matchedRegId = matchedRegId;
	}

	public String getRegtrnId() {
		return this.regtrnId;
	}

	public void setRegtrnId(String regtrnId) {
		this.regtrnId = regtrnId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RegDemoDedupeListPKEntity)) {
			return false;
		}
		RegDemoDedupeListPKEntity castOther = (RegDemoDedupeListPKEntity) other;
		return this.matchedRegId.equals(castOther.matchedRegId) && this.regtrnId.equals(castOther.regtrnId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.matchedRegId.hashCode();
		hash = hash * prime + this.regtrnId.hashCode();

		return hash;
	}
}