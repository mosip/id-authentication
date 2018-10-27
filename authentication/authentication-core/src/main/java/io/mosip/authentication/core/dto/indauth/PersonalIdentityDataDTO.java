package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factory class.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalIdentityDataDTO {

	/** Factory class for demographic details */
	private DemoDTO demo;

	/** BioDTO for {@link BioType} */
	private BioDTO bio;

	/** PinDTO for {@link PinType} */
	private PinDTO pin;

}
