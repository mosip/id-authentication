package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the applicant demographic, biometric, proofs and parent
 * or guardian biometric details.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Identity extends BaseDTO {

	/** The ID schema version. */
	private String idSchemaVersion;

	/** The uin. */
	private String uin;

	/** The full name. */
	private ArrayPropertiesDTO fullName;

	/** The date of birth. */
	private SimplePropertiesDTO dateOfBirth;

	/** The age. */
	private String age;

	/** The gender. */
	private ArrayPropertiesDTO gender;

	/** The address line 1. */
	private ArrayPropertiesDTO addressLine1;

	/** The address line 2. */
	private ArrayPropertiesDTO addressLine2;

	/** The address line 3. */
	private ArrayPropertiesDTO addressLine3;

	/** The region. */
	private ArrayPropertiesDTO region;

	/** The province. */
	private ArrayPropertiesDTO province;

	/** The city. */
	private ArrayPropertiesDTO city;

	/** The postal code. */
	private String postalCode;

	/** The phone. */
	private SimplePropertiesDTO phone;

	/** The email. */
	private SimplePropertiesDTO email;

	/** The CNIE number. */
	private String cnieNumber;

	/** The local administrative authority. */
	private ArrayPropertiesDTO localAdministrativeAuthority;

	/** The parent or guardian name. */
	private ArrayPropertiesDTO parentOrGuardianName;

	/** The parent or guardian RID or UIN. */
	private String parentOrGuardianRIDOrUIN;

	/** The proof of address. */
	private DocumentDetailsDTO proofOfAddress;

	/** The proof of identity. */
	private DocumentDetailsDTO proofOfIdentity;

	/** The proof of relationship. */
	private DocumentDetailsDTO proofOfRelationship;

	/** The date of birth proof. */
	private DocumentDetailsDTO dateOfBirthProof;

	/** The individual biometrics. */
	private CBEFFFilePropertiesDTO individualBiometrics;

	/** The parent or guardian biometrics. */
	private CBEFFFilePropertiesDTO parentOrGuardianBiometrics;

	/**
	 * @return the idSchemaVersion
	 */
	public String getIdSchemaVersion() {
		return idSchemaVersion;
	}

	/**
	 * @param idSchemaVersion
	 *            the idSchemaVersion to set
	 */
	public void setIdSchemaVersion(String idSchemaVersion) {
		this.idSchemaVersion = idSchemaVersion;
	}

	/**
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

	/**
	 * @return the fullName
	 */
	public ArrayPropertiesDTO getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(ArrayPropertiesDTO fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the dateOfBirth
	 */
	public SimplePropertiesDTO getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(SimplePropertiesDTO dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * @return the gender
	 */
	public ArrayPropertiesDTO getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(ArrayPropertiesDTO gender) {
		this.gender = gender;
	}

	/**
	 * @return the addressLine1
	 */
	public ArrayPropertiesDTO getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(ArrayPropertiesDTO addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public ArrayPropertiesDTO getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(ArrayPropertiesDTO addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the addressLine3
	 */
	public ArrayPropertiesDTO getAddressLine3() {
		return addressLine3;
	}

	/**
	 * @param addressLine3
	 *            the addressLine3 to set
	 */
	public void setAddressLine3(ArrayPropertiesDTO addressLine3) {
		this.addressLine3 = addressLine3;
	}

	/**
	 * @return the region
	 */
	public ArrayPropertiesDTO getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(ArrayPropertiesDTO region) {
		this.region = region;
	}

	/**
	 * @return the province
	 */
	public ArrayPropertiesDTO getProvince() {
		return province;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(ArrayPropertiesDTO province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public ArrayPropertiesDTO getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(ArrayPropertiesDTO city) {
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
	public SimplePropertiesDTO getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(SimplePropertiesDTO phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public SimplePropertiesDTO getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(SimplePropertiesDTO email) {
		this.email = email;
	}

	/**
	 * @return the cnieNumber
	 */
	public String getCnieNumber() {
		return cnieNumber;
	}

	/**
	 * @param cnieNumber
	 *            the cnieNumber to set
	 */
	public void setCnieNumber(String cnieNumber) {
		this.cnieNumber = cnieNumber;
	}

	/**
	 * @return the localAdministrativeAuthority
	 */
	public ArrayPropertiesDTO getLocalAdministrativeAuthority() {
		return localAdministrativeAuthority;
	}

	/**
	 * @param localAdministrativeAuthority
	 *            the localAdministrativeAuthority to set
	 */
	public void setLocalAdministrativeAuthority(ArrayPropertiesDTO localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	/**
	 * @return the parentOrGuardianName
	 */
	public ArrayPropertiesDTO getParentOrGuardianName() {
		return parentOrGuardianName;
	}

	/**
	 * @param parentOrGuardianName
	 *            the parentOrGuardianName to set
	 */
	public void setParentOrGuardianName(ArrayPropertiesDTO parentOrGuardianName) {
		this.parentOrGuardianName = parentOrGuardianName;
	}

	/**
	 * @return the parentOrGuardianRIDOrUIN
	 */
	public String getParentOrGuardianRIDOrUIN() {
		return parentOrGuardianRIDOrUIN;
	}

	/**
	 * @param parentOrGuardianRIDOrUIN
	 *            the parentOrGuardianRIDOrUIN to set
	 */
	public void setParentOrGuardianRIDOrUIN(String parentOrGuardianRIDOrUIN) {
		this.parentOrGuardianRIDOrUIN = parentOrGuardianRIDOrUIN;
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
	 * @return the dateOfBirthProof
	 */
	public DocumentDetailsDTO getDateOfBirthProof() {
		return dateOfBirthProof;
	}

	/**
	 * @param dateOfBirthProof
	 *            the dateOfBirthProof to set
	 */
	public void setDateOfBirthProof(DocumentDetailsDTO dateOfBirthProof) {
		this.dateOfBirthProof = dateOfBirthProof;
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
