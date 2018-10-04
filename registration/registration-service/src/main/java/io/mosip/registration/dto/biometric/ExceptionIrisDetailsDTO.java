package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
/**
 * This class contains the information on exception Iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class ExceptionIrisDetailsDTO extends BaseDTO {
	private String missingIris;
	private String exceptionDescription;
	// Permanent or Temporary
	private String exceptionType;

	/**
	 * @return the missingIris
	 */
	public String getMissingIris() {
		return missingIris;
	}

	/**
	 * @param missingIris
	 *            the missingIris to set
	 */
	public void setMissingIris(String missingIris) {
		this.missingIris = missingIris;
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
