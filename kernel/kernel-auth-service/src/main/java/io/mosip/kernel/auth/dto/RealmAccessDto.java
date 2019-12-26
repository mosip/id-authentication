package io.mosip.kernel.auth.dto;

import lombok.Data;


/**
 * Instantiates a new realm access dto.
 * @author Srinivasan
 */
@Data
public class RealmAccessDto {

	/** The roles. */
	private String[] roles;
}
