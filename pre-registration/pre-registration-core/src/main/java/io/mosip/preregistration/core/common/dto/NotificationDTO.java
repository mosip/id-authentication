package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sanober Noor
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NotificationDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	/**
	 * user name
	 */
	private String name;

	/**
	 * Pre-Registration ID
	 */
	private String preRegistrationId;
	/**
	 * appointmentDate
	 */
	private String appointmentDate;
	/**
	 * appointmentTime
	 */
	private String appointmentTime;
	/**
	 * user mobile number
	 */
	private String mobNum;
	/**
	 * user email id
	 */
	private String emailID;
	/**
	 * additionalRecipient for notififcation
	 */
	private boolean additionalRecipient;
	
	/**
	 * batch config field
	 */
	private Boolean isBatch;
	
  

}
