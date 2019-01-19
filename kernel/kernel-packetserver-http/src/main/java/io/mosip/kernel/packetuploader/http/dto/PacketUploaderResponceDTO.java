package io.mosip.kernel.packetuploader.http.dto;

/**
 * Response for successful file upload
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploaderResponceDTO {
	/**
	 * Name of uploaded file
	 */
	private String fileName;
	/**
	 * Size of uploaded file in bytes
	 */
	private long fileSizeInBytes;

	/**
	 * Setter for {@link #fileName}
	 * 
	 * @return {@link #fileName}
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Setter for {@link #fileName}
	 * 
	 * @param fileName
	 *            {@link #fileName}
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Getter for {@link #fileSizeInBytes}
	 * 
	 * @return {@link #fileSizeInBytes}
	 */
	public long getFileSizeInBytes() {
		return fileSizeInBytes;
	}

	/**
	 * Constructor for this class
	 * 
	 * @param fileName
	 *            {@link #fileName}
	 * @param fileSizeInBytes
	 *            {@link #fileSizeInBytes}
	 */
	public PacketUploaderResponceDTO(String fileName, long fileSizeInBytes) {
		this.setFileName(fileName);
		this.setFileSizeInBytes(fileSizeInBytes);
	}

	/**
	 * Setter for {@link #fileSizeInBytes}
	 * 
	 * @param fileSizeInBytes
	 *            {@link #fileSizeInBytes}
	 */
	public void setFileSizeInBytes(long fileSizeInBytes) {
		this.fileSizeInBytes = fileSizeInBytes;
	}

}
