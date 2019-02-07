package io.mosip.registration.processor.core.notification.template.mapping;

import java.util.Arrays;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import lombok.Data;

@Data
public class NotificationTemplate {

	private JsonValue[] firstName;

	public JsonValue[] getFirstName() {
		return Arrays.copyOf(firstName, firstName.length);
	}

	public void setFirstName(JsonValue[] firstName) {
		this.firstName = firstName != null ? firstName : null;
	}

	private String phoneNumber;

	private String emailID;

	private String dateOfBirth;

	private Long age;

	private JsonValue[] gender;

	public JsonValue[] getGender() {
		return Arrays.copyOf(gender, gender.length);
	}

	public void setGender(JsonValue[] gender) {
		this.gender = gender != null ? gender : null;
	}

	private JsonValue[] addressLine1;

	public JsonValue[] getAddressLine1() {
		if (addressLine1 != null)
			return Arrays.copyOf(addressLine1, addressLine1.length);
		return null;
	}

	public void setAddressLine1(JsonValue[] addressLine1) {
		this.addressLine1 = addressLine1 != null ? addressLine1 : null;
	}

	private JsonValue[] addressLine2;

	public JsonValue[] getAddressLine2() {
		if (addressLine2 != null)
			return Arrays.copyOf(addressLine2, addressLine2.length);
		return null;
	}

	public void setAddressLine2(JsonValue[] addressLine2) {
		this.addressLine2 = addressLine2 != null ? addressLine2 : null;
	}

	private JsonValue[] addressLine3;

	public JsonValue[] getAddressLine3() {
		if (addressLine3 != null)
			return Arrays.copyOf(addressLine3, addressLine3.length);
		return null;
	}

	public void setAddressLine3(JsonValue[] addressLine3) {
		this.addressLine3 = addressLine3 != null ? addressLine3 : null;
	}

	private JsonValue[] region;

	public JsonValue[] getRegion() {
		if (region != null)
			return Arrays.copyOf(region, region.length);
		return null;
	}

	public void setRegion(JsonValue[] region) {
		this.region = region != null ? region : null;
	}

	private JsonValue[] province;

	public JsonValue[] getProvince() {
		if (province != null)
			return Arrays.copyOf(province, province.length);
		return null;
	}

	public void setProvince(JsonValue[] province) {
		this.province = province != null ? province : null;
	}

	private JsonValue[] city;

	public JsonValue[] getCity() {
		if (city != null)
			return Arrays.copyOf(city, city.length);
		return null;
	}

	public void setCity(JsonValue[] city) {
		this.city = city != null ? city : null;
	}

	private String postalCode;

	private String parentOrGuardianRIDOrUIN;

	private String proofOfAddress;

	public String getProofOfAddress() {

		return proofOfAddress;
	}

	public void setProofOfAddress(String proofOfAddress) {
		this.proofOfAddress = proofOfAddress;
	}

	private String proofOfIdentity;

	public String getProofOfIdentity() {

		return proofOfIdentity;
	}

	public void setProofOfIdentity(String proofOfIdentity) {
		this.proofOfIdentity = proofOfIdentity;
	}

	private String proofOfRelationship;

	public String getProofOfRelationship() {
		return proofOfRelationship;
	}

	public void setProofOfRelationship(String proofOfRelationship) {
		this.proofOfRelationship = proofOfRelationship;
	}

	private String proofOfDateOfBirth;

	public String getProofOfDateOfBirth() {

		return proofOfDateOfBirth;
	}

	public void setProofOfDateOfBirth(String proofOfDateOfBirth) {
		this.proofOfDateOfBirth = proofOfDateOfBirth;
	}

	private String individualBiometrics;

	public String getIndividualBiometrics() {

		return individualBiometrics;
	}

	public void setIndividualBiometrics(String individualBiometrics) {
		this.individualBiometrics = individualBiometrics;
	}

	private String localAdministrativeAuthority;

	public String getLocalAdministrativeAuthority() {

		return localAdministrativeAuthority;
	}

	public void setLocalAdministrativeAuthority(String localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	private Double idSchemaVersion;

	private Long cnieNumber;

}
