package io.mosip.registration.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The DTO Class PreRegistration Data Sync Request.
 *
 * @author YASWANTH S
 * @since 1.0.0
 */

public class PreRegistrationDataSyncRequestDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Registration-client-Id. */
	@JsonProperty("registrationCenterId")
	private String registrationCenterId;
	
	/** The from-date. */
	@JsonProperty("fromDate")
	private String fromDate;
	
	/** The To-date. */
	@JsonProperty("toDate")
	private String toDate;
	
	/** The UserId. *//*
	@JsonProperty("user-id")
	private String userId;*/

	public String getRegistrationCenterId() {
		return registrationCenterId;
	}

	public void setRegistrationCenterId(String registrationCenterId) {
		this.registrationCenterId = registrationCenterId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	/*public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}*/

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PreRegistrationDataSyncRequestDTO [registrationCenterId=" + registrationCenterId + ", fromDate="
				+ fromDate + ", toDate=" + toDate + "]";
	}
}
