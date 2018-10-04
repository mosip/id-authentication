package io.mosip.registration.dto.json.metadata;

public class ExceptionIris {
	private String missingIris;
	private String exceptionDescription;

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

}
