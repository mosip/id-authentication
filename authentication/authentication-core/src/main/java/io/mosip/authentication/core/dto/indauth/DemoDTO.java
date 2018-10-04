package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factory class for {@link PersonalIdentityDTO}, {@link PersonalAddressDTO} and
 * {@link PersonalFullAddressDTO}
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoDTO {

	private String langCode;
	private PersonalIdentityDTO personalIdentityDTO;
	private PersonalAddressDTO personalAddressDTO;
	private PersonalFullAddressDTO personalFullAddressDTO;

}
