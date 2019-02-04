package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Instantiates a new identity.
 */

/**
 * Instantiates a new identity.
 */

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
	private int age;

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
	private String individualBiometrics;

	/** The city. */
	private String city;
	/** The address line 1. */
	private String addressLine1;

	/** The address line 2. */
	private String addressLine2;

	/** The address line 3. */
	private String addressLine3;

	/** The region. */
	private String region;

	/** The province. */
	private String province;

	/** The postal code. */
	private String postalCode;

	/** The phone number. */
	private String phoneNumber;

	/** The email ID. */
	private String emailID;

	/** The local administrative authority. */
	private String localAdministrativeAuthority;

	/** The id schema version. */
	private double idSchemaVersion;

	/** The cnie number. */
	private int cnieNumber;
}
