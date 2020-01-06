package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AbisResponseDetPKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "matched_bio_ref_id")
	private String matchedBioRefId;

	@Column(name = "abis_resp_id", insertable = false, updatable = false)
	private String abisRespId;

	public AbisResponseDetPKEntity() {
	}

	public String getMatchedBioRefId() {
		return this.matchedBioRefId;
	}

	public void setMatchedBioRefId(String matchedBioRefId) {
		this.matchedBioRefId = matchedBioRefId;
	}

	public String getAbisRespId() {
		return this.abisRespId;
	}

	public void setAbisRespId(String abisRespId) {
		this.abisRespId = abisRespId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbisResponseDetPKEntity)) {
			return false;
		}
		AbisResponseDetPKEntity castOther = (AbisResponseDetPKEntity) other;
		return this.matchedBioRefId.equals(castOther.matchedBioRefId) && this.abisRespId.equals(castOther.abisRespId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.matchedBioRefId.hashCode();
		hash = hash * prime + this.abisRespId.hashCode();

		return hash;
	}
}