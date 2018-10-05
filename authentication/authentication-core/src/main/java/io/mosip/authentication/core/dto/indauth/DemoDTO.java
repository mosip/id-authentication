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

	/**
	 * International Language Code indicating the primary language (ISO-639-1) –
	 * default primary language code
	 */
	private String langPri;

	/** PersonalIdentityDTO gives individual identity details */
	private PersonalIdentityDTO personalIdentityDTO;

	/** PersonalAddressDTO gives individual address details */
	private PersonalAddressDTO personalAddressDTO;

	/** PersonalFullAddressDTO gives individual full address details */
	private PersonalFullAddressDTO personalFullAddressDTO;

}
