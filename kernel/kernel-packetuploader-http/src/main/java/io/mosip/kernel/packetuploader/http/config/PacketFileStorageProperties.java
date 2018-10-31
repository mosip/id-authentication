package io.mosip.kernel.packetuploader.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration Context properties for upload directory
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "mosip.kernel.packetuploader.http.file")
public class PacketFileStorageProperties {
	/**
	 * Upload directory for this application
	 */
	private String uploadDir;

	/**
	 * Getter for {@link #uploadDir}
	 * 
	 * @return {@link #uploadDir}
	 */
	public String getUploadDir() {
		return uploadDir;
	}

	/**
	 * Setter for {@link #uploadDir}
	 * 
	 * @param uploadDir
	 *            {@link #uploadDir}
	 */
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
}