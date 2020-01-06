package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class PacketStatusDTO.
 * 
 * @author Rishabh Keshari
 */
@Data
public class PacketStatusDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8195613350182933826L;

	/** The source path. */
	private String sourcePath;

	/** The file name. */
	private String fileName;

	/** The upload status. */
	private String uploadStatus;

	/** The upload time. */
	private String uploadTime;

	/** The remarks. */
	private String remarks;

	/**
	 * Gets the source path.
	 *
	 * @return the source path
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * Sets the source path.
	 *
	 * @param sourcePath
	 *            the new source path
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName
	 *            the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the upload status.
	 *
	 * @return the upload status
	 */
	public String getUploadStatus() {
		return uploadStatus;
	}

	/**
	 * Sets the upload status.
	 *
	 * @param uploadStatus
	 *            the new upload status
	 */
	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	/**
	 * Gets the upload time.
	 *
	 * @return the upload time
	 */
	public String getUploadTime() {
		return uploadTime;
	}

	/**
	 * Sets the upload time.
	 *
	 * @param uploadTime
	 *            the new upload time
	 */
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 *
	 * @param remarks
	 *            the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
