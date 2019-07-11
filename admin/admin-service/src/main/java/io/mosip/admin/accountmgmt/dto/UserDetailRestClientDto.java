package io.mosip.admin.accountmgmt.dto;

import java.util.List;

import lombok.Data;


/**
 * 
 * @author Srinivasan
 *
 */
@Data
public class UserDetailRestClientDto {

	/** The user details. */
	List<UserDetailsDto> userDetails;
}
