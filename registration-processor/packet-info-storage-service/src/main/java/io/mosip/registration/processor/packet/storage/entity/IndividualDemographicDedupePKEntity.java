package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Girish Yarru
 *
 */
@Embeddable
public class IndividualDemographicDedupePKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "reg_id", nullable = false)
	private String regId;

	@Column(name = "lang_code", nullable = false)
	private String langCode;

	public IndividualDemographicDedupePKEntity() {
		super();
	}

	public String getRegId() {
		return this.regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof IndividualDemographicDedupePKEntity)) {
			return false;
		}
		IndividualDemographicDedupePKEntity castOther = (IndividualDemographicDedupePKEntity) other;
		return this.regId.equals(castOther.regId) && this.langCode.equals(castOther.langCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.langCode.hashCode();

		return hash;
	}
}