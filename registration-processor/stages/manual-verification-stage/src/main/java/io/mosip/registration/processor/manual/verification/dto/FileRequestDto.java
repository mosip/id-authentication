package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;
	
/**
 * The RequestDTO class.
 */
public class FileRequestDto implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The reg id. */
	String regId;
	
	/** The file name. */
	String fileName;
	
	/**
	 * Gets the reg id.
	 *
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}
	
	/**
	 * Sets the reg id.
	 *
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	/**
	 * Gets the file name.
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the file name.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
