package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;
	
/**
 * The {@link ManualVerificationDTO} class.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class ManualVerificationDTO implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	private String regId;
	
	/** The mv usr id. */
	private String mvUsrId;
	
	/** The status code. */
	private String statusCode;
	
	/** The matched ref id. */
	private String matchedRefId;
	
	/** The matched ref type. */
	private String matchedRefType;
	
	/** The reason code. */
	private String reasonCode;

	/**
	 * Gets the reg id.
	 *
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * Gets the mv usr id.
	 *
	 * @return the mvUsrId
	 */
	public String getMvUsrId() {
		return mvUsrId;
	}

	/**
	 * Sets the mv usr id.
	 *
	 * @param mvUsrId the mvUsrId to set
	 */
	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the matched ref id.
	 *
	 * @return the matchedRefId
	 */
	public String getMatchedRefId() {
		return matchedRefId;
	}

	/**
	 * Sets the matched ref id.
	 *
	 * @param matchedRefId the matchedRefId to set
	 */
	public void setMatchedRefId(String matchedRefId) {
		this.matchedRefId = matchedRefId;
	}

	/**
	 * Gets the matched ref type.
	 *
	 * @return the matchedRefType
	 */
	public String getMatchedRefType() {
		return matchedRefType;
	}

	/**
	 * Sets the matched ref type.
	 *
	 * @param matchedRefType the matchedRefType to set
	 */
	public void setMatchedRefType(String matchedRefType) {
		this.matchedRefType = matchedRefType;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matchedRefId == null) ? 0 : matchedRefId.hashCode());
		result = prime * result + ((matchedRefType == null) ? 0 : matchedRefType.hashCode());
		result = prime * result + ((mvUsrId == null) ? 0 : mvUsrId.hashCode());
		result = prime * result + ((regId == null) ? 0 : regId.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManualVerificationDTO other = (ManualVerificationDTO) obj;
		if (matchedRefId == null) {
			if (other.matchedRefId != null)
				return false;
		} else if (!matchedRefId.equals(other.matchedRefId)) {
			return false;
		}
		if (matchedRefType == null) {
			if (other.matchedRefType != null)
				return false;
		} else if (!matchedRefType.equals(other.matchedRefType)) {
			return false;
		}
		if (mvUsrId == null) {
			if (other.mvUsrId != null)
				return false;
		} else if (!mvUsrId.equals(other.mvUsrId)) {
			return false;
		}
		if (regId == null) {
			if (other.regId != null)
				return false;
		} else if (!regId.equals(other.regId)) {
			return false;
		}
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the reason code.
	 *
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * Sets the reason code.
	 *
	 * @param reasonCode the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	
	
	
	
	
}
