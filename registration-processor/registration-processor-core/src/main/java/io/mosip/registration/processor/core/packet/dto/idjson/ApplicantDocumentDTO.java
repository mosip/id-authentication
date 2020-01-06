package io.mosip.registration.processor.core.packet.dto.idjson;

import java.util.Arrays;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class ApplicantDocumentDTO{

	/** The photograph name. */
	private String photographName;

	/** The photo. */
	private byte[] photo;
	
	/** The compressed photo for QR Code. */
	private byte[] compressedFacePhoto;

	/** The has exception photo. */
	private boolean hasExceptionPhoto;

	/** The exception photo. */
	private byte[] exceptionPhoto;

	/** The exception photo name. */
	private String exceptionPhotoName;

	/** The quality score. */
	private double qualityScore;

	/** The num retry. */
	private int numRetry;

	/** The acknowledge receipt. */
	private byte[] acknowledgeReceipt;

	/** The acknowledge receipt name. */
	private String acknowledgeReceiptName;

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
	 * @return the photo
	 */
	public byte[] getPhoto() {
		if(photo!=null)
			return Arrays.copyOf(photo, photo.length);
		return null;
	}

	/**
	 * @param photo
	 *            the photo to set
	 */
	public void setPhoto(byte[] photo) {
		this.photo=photo!=null?photo:null;
	}
	
	/**
	 * @return the compressedFacePhoto
	 */
	public byte[] getCompressedFacePhoto() {
		if(compressedFacePhoto!=null)
			return Arrays.copyOf(compressedFacePhoto, compressedFacePhoto.length);
		return null;
	}

	/**
	 * @param compressedFacePhoto
	 *            the compressed face photo to set
	 */
	public void setCompressedFacePhoto(byte[] compressedFacePhoto) {
		this.compressedFacePhoto=compressedFacePhoto!=null?compressedFacePhoto:null;
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
	 * @return the exceptionPhoto
	 */
	public byte[] getExceptionPhoto() {
		if(exceptionPhoto!=null)
			return Arrays.copyOf(exceptionPhoto, exceptionPhoto.length);
		return null;
		
	}

	/**
	 * @param exceptionPhoto
	 *            the exceptionPhoto to set
	 */
	public void setExceptionPhoto(byte[] exceptionPhoto) {
		this.exceptionPhoto=exceptionPhoto!=null?exceptionPhoto:null;
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

	/**
	 * @return the qualityScore
	 */
	public double getQualityScore() {
		return qualityScore;
	}

	/**
	 * @param qualityScore
	 *            the qualityScore to set
	 */
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
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
	 * @return the acknowledgeReceipt
	 */
	public byte[] getAcknowledgeReceipt() {
		if(acknowledgeReceipt!=null)
			return Arrays.copyOf(acknowledgeReceipt, acknowledgeReceipt.length);
		return null;
	}

	/**
	 * @param acknowledgeReceipt
	 *            the acknowledgeReceipt to set
	 */
	public void setAcknowledgeReceipt(byte[] acknowledgeReceipt) {
		this.acknowledgeReceipt=acknowledgeReceipt!=null?acknowledgeReceipt:null;
	}

	/**
	 * @return the acknowledgeReceiptName
	 */
	public String getAcknowledgeReceiptName() {
		return acknowledgeReceiptName;
	}

	/**
	 * @param acknowledgeReceiptName
	 *            the acknowledgeReceiptName to set
	 */
	public void setAcknowledgeReceiptName(String acknowledgeReceiptName) {
		this.acknowledgeReceiptName = acknowledgeReceiptName;
	}

}
