package io.mosip.registration.dto;

import lombok.Data;

/**
 * DTO class for User Role details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class UserRoleDTO {	
	private String usrId;
	private String roleCode;
	private String langCode;
	private boolean isActive;
}
