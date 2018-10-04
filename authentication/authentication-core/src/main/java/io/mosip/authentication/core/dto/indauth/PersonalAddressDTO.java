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

	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String state;
	private String country;
	private String pinCode;
}
