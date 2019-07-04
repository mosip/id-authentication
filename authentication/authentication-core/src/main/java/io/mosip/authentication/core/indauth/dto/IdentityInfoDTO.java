package io.mosip.authentication.core.indauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * General-purpose of {@code IdentityInfoDTO} class used to provide Identity
 * info's
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityInfoDTO {

	/** Variable to hold language */
	private String language;

	/** Variable to hold value */
	private String value;
}
