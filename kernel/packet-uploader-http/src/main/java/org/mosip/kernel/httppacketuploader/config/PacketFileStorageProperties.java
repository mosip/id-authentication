package org.mosip.kernel.httppacketuploader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration Context properties for upload directory
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "file")
public class PacketFileStorageProperties {
	/**
	 * upload directory for this application
	 */
	private String uploadDir;

	/**
	 * getter for {@link #uploadDir}
	 * 
	 * @return {@link #uploadDir}
	 */
	public String getUploadDir() {
		return uploadDir;
	}

	/**
	 * setter for {@link #uploadDir}
	 * 
	 * @param uploadDir
	 *            {@link #uploadDir}
	 */
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
}