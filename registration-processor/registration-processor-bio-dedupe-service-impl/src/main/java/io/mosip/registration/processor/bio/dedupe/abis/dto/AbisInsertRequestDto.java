package io.mosip.registration.processor.bio.dedupe.abis.dto;

/**
 * The Class AbisInsertRequestDto.
 */
public class AbisInsertRequestDto {

	/** The id. */
	private String id;

	/** The ver. */
	private String ver;

	/** The request id. */
	private String requestId;

	/** The timestamp. */
	private String timestamp;

	/** The reference id. */
	private String referenceId;

	/** The reference URL. */
	private String referenceURL;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the ver.
	 *
	 * @return the ver
	 */
	public String getVer() {
		return ver;
	}

	/**
	 * Sets the ver.
	 *
	 * @param ver
	 *            the new ver
	 */
	public void setVer(String ver) {
		this.ver = ver;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId
	 *            the new request id
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp
	 *            the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the reference id.
	 *
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the reference id.
	 *
	 * @param referenceId
	 *            the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Gets the reference URL.
	 *
	 * @return the reference URL
	 */
	public String getReferenceURL() {
		return referenceURL;
	}

	/**
	 * Sets the reference URL.
	 *
	 * @param referenceURL
	 *            the new reference URL
	 */
	public void setReferenceURL(String referenceURL) {
		this.referenceURL = referenceURL;
	}
}
