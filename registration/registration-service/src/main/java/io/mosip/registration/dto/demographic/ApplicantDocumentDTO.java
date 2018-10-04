package io.mosip.registration.dto.demographic;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class ApplicantDocumentDTO extends BaseDTO {
	private List<DocumentDetailsDTO> documentDetailsDTO;
	private String photoName;
	private byte[] photo;
	private boolean hasExceptionPhoto;
	private byte[] exceptionPhoto;
	private String exceptionPhotoName;
	private byte[] acknowledgeReceipt;
	private String acknowledgeReceiptName;

	/**
	 * @return the documentDetailsDTO
	 */
	public List<DocumentDetailsDTO> getDocumentDetailsDTO() {
		return documentDetailsDTO;
	}

	/**
	 * @param documentDetailsDTO
	 *            the documentDetailsDTO to set
	 */
	public void setDocumentDetailsDTO(List<DocumentDetailsDTO> documentDetailsDTO) {
		this.documentDetailsDTO = documentDetailsDTO;
	}

	/**
	 * @return the photoName
	 */
	public String getPhotoName() {
		return photoName;
	}

	/**
	 * @param photoName
	 *            the photoName to set
	 */
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	/**
	 * @return the photo
	 */
	public byte[] getPhoto() {
		return photo;
	}

	/**
	 * @param photo
	 *            the photo to set
	 */
	public void setPhoto(byte[] photo) {
		this.photo = photo;
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
		return exceptionPhoto;
	}

	/**
	 * @param exceptionPhoto
	 *            the exceptionPhoto to set
	 */
	public void setExceptionPhoto(byte[] exceptionPhoto) {
		this.exceptionPhoto = exceptionPhoto;
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
	 * @return the acknowledgeReceipt
	 */
	public byte[] getAcknowledgeReceipt() {
		return acknowledgeReceipt;
	}

	/**
	 * @param acknowledgeReceipt
	 *            the acknowledgeReceipt to set
	 */
	public void setAcknowledgeReceipt(byte[] acknowledgeReceipt) {
		this.acknowledgeReceipt = acknowledgeReceipt;
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
