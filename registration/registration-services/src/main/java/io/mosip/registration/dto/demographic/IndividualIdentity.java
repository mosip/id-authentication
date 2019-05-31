package io.mosip.registration.dto.demographic;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the applicant demographic, biometric, proofs and parent
 * or guardian biometric details.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class IndividualIdentity extends Identity {

	/** The uin. */
	@JsonProperty("UIN")
	private BigInteger uin;

	/** The full name. */
	private List<ValuesDTO> fullName;

	/** The date of birth. */
	private String dateOfBirth;

	/** The age. */
	private Integer age;

	/** The gender. */
	private List<ValuesDTO> gender;
	
	/** The full name. */
	private List<ValuesDTO> residenceStatus;

	/** The address line 1. */
	private List<ValuesDTO> addressLine1;

	/** The address line 2. */
	private List<ValuesDTO> addressLine2;

	/** The address line 3. */
	private List<ValuesDTO> addressLine3;

	/** The region. */
	private List<ValuesDTO> region;

	/** The province. */
	private List<ValuesDTO> province;

	/** The city. */
	private List<ValuesDTO> city;

	/** The postal code. */
	private String postalCode;

	/** The phone. */
	private String phone;

	/** The email. */
	private String email;

	/** The CNIE number. */
	@JsonProperty("CNIENumber")
	private String cnieNumber;

	/** The local administrative authority. */
	private List<ValuesDTO> localAdministrativeAuthority;

	/** The parent or guardian RID. */
	private BigInteger parentOrGuardianRID;

	/** The parent or guardian UIN. */
	private BigInteger parentOrGuardianUIN;

	/** The parent or guardian name. */
	private List<ValuesDTO> parentOrGuardianName;

	/** The proof of address. */
	private DocumentDetailsDTO proofOfAddress;

	/** The proof of identity. */
	private DocumentDetailsDTO proofOfIdentity;

	/** The proof of relationship. */
	private DocumentDetailsDTO proofOfRelationship;

	/** The date of birth proof. */
	private DocumentDetailsDTO proofOfDateOfBirth;

	/** The proof of exception. */
	private DocumentDetailsDTO proofOfException;

	/** The individual biometrics. */
	private CBEFFFilePropertiesDTO individualBiometrics;

	/** The parent or guardian biometrics. */
	private CBEFFFilePropertiesDTO parentOrGuardianBiometrics;

}
