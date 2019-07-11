package io.mosip.registration.processor.core.packet.dto.idjson;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class contains the applicant demographic, biometric, proofs and parent
 * or guardian biometric details.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */

@JsonInclude(value = Include.NON_EMPTY)
@Data
public class Identity { 

	/** The ID schema version. */
	@JsonProperty(value = "IDSchemaVersion")
	private double idSchemaVersion;

	/** The uin. */
	@JsonProperty(value = "UIN")
	private BigInteger uin;

	/** The full name. */
	private List<ValuesDTO> fullName;

	/** The date of birth. */
	private String dateOfBirth;

	/** The age. */
	private Integer age;

	/** The gender. */
	private List<ValuesDTO> gender;

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
	private BigInteger cnieNumber;

	/** The local administrative authority. */
	private List<ValuesDTO> localAdministrativeAuthority;

	/** The parent or guardian RID or UIN. */
	private BigInteger parentOrGuardianRIDOrUIN;

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

	/** The individual biometrics. */
	private CBEFFFilePropertiesDTO individualBiometrics;

	/** The parent or guardian biometrics. */
	private CBEFFFilePropertiesDTO parentOrGuardianBiometrics;

	@Override
	public String toString() {
		return "Identity [idSchemaVersion=" + idSchemaVersion + ", uin=" + uin + ", fullName=" + fullName
				+ ", dateOfBirth=" + dateOfBirth + ", age=" + age + ", gender=" + gender + ", addressLine1="
				+ addressLine1 + ", addressLine2=" + addressLine2 + ", addressLine3=" + addressLine3 + ", region="
				+ region + ", province=" + province + ", city=" + city + ", postalCode=" + postalCode + ", phone="
				+ phone + ", email=" + email + ", cnieNumber=" + cnieNumber + ", localAdministrativeAuthority="
				+ localAdministrativeAuthority + ", parentOrGuardianRIDOrUIN=" + parentOrGuardianRIDOrUIN
				+ ", parentOrGuardianName=" + parentOrGuardianName + ", proofOfAddress=" + proofOfAddress
				+ ", proofOfIdentity=" + proofOfIdentity + ", proofOfRelationship=" + proofOfRelationship
				+ ", proofOfDateOfBirth=" + proofOfDateOfBirth + ", individualBiometrics=" + individualBiometrics
				+ ", parentOrGuardianBiometrics=" + parentOrGuardianBiometrics + "]";
	}

}
