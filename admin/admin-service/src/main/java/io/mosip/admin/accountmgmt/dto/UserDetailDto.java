package io.mosip.admin.accountmgmt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Auto-generated Javadoc
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
	
}
