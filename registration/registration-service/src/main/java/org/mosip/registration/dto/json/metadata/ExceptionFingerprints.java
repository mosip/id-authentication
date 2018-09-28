package org.mosip.registration.dto.json.metadata;

public class ExceptionFingerprints {

	private String missingFinger;
	private String exceptionDescription;
	private String exceptionType;

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