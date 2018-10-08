package io.mosip.authentication.core.spi.idauth.demo;

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

	/** Address line 1 of the individual for primary language */
	private String addrLine1Pri;

	/** Address line 2 of the individual for primary language */
	private String addrLine2Pri;

	/** Address line 3 of the individual for primary language */
	private String addrLine3Pri;

	/** Registered state of the individual */
	//private String state; // TODO

	/** Registered country of the individual for primary language */
	private String countryPri;

	/** Registered pinCode of the individual for primary language */
	private String pinCodePri;
	
	/** Address line 1 of the individual for secondary language */
	private String addrLine1Sec;

	/** Address line 2 of the individual for secondary language */
	private String addrLine2Sec;

	/** Address line 3 of the individual for secondary language */
	private String addrLine3Sec;
	
	/** Registered country of the individual for secondary language */
	private String countrySec;

	/** Registered pinCode of the individual for secondary language */
	private String pinCodeSec;
}
