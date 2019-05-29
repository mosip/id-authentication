package io.mosip.admin.accountmgmt.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The Class UserDetailDto.
 *
 * @author Srinivasan
 * @since 1.0.0
 */

/**
 * Instantiates a new user detail dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {

	private String userId;
	private String mobile;
	private String mail;
	@JsonIgnore
	private String langCode;
	@JsonIgnore
	private String userPassword;
	private String name;
	private String role;
	@JsonIgnore
	private String rId;
	private String firstName;
	private String lastName;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	private String gender;
	private List<String> roles;
	private boolean isActive;

}
