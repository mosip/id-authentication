package io.mosip.registration.dto;

import java.sql.Timestamp;
import java.util.Set;

import lombok.Data;

/**
 * DTO class for User info
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class UserDTO {	
	private String id;	
	private String regid;
	private String salt;
	private String name;
	private String email;	
	private String mobile;	
	private String statusCode;	
	private String langCode;
	private Timestamp lastLoginDtimes;	
	private String lastLoginMethod;	
	private Integer unsuccessfulLoginCount;
	private Timestamp userlockTillDtimes;
	private Boolean isActive;
	private Boolean isDeleted;
	private Timestamp delDtimes;

	private Set<UserRoleDTO> userRole;	
	private Set<UserMachineMappingDTO> userMachineMapping;	
	private Set<UserBiometricDTO> userBiometric;	
	private UserPasswordDTO userPassword;	
	private RegCenterUserDTO regCenterUser;	
}
