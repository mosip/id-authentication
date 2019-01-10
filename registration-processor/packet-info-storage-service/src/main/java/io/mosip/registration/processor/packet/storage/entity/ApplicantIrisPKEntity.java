package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
	
/**
 * The primary key class for the applicant_iris database table.
 * 
 * @author Horteppa M1048399
 */

@Embeddable
public class ApplicantIrisPKEntity implements Serializable {

	/** The Constant serialVersionUID. */
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

	/** The typ. */
	@Column(name = "typ", nullable = false)
	private String typ;

	/**
	 * Instantiates a new applicant iris PK entity.
	 */
	public ApplicantIrisPKEntity() {
		super();
	}

	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return this.regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * Gets the typ.
	 *
	 * @return the typ
	 */
	public String getTyp() {
		return this.typ;
	}

	/**
	 * Sets the typ.
	 *
	 * @param typ the new typ
	 */
	public void setTyp(String typ) {
		this.typ = typ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantIrisPKEntity)) {
			return false;
		}
		ApplicantIrisPKEntity castOther = (ApplicantIrisPKEntity) other;
		return this.regId.equals(castOther.regId) && this.typ.equals(castOther.typ);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.typ.hashCode();

		return hash;
	}
}