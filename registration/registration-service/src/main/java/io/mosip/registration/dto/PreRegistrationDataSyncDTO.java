package io.mosip.registration.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PreRegistration DataSync DTO
 * @author YASWANTH S
 * @since 1.0.0
 */
public class PreRegistrationDataSyncDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String ver;
	private String reqTime;
	
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
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
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
