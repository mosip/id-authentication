package io.mosip.registration.processor.core.notification.template.mapping;

import java.util.Arrays;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import lombok.Data;

/**
 * Instantiates a new notification template.
 */
@Data
public class NotificationTemplate {

	/** The first name. */
	private JsonValue[] firstName;

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public JsonValue[] getFirstName() {
		return Arrays.copyOf(firstName, firstName.length);
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName
	 *            the new first name
	 */
	public void setFirstName(JsonValue[] firstName) {
		this.firstName = firstName != null ? firstName : null;
	}

	/** The phone number. */
	private String phoneNumber;

	/** The email ID. */
	private String emailID;

	/** The date of birth. */
	private String dateOfBirth;

	/** The age. */
	private Long age;

	/** The gender. */
	private JsonValue[] gender;

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public JsonValue[] getGender() {
		return Arrays.copyOf(gender, gender.length);
	}

	/**
	 * Sets the gender.
	 *
	 * @param gender
	 *            the new gender
	 */
	public void setGender(JsonValue[] gender) {
		this.gender = gender != null ? gender : null;
	}

	/** The address line 1. */
	private JsonValue[] addressLine1;

	/**
	 * Gets the address line 1.
	 *
	 * @return the address line 1
	 */
	public JsonValue[] getAddressLine1() {
		if (addressLine1 != null)
			return Arrays.copyOf(addressLine1, addressLine1.length);
		return null;
	}

	/**
	 * Sets the address line 1.
	 *
	 * @param addressLine1
	 *            the new address line 1
	 */
	public void setAddressLine1(JsonValue[] addressLine1) {
		this.addressLine1 = addressLine1 != null ? addressLine1 : null;
	}

	/** The address line 2. */
	private JsonValue[] addressLine2;

	/**
	 * Gets the address line 2.
	 *
	 * @return the address line 2
	 */
	public JsonValue[] getAddressLine2() {
		if (addressLine2 != null)
			return Arrays.copyOf(addressLine2, addressLine2.length);
		return null;
	}

	/**
	 * Sets the address line 2.
	 *
	 * @param addressLine2
	 *            the new address line 2
	 */
	public void setAddressLine2(JsonValue[] addressLine2) {
		this.addressLine2 = addressLine2 != null ? addressLine2 : null;
	}

	/** The address line 3. */
	private JsonValue[] addressLine3;

	/**
	 * Gets the address line 3.
	 *
	 * @return the address line 3
	 */
	public JsonValue[] getAddressLine3() {
		if (addressLine3 != null)
			return Arrays.copyOf(addressLine3, addressLine3.length);
		return null;
	}

	/**
	 * Sets the address line 3.
	 *
	 * @param addressLine3
	 *            the new address line 3
	 */
	public void setAddressLine3(JsonValue[] addressLine3) {
		this.addressLine3 = addressLine3 != null ? addressLine3 : null;
	}

	/** The region. */
	private JsonValue[] region;

	/**
	 * Gets the region.
	 *
	 * @return the region
	 */
	public JsonValue[] getRegion() {
		if (region != null)
			return Arrays.copyOf(region, region.length);
		return null;
	}

	/**
	 * Sets the region.
	 *
	 * @param region
	 *            the new region
	 */
	public void setRegion(JsonValue[] region) {
		this.region = region != null ? region : null;
	}

	/** The province. */
	private JsonValue[] province;

	/**
	 * Gets the province.
	 *
	 * @return the province
	 */
	public JsonValue[] getProvince() {
		if (province != null)
			return Arrays.copyOf(province, province.length);
		return null;
	}

	/**
	 * Sets the province.
	 *
	 * @param province
	 *            the new province
	 */
	public void setProvince(JsonValue[] province) {
		this.province = province != null ? province : null;
	}

	/** The city. */
	private JsonValue[] city;

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public JsonValue[] getCity() {
		if (city != null)
			return Arrays.copyOf(city, city.length);
		return null;
	}

	/**
	 * Sets the city.
	 *
	 * @param city
	 *            the new city
	 */
	public void setCity(JsonValue[] city) {
		this.city = city != null ? city : null;
	}

	/** The postal code. */
	private String postalCode;

	/** The parent or guardian RID or UIN. */
	private String parentOrGuardianRIDOrUIN;

	/** The proof of address. */
	private String proofOfAddress;

	/**
	 * Gets the proof of address.
	 *
	 * @return the proof of address
	 */
	public String getProofOfAddress() {

		return proofOfAddress;
	}

	/**
	 * Sets the proof of address.
	 *
	 * @param proofOfAddress
	 *            the new proof of address
	 */
	public void setProofOfAddress(String proofOfAddress) {
		this.proofOfAddress = proofOfAddress;
	}

	/** The proof of identity. */
	private String proofOfIdentity;

	/**
	 * Gets the proof of identity.
	 *
	 * @return the proof of identity
	 */
	public String getProofOfIdentity() {

		return proofOfIdentity;
	}

	/**
	 * Sets the proof of identity.
	 *
	 * @param proofOfIdentity
	 *            the new proof of identity
	 */
	public void setProofOfIdentity(String proofOfIdentity) {
		this.proofOfIdentity = proofOfIdentity;
	}

	/** The proof of relationship. */
	private String proofOfRelationship;

	/**
	 * Gets the proof of relationship.
	 *
	 * @return the proof of relationship
	 */
	public String getProofOfRelationship() {
		return proofOfRelationship;
	}

	/**
	 * Sets the proof of relationship.
	 *
	 * @param proofOfRelationship
	 *            the new proof of relationship
	 */
	public void setProofOfRelationship(String proofOfRelationship) {
		this.proofOfRelationship = proofOfRelationship;
	}

	/** The proof of date of birth. */
	private String proofOfDateOfBirth;

	/**
	 * Gets the proof of date of birth.
	 *
	 * @return the proof of date of birth
	 */
	public String getProofOfDateOfBirth() {

		return proofOfDateOfBirth;
	}

	/**
	 * Sets the proof of date of birth.
	 *
	 * @param proofOfDateOfBirth
	 *            the new proof of date of birth
	 */
	public void setProofOfDateOfBirth(String proofOfDateOfBirth) {
		this.proofOfDateOfBirth = proofOfDateOfBirth;
	}

	/** The individual biometrics. */
	private String individualBiometrics;

	/**
	 * Gets the individual biometrics.
	 *
	 * @return the individual biometrics
	 */
	public String getIndividualBiometrics() {

		return individualBiometrics;
	}

	/**
	 * Sets the individual biometrics.
	 *
	 * @param individualBiometrics
	 *            the new individual biometrics
	 */
	public void setIndividualBiometrics(String individualBiometrics) {
		this.individualBiometrics = individualBiometrics;
	}

	/** The local administrative authority. */
	private String localAdministrativeAuthority;

	/**
	 * Gets the local administrative authority.
	 *
	 * @return the local administrative authority
	 */
	public String getLocalAdministrativeAuthority() {

		return localAdministrativeAuthority;
	}

	/**
	 * Sets the local administrative authority.
	 *
	 * @param localAdministrativeAuthority
	 *            the new local administrative authority
	 */
	public void setLocalAdministrativeAuthority(String localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	/** The id schema version. */
	private Double idSchemaVersion;

	/** The cnie number. */
	private Long cnieNumber;

}
