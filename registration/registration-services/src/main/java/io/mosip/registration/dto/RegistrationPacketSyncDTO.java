package io.mosip.registration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationPacketSyncDTO {

	private String id;
	private String requestTimestamp;
	private String version;
	
	@JsonProperty("request")
	private List<SyncRegistrationDTO> syncRegistrationDTOs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(String requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<SyncRegistrationDTO> getSyncRegistrationDTOs() {
		return syncRegistrationDTOs;
	}

	public void setSyncRegistrationDTOs(List<SyncRegistrationDTO> syncRegistrationDTOs) {
		this.syncRegistrationDTOs = syncRegistrationDTOs;
	}

}
