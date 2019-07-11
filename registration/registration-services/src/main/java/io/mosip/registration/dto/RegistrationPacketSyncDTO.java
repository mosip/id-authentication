package io.mosip.registration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The DTO Class RegistrationPacketSyncDTO.
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 */
public class RegistrationPacketSyncDTO {

	private String id;
	private String 	requesttime;
	private String version;
	
	@JsonProperty("request")
	private List<SyncRegistrationDTO> syncRegistrationDTOs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getRequesttime() {
		return requesttime;
	}

	public void setRequesttime(String requesttime) {
		this.requesttime = requesttime;
	}
	
	

}
