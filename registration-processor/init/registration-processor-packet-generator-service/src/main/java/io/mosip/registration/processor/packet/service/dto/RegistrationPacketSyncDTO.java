package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import lombok.Data;

/**
 * The Class RegistrationPacketSyncDTO.
 * 
 * @author Sowmya
 */
@Data
public class RegistrationPacketSyncDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5472421964614022195L;

	/** The id. */
	private String id;

	/** The request timestamp. */
	private String requestTimestamp;

	/** The version. */
	private String version;

	/** The sync registration DT os. */
	@JsonProperty("request")
	private List<SyncRegistrationDto> syncRegistrationDTOs;

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
	 * Gets the request timestamp.
	 *
	 * @return the request timestamp
	 */
	public String getRequestTimestamp() {
		return requestTimestamp;
	}

	/**
	 * Sets the request timestamp.
	 *
	 * @param requestTimestamp
	 *            the new request timestamp
	 */
	public void setRequestTimestamp(String requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the sync registration DT os.
	 *
	 * @return the sync registration DT os
	 */
	public List<SyncRegistrationDto> getSyncRegistrationDTOs() {
		return syncRegistrationDTOs;
	}

	/**
	 * Sets the sync registration DT os.
	 *
	 * @param syncRegistrationDTOs
	 *            the new sync registration DT os
	 */
	public void setSyncRegistrationDTOs(List<SyncRegistrationDto> syncRegistrationDTOs) {
		this.syncRegistrationDTOs = syncRegistrationDTOs;
	}

}
