package io.mosip.registration.dto.json.metadata;

import java.util.List;

/**
 * This class is to capture the json parsing iris data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class IrisData {
	private List<Iris> iris;
	private int numRetry;
	private List<BiometricException> exceptionIris;
	public List<Iris> getIris() {
		return iris;
	}
	public void setIris(List<Iris> iris) {
		this.iris = iris;
	}
	public int getNumRetry() {
		return numRetry;
	}
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}
	public List<BiometricException> getExceptionIris() {
		return exceptionIris;
	}
	public void setExceptionIris(List<BiometricException> exceptionIris) {
		this.exceptionIris = exceptionIris;
	}
	
}
