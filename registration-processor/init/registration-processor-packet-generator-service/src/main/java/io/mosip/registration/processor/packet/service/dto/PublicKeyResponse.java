package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * The Class PublicKeyResponse.
 *
 * @param <T>
 *            the generic type
 */
@Data
public class PublicKeyResponse<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2552709003515289394L;

	/** The string alias. */
	@JsonIgnore
	private String alias;

	/** Field for public key. */
	// @ApiModelProperty(notes = "Public key in BASE64 encoding format", required =
	// true)
	private T publicKey;

	/** Key creation time. */
	// @ApiModelProperty(notes = "Timestamp of issuance of public key", required =
	// true)
	private LocalDateTime issuedAt;

	/** Key expiry time. */
	// @ApiModelProperty(notes = "Timestamp of expiry of public key", required =
	// true)
	private LocalDateTime expiryAt;

	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 *
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets the public key.
	 *
	 * @return the publicKey
	 */
	public T getPublicKey() {
		return publicKey;
	}

	/**
	 * Sets the public key.
	 *
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(T publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * Gets the issued at.
	 *
	 * @return the issuedAt
	 */
	public LocalDateTime getIssuedAt() {
		return issuedAt;
	}

	/**
	 * Sets the issued at.
	 *
	 * @param issuedAt
	 *            the issuedAt to set
	 */
	public void setIssuedAt(LocalDateTime issuedAt) {
		this.issuedAt = issuedAt;
	}

	/**
	 * Gets the expiry at.
	 *
	 * @return the expiryAt
	 */
	public LocalDateTime getExpiryAt() {
		return expiryAt;
	}

	/**
	 * Sets the expiry at.
	 *
	 * @param expiryAt
	 *            the expiryAt to set
	 */
	public void setExpiryAt(LocalDateTime expiryAt) {
		this.expiryAt = expiryAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PublicKeyResponse [alias=" + alias + ", publicKey=" + publicKey + ", issuedAt=" + issuedAt
				+ ", expiryAt=" + expiryAt + "]";
	}

}
