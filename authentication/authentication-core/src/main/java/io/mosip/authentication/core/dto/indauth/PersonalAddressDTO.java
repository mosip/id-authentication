package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used for match address based on match strategy and match value.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalAddressDTO {

	/** Address line 1 of the individual */
	private String addrLine1;

	/** Address line 2 of the individual */
	private String addrLine2;

	/** Address line 3 of the individual */
	private String addrLine;

	/** Registered state of the individual */
	private String state;

	/** Registered country of the individual */
	private String country;

	/** Registered pinCode of the individual */
	private String pinCode;
}
