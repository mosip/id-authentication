package io.mosip.kernel.auth.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserDetailsDto {

	
	private String userName;
	
	private String firstName;
	
	private String lastName;
	
	private String contactNo;
	
	private String emailID;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private String gender;
	
	private String appId;
	
	private String langCode;
	
	private byte[] userPassword;
	
	private String name;
	
	private String role;
	
	private String rId;
	
	private String isActive;
}
