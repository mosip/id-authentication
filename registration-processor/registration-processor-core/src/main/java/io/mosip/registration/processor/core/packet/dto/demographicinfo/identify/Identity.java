package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Instantiates a new identity.
 */
@Data
@Component
public class Identity {

	/** The name. */
	private IdentityJsonValues name;

	/** The gender. */
	private IdentityJsonValues gender;

	/** The dob. */
	private IdentityJsonValues dob;

	/** The age. */
	private IdentityJsonValues age;

	/** The pheonitic name. */
	private IdentityJsonValues pheoniticName;

	/** The poa. */
	private IdentityJsonValues poa;

	/** The por. */
	private IdentityJsonValues por;

	/** The poi. */
	private IdentityJsonValues poi;

	/** The pob. */
	private IdentityJsonValues pob;

	/** The individual biometrics. */
	private IdentityJsonValues individualBiometrics;

	/** The city. */
	private IdentityJsonValues city;

	/** The address line 1. */
	private IdentityJsonValues addressLine1;

	/** The address line 2. */
	private IdentityJsonValues addressLine2;

	/** The address line 3. */
	private IdentityJsonValues addressLine3;

	/** The region. */
	private IdentityJsonValues region;

	/** The province. */
	private IdentityJsonValues province;

	/** The postal code. */
	private IdentityJsonValues postalCode;

	/** The phone number. */
	private IdentityJsonValues phone;

	/** The email ID. */
	private IdentityJsonValues email;

	/** The local administrative authority. */
	private IdentityJsonValues localAdministrativeAuthority;

	/** The id schema version. */
	private IdentityJsonValues idschemaversion;

	/** The cnie number. */
	private IdentityJsonValues cnienumber;

}
