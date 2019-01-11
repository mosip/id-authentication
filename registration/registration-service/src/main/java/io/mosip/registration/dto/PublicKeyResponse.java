package io.mosip.registration.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PublicKeyResponse<T> {

	/**
	 * The string alias
	 */
	@JsonIgnore
	private String alias;

	/**
	 * Field for public key
	 */
	//@ApiModelProperty(notes = "Public key in BASE64 encoding format", required = true)
	private T publicKey;

	/**
	 * Key creation time
	 */
	//@ApiModelProperty(notes = "Timestamp of issuance of public key", required = true)
	private LocalDateTime issuedAt;

	/**
	 * Key expiry time
	 */
	//@ApiModelProperty(notes = "Timestamp of expiry of public key", required = true)
	private LocalDateTime expiryAt;

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the publicKey
	 */
	public T getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey the publicKey to set
	 */
	public void setPublicKey(T publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the issuedAt
	 */
	public LocalDateTime getIssuedAt() {
		return issuedAt;
	}

	/**
	 * @param issuedAt the issuedAt to set
	 */
	public void setIssuedAt(LocalDateTime issuedAt) {
		this.issuedAt = issuedAt;
	}

	/**
	 * @return the expiryAt
	 */
	public LocalDateTime getExpiryAt() {
		return expiryAt;
	}

	/**
	 * @param expiryAt the expiryAt to set
	 */
	public void setExpiryAt(LocalDateTime expiryAt) {
		this.expiryAt = expiryAt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PublicKeyResponse [alias=" + alias + ", publicKey=" + publicKey + ", issuedAt=" + issuedAt
				+ ", expiryAt=" + expiryAt + "]";
	}
	
	
	
	

}

