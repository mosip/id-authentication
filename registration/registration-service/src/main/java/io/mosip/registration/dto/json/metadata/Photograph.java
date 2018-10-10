package io.mosip.registration.dto.json.metadata;

public class Photograph {

	private String photographName;
	private boolean hasExceptionPhoto;
	private String exceptionPhotoName;

	/**
	 * @return the photographName
	 */
	public String getPhotographName() {
		return photographName;
	}

	/**
	 * @param photographName
	 *            the photographName to set
	 */
	public void setPhotographName(String photographName) {
		this.photographName = photographName;
	}

	/**
	 * @return the hasExceptionPhoto
	 */
	public boolean isHasExceptionPhoto() {
		return hasExceptionPhoto;
	}

	/**
	 * @param hasExceptionPhoto
	 *            the hasExceptionPhoto to set
	 */
	public void setHasExceptionPhoto(boolean hasExceptionPhoto) {
		this.hasExceptionPhoto = hasExceptionPhoto;
	}

	/**
	 * @return the exceptionPhotoName
	 */
	public String getExceptionPhotoName() {
		return exceptionPhotoName;
	}

	/**
	 * @param exceptionPhotoName
	 *            the exceptionPhotoName to set
	 */
	public void setExceptionPhotoName(String exceptionPhotoName) {
		this.exceptionPhotoName = exceptionPhotoName;
	}

}