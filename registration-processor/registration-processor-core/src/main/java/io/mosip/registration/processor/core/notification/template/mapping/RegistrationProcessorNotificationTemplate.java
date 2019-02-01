package io.mosip.registration.processor.core.notification.template.mapping;

import java.math.BigInteger;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Instantiates a new registration processor notification template.
 * 
 * @author M1048358
 */
@Data
@Component
public class RegistrationProcessorNotificationTemplate {

	/** The first name. */
	private String firstName;
	
	/** The phone number. */
	private String phoneNumber;
	
	/** The email ID. */
	private String emailID;
	
	/** The date of birth. */
	private String dateOfBirth;
	
	/** The age. */
	private Integer age;
	
	/** The gender. */
	private String gender;
	
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
	
	/** The city. */
	private String city;
	
	/** The postal code. */
	private String postalCode;
	
	/** The parent or guardian name. */
	private String parentOrGuardianName;
	
	/** The parent or guardian RID or UIN. */
	private BigInteger parentOrGuardianRIDOrUIN;
	
	/** The proof of address. */
	private String proofOfAddress;
	
	/** The proof of identity. */
	private String proofOfIdentity;
	
	/** The proof of relationship. */
	private String proofOfRelationship;
	
	/** The proof of date of birth. */
	private String proofOfDateOfBirth;
	
	/** The individual biometrics. */
	private String individualBiometrics;
	
	/** The parent or guardian biometrics. */
	private String parentOrGuardianBiometrics;
	
	/** The local administrative authority. */
	private String localAdministrativeAuthority;
	
	/** The id schema version. */
	private Double idSchemaVersion;
	
	/** The cnie number. */
	private Integer cnieNumber;

}
