package io.mosip.registration.dto.biometric;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the information on captured Iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class IrisDetailsDTO extends BaseDTO {
	
	@JsonIgnore
	private byte[] iris;
	protected String irisImageName;
	protected double qualityScore;
	protected boolean isForceCaptured;
	protected String irisType;
	public byte[] getIris() {
		return iris;
	}
	public void setIris(byte[] iris) {
		this.iris = iris;
	}
	public String getIrisImageName() {
		return irisImageName;
	}
	public void setIrisImageName(String irisImageName) {
		this.irisImageName = irisImageName;
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
	public String getIrisType() {
		return irisType;
	}
	public void setIrisType(String irisType) {
		this.irisType = irisType;
	}
	
}
