package org.mosip.registration.dto.json.metadata;

import java.util.List;

public class FingerprintData {
	private List<Fingerprints> fingerprints;
	private int numRetry;
	private List<ExceptionFingerprints> exceptionFingerprints;

	/**
	 * @return the fingerprints
	 */
	public List<Fingerprints> getFingerprints() {
		return fingerprints;
	}

	/**
	 * @param fingerprints
	 *            the fingerprints to set
	 */
	public void setFingerprints(List<Fingerprints> fingerprints) {
		this.fingerprints = fingerprints;
	}

	/**
	 * @return the numRetry
	 */
	public int getNumRetry() {
		return numRetry;
	}

	/**
	 * @param numRetry
	 *            the numRetry to set
	 */
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}

	/**
	 * @return the exceptionFingerprints
	 */
	public List<ExceptionFingerprints> getExceptionFingerprints() {
		return exceptionFingerprints;
	}

	/**
	 * @param exceptionFingerprints
	 *            the exceptionFingerprints to set
	 */
	public void setExceptionFingerprints(List<ExceptionFingerprints> exceptionFingerprints) {
		this.exceptionFingerprints = exceptionFingerprints;
	}
}
