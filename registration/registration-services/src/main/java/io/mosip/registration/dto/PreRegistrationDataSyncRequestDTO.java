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
	@JsonProperty("registration-client-id")
	private String regClientId;
	
	/** The from-date. */
	@JsonProperty("from-date")
	private String fromDate;
	
	/** The To-date. */
	@JsonProperty("to-date")
	private String toDate;
	
	/** The UserId. */
	@JsonProperty("user-id")
	private String userId;

	public String getRegClientId() {
		return regClientId;
	}

	public void setRegClientId(String regClientId) {
		this.regClientId = regClientId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
