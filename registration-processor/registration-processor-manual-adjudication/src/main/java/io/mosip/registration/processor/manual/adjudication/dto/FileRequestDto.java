package io.mosip.registration.processor.manual.adjudication.dto;

import java.io.Serializable;

/**
 * The RequestDTO class
 *
 */
public class FileRequestDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String regId;
	String fileName;
	/**
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}
	/**
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
