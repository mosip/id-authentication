package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the applicant_demographic database table.
 * 
 */
@Embeddable
public class IndividualDemographicDedupePKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "ref_id", nullable = false)
	private String refId;

	@Column(name = "lang_code", nullable = false)
	private String langCode;

	@Column(name = "ref_id_type", nullable = false)
	private String refIdType;

	public IndividualDemographicDedupePKEntity() {
		super();
	}

	public String getRefId() {
		return this.refId;
	}

	public void setRefId(String regId) {
		this.refId = regId;
	}

	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	

	public String getRefIdType() {
		return refIdType;
	}

	public void setRefIdType(String refIdType) {
		this.refIdType = refIdType;
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
		return this.refId.equals(castOther.refId) && this.langCode.equals(castOther.langCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.refId.hashCode();
		hash = hash * prime + this.langCode.hashCode();

		return hash;
	}
}