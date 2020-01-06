package io.mosip.registration.mdm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the Error details
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class Error {
	private String errorCode;
	private String errorInfo;
}
