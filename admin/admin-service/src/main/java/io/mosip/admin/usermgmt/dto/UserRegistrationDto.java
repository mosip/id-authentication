package io.mosip.admin.usermgmt.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
	
	private String userName;
	
	private String firstName;
	
	private String lastName;
	
	private String contactNo;
	
	private String emailID;
	
	private LocalDate dateOfBirth;
	
	private String gender;
	
	private String role;
	
}
