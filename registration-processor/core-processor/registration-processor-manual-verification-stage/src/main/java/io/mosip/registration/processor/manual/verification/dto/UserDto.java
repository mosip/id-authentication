package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;

import lombok.Data;
	
/**
 * The {@link UserDto} class.
 *
 * @author Shuchita
 * @author Rishabh Keshari
 * @since 0.0.1
 */
@Data
public class UserDto implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The user id. */
	private String userId;

	/** The source name. */
	private String matchType;

}
