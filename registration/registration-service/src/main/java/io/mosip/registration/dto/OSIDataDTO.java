package io.mosip.registration.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the Registration Operator Specific Information
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Getter
@Setter
public class OSIDataDTO extends BaseDTO {

	private String operatorID;
	private String supervisorID;
	// Below fields are used for Introducer or HOF or Parent
	private String introducerType;
	private boolean isOperatorAuthenticatedByPassword;
	private boolean isSuperviorAuthenticatedByPassword;
	private boolean isOperatorAuthenticatedByPIN;
	private boolean isSuperviorAuthenticatedByPIN;

}
