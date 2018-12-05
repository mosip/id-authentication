package io.mosip.registration.processor.manual.adjudication.dto;

import java.io.Serializable;

/**
 * The {@link ManualVerificationDTO} class
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
public class ManualVerificationDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String regId;
	
	private String mvUsrId;
	
	private String statusCode;
	
	private String matchedRefId;
	
	private String matchedRefType;

	/**
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * @return the mvUsrId
	 */
	public String getMvUsrId() {
		return mvUsrId;
	}

	/**
	 * @param mvUsrId the mvUsrId to set
	 */
	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the matchedRefId
	 */
	public String getMatchedRefId() {
		return matchedRefId;
	}

	/**
	 * @param matchedRefId the matchedRefId to set
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
	 * @param matchedRefType the matchedRefType to set
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
