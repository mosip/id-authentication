package io.mosip.kernel.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserDetailsDto {

	
	private String userId;
	
	private String firstName;
	
	private String lastName;
	
	private String mobile;
	
	private String mail;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private String gender;
	
	private String langCode;
	
	private byte[] userPassword;
	
	private String name;
	
	private String role;
	
	private String rId;
	
	private boolean activationStatus;
	
	private boolean blackListedStatus;
	
	private boolean isDeleted;
	
	private LocalDateTime createdTimeStamp;
	
	private LocalDateTime updatedTimeStamp;
	
	private LocalDateTime deletedTimeStamp;
	
	private String zone;
	
	private String registrationId;
	
	private String address;
	
	private boolean isActive;
}
