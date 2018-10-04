package io.mosip.registration.dto.json.metadata;

public class BiometericData {
	private FingerprintData fingerprintData;
	private IrisData irisData;

	/**
	 * @return the fingerprintData
	 */
	public FingerprintData getFingerprintData() {
		return fingerprintData;
	}

	/**
	 * @param fingerprintData
	 *            the fingerprintData to set
	 */
	public void setFingerprintData(FingerprintData fingerprintData) {
		this.fingerprintData = fingerprintData;
	}

	/**
	 * @return the irisData
	 */
	public IrisData getIrisData() {
		return irisData;
	}

	/**
	 * @param irisData
	 *            the irisData to set
	 */
	public void setIrisData(IrisData irisData) {
		this.irisData = irisData;
	}
}
