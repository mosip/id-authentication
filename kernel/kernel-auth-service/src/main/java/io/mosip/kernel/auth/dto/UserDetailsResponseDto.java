package io.mosip.kernel.auth.dto;

import java.util.List;

import lombok.Data;


/**
 * 
 * Instantiates a new user details response dto.
 * @author Srinivasan
 * @since 1.0.0
 */
@Data
public class UserDetailsResponseDto {

	/** The user details. */
	List<UserDetailsDto> userDetails;
}
