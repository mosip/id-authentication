package io.mosip.kernel.syncdata.dto;

import lombok.Data;

@Data
public class UserDetailDto {

	private String userName;
	
	private String mail;
	
	private String mobile;
	
	private byte[] userPassword;
	
    private String userId;
    
    private String role;

	
}
