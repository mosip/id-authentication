package io.mosip.registration.dto.json.metadata;

/**
 * This class is to capture the json parsing photograph data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class Photograph {

	private String photographName;
	private boolean hasExceptionPhoto;
	private String exceptionPhotoName;
	private double qualityScore;
	private int numRetry;
	public String getPhotographName() {
		return photographName;
	}
	public void setPhotographName(String photographName) {
		this.photographName = photographName;
	}
	public boolean isHasExceptionPhoto() {
		return hasExceptionPhoto;
	}
	public void setHasExceptionPhoto(boolean hasExceptionPhoto) {
		this.hasExceptionPhoto = hasExceptionPhoto;
	}
	public String getExceptionPhotoName() {
		return exceptionPhotoName;
	}
	public void setExceptionPhotoName(String exceptionPhotoName) {
		this.exceptionPhotoName = exceptionPhotoName;
	}
	public double getQualityScore() {
		return qualityScore;
	}
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}
	public int getNumRetry() {
		return numRetry;
	}
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}
	
}