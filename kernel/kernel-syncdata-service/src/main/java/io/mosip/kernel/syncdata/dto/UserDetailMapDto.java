package io.mosip.kernel.syncdata.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDetailMapDto {
	
private String userName;
	
	private String mail;
	
	private String mobile;
	
	private String langCode;
	
	private byte[] userPassword;
	
    private String name;
    
    private List<String> roles;


}
