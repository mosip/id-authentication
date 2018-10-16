package io.mosip.registration.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the Registration Operator Specific Information
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OSIDataDTO extends BaseDTO {
	
	private String operatorID;
	private String supervisorID;
	private String supervisorName;
	// Below fields are used for Introducer or HOF or Parent
	private String introducerType;
	private String introducerName;
}
