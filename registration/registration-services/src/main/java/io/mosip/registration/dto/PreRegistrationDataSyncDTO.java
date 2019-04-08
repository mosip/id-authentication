package io.mosip.registration.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The DTO Class PreRegistration Data Sync.
 *
 * @author YASWANTH S
 * @since 1.0.0
 */
public class PreRegistrationDataSyncDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	@JsonProperty("version")
	private String version;
	@JsonProperty("requesttime")
	private String requesttime;
	
	/**
	 * object to accept json
	 */
	@JsonProperty("request")
	private PreRegistrationDataSyncRequestDTO dataSyncRequestDto;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVer() {
		return version;
	}

	public void setVer(String version) {
		this.version = version;
	}

	public String getReqTime() {
		return requesttime;
	}

	public void setReqTime(String requesttime) {
		this.requesttime = requesttime;
	}

	public PreRegistrationDataSyncRequestDTO getDataSyncRequestDto() {
		return dataSyncRequestDto;
	}

	public void setDataSyncRequestDto(PreRegistrationDataSyncRequestDTO dataSyncRequestDto) {
		this.dataSyncRequestDto = dataSyncRequestDto;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
