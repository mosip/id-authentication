package io.mosip.registration.dto.json.metadata;

import java.util.List;

public class IrisData {
	private List<Iris> iris;
	private int numRetry;
	private List<ExceptionIris> exceptionIris;

	/**
	 * @return the iris
	 */
	public List<Iris> getIris() {
		return iris;
	}

	/**
	 * @param iris
	 *            the iris to set
	 */
	public void setIris(List<Iris> iris) {
		this.iris = iris;
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
	 * @return the exceptionIris
	 */
	public List<ExceptionIris> getExceptionIris() {
		return exceptionIris;
	}

	/**
	 * @param exceptionIris
	 *            the exceptionIris to set
	 */
	public void setExceptionIris(List<ExceptionIris> exceptionIris) {
		this.exceptionIris = exceptionIris;
	}

}
