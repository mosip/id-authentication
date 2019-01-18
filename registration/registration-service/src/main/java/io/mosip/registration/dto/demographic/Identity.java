package io.mosip.registration.dto.demographic;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the applicant demographic, biometric, proofs and parent
 * or guardian biometric details.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@JsonInclude(value = Include.NON_NULL)
public class Identity extends BaseDTO {

	/** The ID schema version. */
	@JsonProperty("IDSchemaVersion")
	private double idSchemaVersion;

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

	/**
	 * @return the idSchemaVersion
	 */
	public double getIdSchemaVersion() {
		return idSchemaVersion;
	}

	/**
	 * @param idSchemaVersion
	 *            the idSchemaVersion to set
	 */
	public void setIdSchemaVersion(double idSchemaVersion) {
		this.idSchemaVersion = idSchemaVersion;
	}

	/**
	 * @return the uin
	 */
	public BigInteger getUin() {
		return uin;
	}

	/**
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(BigInteger uin) {
		this.uin = uin;
	}

	/**
	 * @return the fullName
	 */
	public List<ValuesDTO> getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(List<ValuesDTO> fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the dateOfBirth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(Integer age) {
		this.age = age;
	}

	/**
	 * @return the gender
	 */
	public List<ValuesDTO> getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(List<ValuesDTO> gender) {
		this.gender = gender;
	}

	/**
	 * @return the addressLine1
	 */
	public List<ValuesDTO> getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(List<ValuesDTO> addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public List<ValuesDTO> getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(List<ValuesDTO> addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the addressLine3
	 */
	public List<ValuesDTO> getAddressLine3() {
		return addressLine3;
	}

	/**
	 * @param addressLine3
	 *            the addressLine3 to set
	 */
	public void setAddressLine3(List<ValuesDTO> addressLine3) {
		this.addressLine3 = addressLine3;
	}

	/**
	 * @return the region
	 */
	public List<ValuesDTO> getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(List<ValuesDTO> region) {
		this.region = region;
	}

	/**
	 * @return the province
	 */
	public List<ValuesDTO> getProvince() {
		return province;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(List<ValuesDTO> province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public List<ValuesDTO> getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(List<ValuesDTO> city) {
		this.city = city;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the cnieNumber
	 */
	public BigInteger getCnieNumber() {
		return cnieNumber;
	}

	/**
	 * @param cnieNumber
	 *            the cnieNumber to set
	 */
	public void setCnieNumber(BigInteger cnieNumber) {
		this.cnieNumber = cnieNumber;
	}

	/**
	 * @return the localAdministrativeAuthority
	 */
	public List<ValuesDTO> getLocalAdministrativeAuthority() {
		return localAdministrativeAuthority;
	}

	/**
	 * @param localAdministrativeAuthority
	 *            the localAdministrativeAuthority to set
	 */
	public void setLocalAdministrativeAuthority(List<ValuesDTO> localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	/**
	 * @return the parentOrGuardianRIDOrUIN
	 */
	public BigInteger getParentOrGuardianRIDOrUIN() {
		return parentOrGuardianRIDOrUIN;
	}

	/**
	 * @param parentOrGuardianRIDOrUIN
	 *            the parentOrGuardianRIDOrUIN to set
	 */
	public void setParentOrGuardianRIDOrUIN(BigInteger parentOrGuardianRIDOrUIN) {
		this.parentOrGuardianRIDOrUIN = parentOrGuardianRIDOrUIN;
	}

	/**
	 * @return the parentOrGuardianName
	 */
	public List<ValuesDTO> getParentOrGuardianName() {
		return parentOrGuardianName;
	}

	/**
	 * @param parentOrGuardianName
	 *            the parentOrGuardianName to set
	 */
	public void setParentOrGuardianName(List<ValuesDTO> parentOrGuardianName) {
		this.parentOrGuardianName = parentOrGuardianName;
	}

	/**
	 * @return the proofOfAddress
	 */
	public DocumentDetailsDTO getProofOfAddress() {
		return proofOfAddress;
	}

	/**
	 * @param proofOfAddress
	 *            the proofOfAddress to set
	 */
	public void setProofOfAddress(DocumentDetailsDTO proofOfAddress) {
		this.proofOfAddress = proofOfAddress;
	}

	/**
	 * @return the proofOfIdentity
	 */
	public DocumentDetailsDTO getProofOfIdentity() {
		return proofOfIdentity;
	}

	/**
	 * @param proofOfIdentity
	 *            the proofOfIdentity to set
	 */
	public void setProofOfIdentity(DocumentDetailsDTO proofOfIdentity) {
		this.proofOfIdentity = proofOfIdentity;
	}

	/**
	 * @return the proofOfRelationship
	 */
	public DocumentDetailsDTO getProofOfRelationship() {
		return proofOfRelationship;
	}

	/**
	 * @param proofOfRelationship
	 *            the proofOfRelationship to set
	 */
	public void setProofOfRelationship(DocumentDetailsDTO proofOfRelationship) {
		this.proofOfRelationship = proofOfRelationship;
	}

	/**
	 * @return the proofOfDateOfBirth
	 */
	public DocumentDetailsDTO getProofOfDateOfBirth() {
		return proofOfDateOfBirth;
	}

	/**
	 * @param proofOfDateOfBirth
	 *            the proofOfDateOfBirth to set
	 */
	public void setProofOfDateOfBirth(DocumentDetailsDTO proofOfDateOfBirth) {
		this.proofOfDateOfBirth = proofOfDateOfBirth;
	}

	/**
	 * @return the individualBiometrics
	 */
	public CBEFFFilePropertiesDTO getIndividualBiometrics() {
		return individualBiometrics;
	}

	/**
	 * @param individualBiometrics
	 *            the individualBiometrics to set
	 */
	public void setIndividualBiometrics(CBEFFFilePropertiesDTO individualBiometrics) {
		this.individualBiometrics = individualBiometrics;
	}

	/**
	 * @return the parentOrGuardianBiometrics
	 */
	public CBEFFFilePropertiesDTO getParentOrGuardianBiometrics() {
		return parentOrGuardianBiometrics;
	}

	/**
	 * @param parentOrGuardianBiometrics
	 *            the parentOrGuardianBiometrics to set
	 */
	public void setParentOrGuardianBiometrics(CBEFFFilePropertiesDTO parentOrGuardianBiometrics) {
		this.parentOrGuardianBiometrics = parentOrGuardianBiometrics;
	}

}
