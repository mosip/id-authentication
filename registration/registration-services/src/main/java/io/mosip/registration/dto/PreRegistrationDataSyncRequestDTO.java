package io.mosip.registration.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PreRegistrationDataSync RequestDTO
 * @author YASWANTH S
 * @since 1.0.0
 */

public class PreRegistrationDataSyncRequestDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Registration-client-Id. */
	@JsonProperty("registartion-client-id")
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
