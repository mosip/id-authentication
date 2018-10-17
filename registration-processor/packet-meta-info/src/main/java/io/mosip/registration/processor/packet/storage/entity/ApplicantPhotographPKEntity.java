package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the applicant_photograph database table.
 * 
 * @author Horteppa M1048399
 */
@Embeddable
public class ApplicantPhotographPKEntity implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "reg_id", nullable = false)
	private String regId;

	public ApplicantPhotographPKEntity() {
		super();
	}

	public String getRegId() {
		return this.regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantPhotographPKEntity)) {
			return false;
		}
		ApplicantPhotographPKEntity castOther = (ApplicantPhotographPKEntity) other;
		return this.regId.equals(castOther.regId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();

		return hash;
	}
}