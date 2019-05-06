package io.mosip.registration.processor.core.packet.dto.abis;

/**
 * The Class AbisPingRequestDto.
 * @author M1048860 Kiran Raj
 */
public class AbisPingRequestDto {
	
	/** The id. */
	private String id;

	/** The ver. */
	private String ver;

	/** The request id. */
	private String requestId;

	/** The timestamp. */
	private String timestamp;

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
	 * @param id the new id
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
	 * @param ver the new ver
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
	 * @param requestId the new request id
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
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


}
