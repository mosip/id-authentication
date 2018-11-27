package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityValue {

	/** Variable to hold language */
	private String language;

	/** Variable to hold value */
	private String value;
}
