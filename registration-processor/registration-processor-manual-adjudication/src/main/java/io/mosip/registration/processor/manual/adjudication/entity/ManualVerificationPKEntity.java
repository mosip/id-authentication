package io.mosip.registration.processor.manual.adjudication.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the reg_manual_verification database table.
 * 
 * @author Shuchita
 * @since 0.0.1
 */
@Embeddable
public class ManualVerificationPKEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "reg_id")
	private String regId;

	@Column(name = "matched_ref_id")
	private String matchedRefId;

	@Column(name = "matched_ref_type")
	private String matchedRefType;

	/**
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * @param regId
	 *            the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * @return the matchedRefId
	 */
	public String getMatchedRefId() {
		return matchedRefId;
	}

	/**
	 * @param matchedRefId
	 *            the matchedRefId to set
	 */
	public void setMatchedRefId(String matchedRefId) {
		this.matchedRefId = matchedRefId;
	}

	/**
	 * @return the matchedRefType
	 */
	public String getMatchedRefType() {
		return matchedRefType;
	}

	/**
	 * @param matchedRefType
	 *            the matchedRefType to set
	 */
	public void setMatchedRefType(String matchedRefType) {
		this.matchedRefType = matchedRefType;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ManualVerificationPKEntity)) {
			return false;
		}
		ManualVerificationPKEntity castOther = (ManualVerificationPKEntity) other;
		return this.regId.equals(castOther.regId) && this.matchedRefId.equals(castOther.matchedRefId)
				&& this.matchedRefType.equals(castOther.matchedRefType);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.matchedRefId.hashCode();
		hash = hash * prime + this.matchedRefType.hashCode();

		return hash;
	}
}