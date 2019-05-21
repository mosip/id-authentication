package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual.
 *
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicantDocumentDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6140664025425342026L;

	/** The documents. */
	private Map<String, DocumentDetailsDTO> documents;

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
	
	public byte[] getExceptionPhoto() {
		if(exceptionPhoto!=null)
			return Arrays.copyOf(exceptionPhoto, exceptionPhoto.length);
		return null;
	}
	public void setExceptionPhoto(byte[] exceptionPhoto) {
		this.exceptionPhoto=exceptionPhoto!=null?exceptionPhoto:null;
	}
	
	public byte[] getAcknowledgeReceipt() {
		if(acknowledgeReceipt!=null)
			return Arrays.copyOf(acknowledgeReceipt, acknowledgeReceipt.length);
		return null;
	}
	public void setAcknowledgeReceipt(byte[] acknowledgeReceipt) {
		this.acknowledgeReceipt=acknowledgeReceipt!=null?acknowledgeReceipt:null;
	}

	public byte[] getCompressedFacePhoto() {
		if(compressedFacePhoto!=null)
			return Arrays.copyOf(compressedFacePhoto, compressedFacePhoto.length);
		return null;
	}
	public void setCompressedFacePhoto(byte[] compressedFacePhoto) {
		this.compressedFacePhoto=compressedFacePhoto!=null?compressedFacePhoto:null;
	}
	
	public byte[] getPhoto() {
		if(photo!=null)
			return Arrays.copyOf(photo, photo.length);
		return null;
	}
	public void setPhoto(byte[] photo) {
		this.photo=photo!=null?photo:null;
	}


}
