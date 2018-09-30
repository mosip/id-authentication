package org.mosip.registration.dto.biometric;

import org.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on exception fingerprint data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class ExceptionFingerprintDetailsDTO extends BaseDTO {
	protected String missingFinger;
	protected String exceptionDescription;
	// Permanent or Temporary
	protected String exceptionType;

	/**
	 * @return the missingFinger
	 */
	public String getMissingFinger() {
		return missingFinger;
	}

	/**
	 * @param missingFinger
	 *            the missingFinger to set
	 */
	public void setMissingFinger(String missingFinger) {
		this.missingFinger = missingFinger;
	}

	/**
	 * @return the exceptionDescription
	 */
	public String getExceptionDescription() {
		return exceptionDescription;
	}

	/**
	 * @param exceptionDescription
	 *            the exceptionDescription to set
	 */
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}

	/**
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}

	/**
	 * @param exceptionType
	 *            the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

}