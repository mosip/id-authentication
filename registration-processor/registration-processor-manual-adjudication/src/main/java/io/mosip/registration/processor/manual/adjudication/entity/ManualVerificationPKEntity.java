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
}