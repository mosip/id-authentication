package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the applicant_demographic database table.
 * 
 */
@Embeddable
public class ApplicantDemographicPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="lang_code")
	private String langCode;

	public ApplicantDemographicPKEntity() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantDemographicPKEntity)) {
			return false;
		}
		ApplicantDemographicPKEntity castOther = (ApplicantDemographicPKEntity)other;
		return 
			this.regId.equals(castOther.regId)
			&& this.langCode.equals(castOther.langCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.langCode.hashCode();
		
		return hash;
	}
}