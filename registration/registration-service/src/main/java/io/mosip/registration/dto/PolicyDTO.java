package io.mosip.registration.dto;

import java.sql.Timestamp;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */

public class PolicyDTO {
	private String publicKey;
	private Timestamp keyGenerationTime;
	private Timestamp keyExpiryTime;
	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}
	/**
	 * @param publicKey the publicKey to set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	/**
	 * @return the keyGenerationTime
	 */
	public Timestamp getKeyGenerationTime() {
		return keyGenerationTime;
	}
	/**
	 * @param keyGenerationTime the keyGenerationTime to set
	 */
	public void setKeyGenerationTime(Timestamp keyGenerationTime) {
		this.keyGenerationTime = keyGenerationTime;
	}
	/**
	 * @return the keyExpiryTime
	 */
	public Timestamp getKeyExpiryTime() {
		return keyExpiryTime;
	}
	/**
	 * @param keyExpiryTime the keyExpiryTime to set
	 */
	public void setKeyExpiryTime(Timestamp keyExpiryTime) {
		this.keyExpiryTime = keyExpiryTime;
	}

}
