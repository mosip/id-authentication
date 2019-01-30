package io.mosip.preregistration.acknowledgement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Sanober Noor
 *
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AcknowledgementDTO {

	/**
	 * user name
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * Pre-Registration ID
	 */
	@JsonProperty("pre_registartion_id")
	private String preId;
	/**
	 * appointmentDate
	 */
	@JsonProperty("appointment_date")
	private String appointmentDate;
	/**
	 * appointmentTime
	 */
	@JsonProperty("appointment_time")
	private String appointmentTime;
	/**
	 * user mobile number
	 */
	@JsonProperty("mobile_number")
	private String mobNum;
	/**
	 * user email id
	 */
	@JsonProperty("email_id")
	private String emailID;

}
