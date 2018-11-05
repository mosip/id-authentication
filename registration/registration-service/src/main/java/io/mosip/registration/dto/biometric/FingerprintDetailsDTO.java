package io.mosip.registration.dto.biometric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on captured Finger prints
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class FingerprintDetailsDTO extends BaseDTO {
	
	@JsonIgnore
	private byte[] fingerPrint;
	protected String fingerprintImageName;
	protected double qualityScore;
	protected boolean isForceCaptured;
	protected String fingerType;
	protected int numRetry;
	public byte[] getFingerPrint() {
		return fingerPrint;
	}
	public void setFingerPrint(byte[] fingerPrint) {
		this.fingerPrint = fingerPrint;
	}
	public String getFingerprintImageName() {
		return fingerprintImageName;
	}
	public void setFingerprintImageName(String fingerprintImageName) {
		this.fingerprintImageName = fingerprintImageName;
	}
	public double getQualityScore() {
		return qualityScore;
	}
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}
	public boolean isForceCaptured() {
		return isForceCaptured;
	}
	public void setForceCaptured(boolean isForceCaptured) {
		this.isForceCaptured = isForceCaptured;
	}
	public String getFingerType() {
		return fingerType;
	}
	public void setFingerType(String fingerType) {
		this.fingerType = fingerType;
	}
	public int getNumRetry() {
		return numRetry;
	}
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}
	
}
