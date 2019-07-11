package io.mosip.registration.dto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The DTO Class PublicKeyResponse.
 * 
 * @author Brahmananda reddy
 *
 * @param <T> the generic type
 */
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
	
	private String id;
	
	private String version;
	
	private String responsetime;
	
	private String metadata;
	
	private List<LinkedHashMap<String, Object>> errors;
	
	private LinkedHashMap<String, Object> response;
	
	
	
	

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the responsetime
	 */
	public String getResponsetime() {
		return responsetime;
	}

	/**
	 * @param responsetime the responsetime to set
	 */
	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	/**
	 * @return the metadata
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return the errors
	 */
	public List<LinkedHashMap<String, Object>> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<LinkedHashMap<String, Object>> errors) {
		this.errors = errors;
	}

	/**
	 * @return the response
	 */
	public LinkedHashMap<String, Object> getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(LinkedHashMap<String, Object> response) {
		this.response = response;
	}

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

