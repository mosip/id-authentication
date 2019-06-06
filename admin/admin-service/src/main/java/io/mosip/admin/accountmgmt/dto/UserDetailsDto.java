package io.mosip.admin.accountmgmt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class UserDetailsDto {

	@JsonInclude(Include.NON_NULL)
	private String userId;

	@JsonInclude(Include.NON_NULL)
	private String firstName;

	@JsonInclude(Include.NON_NULL)
	private String lastName;

	@JsonInclude(Include.NON_NULL)
	private String mobile;

	@JsonInclude(Include.NON_NULL)
	private String mail;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	@JsonInclude(Include.NON_NULL)
	private String gender;

	@JsonInclude(Include.NON_NULL)
	private String langCode;

	@JsonInclude(Include.NON_NULL)
	private byte[] userPassword;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonInclude(Include.NON_NULL)
	private List<String> roles;

	@JsonInclude(Include.NON_NULL)
	private String role;

	@JsonInclude(Include.NON_NULL)
	private String rId;

	@JsonInclude(Include.NON_NULL)
	private boolean activationStatus;

	@JsonInclude(Include.NON_NULL)
	private boolean blackListedStatus;

	@JsonInclude(Include.NON_NULL)
	private boolean isDeleted;

	@JsonInclude(Include.NON_NULL)
	private LocalDateTime createdTimeStamp;

	@JsonInclude(Include.NON_NULL)
	private LocalDateTime updatedTimeStamp;

	@JsonInclude(Include.NON_NULL)
	private LocalDateTime deletedTimeStamp;

	@JsonInclude(Include.NON_NULL)
	private String zone;

	@JsonInclude(Include.NON_NULL)
	private String registrationId;

	@JsonInclude(Include.NON_NULL)
	private String address;

	@JsonInclude(Include.NON_NULL)
	private boolean isActive;

}
