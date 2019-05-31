package io.mosip.preregistration.core.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanober Noor
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

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
	 * batch config field
	 */
	private boolean isBatch;
	
	/**

	 * additionalRecipient for notififcation
	 */
	private boolean additionalRecipient;   


}
