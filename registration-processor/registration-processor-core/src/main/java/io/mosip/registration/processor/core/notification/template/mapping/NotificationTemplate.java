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

	private Integer age;

	private JsonValue[] gender;

	public JsonValue[] getGender() {
		return Arrays.copyOf(gender, gender.length);
	}

	public void setGender(JsonValue[] gender) {
		this.gender = gender != null ? gender : null;
	}

	private JsonValue[] addressLine1;

	public JsonValue[] getAddressLine1() {
		if(addressLine1 != null) 
			return Arrays.copyOf(addressLine1, addressLine1.length);
		return null;
	}

	public void setAddressLine1(JsonValue[] addressLine1) {
		this.addressLine1 = addressLine1 != null ? addressLine1 : null;
	}

	private JsonValue[] addressLine2;

	public JsonValue[] getAddressLine2() {
		if(addressLine2 != null) 
			return Arrays.copyOf(addressLine2, addressLine2.length);
		return null;
	}

	public void setAddressLine2(JsonValue[] addressLine2) {
		this.addressLine2 = addressLine2 != null ? addressLine2 : null;
	}

	private JsonValue[] addressLine3;

	public JsonValue[] getAddressLine3() {
		if(addressLine3 != null) 
			return Arrays.copyOf(addressLine3, addressLine3.length);
		return null;
	}

	public void setAddressLine3(JsonValue[] addressLine3) {
		this.addressLine3 = addressLine3 != null ? addressLine3 : null;
	}

	private JsonValue[] region;

	public JsonValue[] getRegion() {
		if(region != null) 
			return Arrays.copyOf(region, region.length);
		return null;
	}

	public void setRegion(JsonValue[] region) {
		this.region = region != null ? region : null;
	}

	private JsonValue[] province;

	public JsonValue[] getProvince() {
		if(province != null) 
			return Arrays.copyOf(province, province.length);
		return null;
	}

	public void setProvince(JsonValue[] province) {
		this.province = province != null ? province : null;
	}

	private JsonValue[] city;

	public JsonValue[] getCity() {
		if(city != null) 
			return Arrays.copyOf(city, city.length);
		return null;
	}

	public void setCity(JsonValue[] city) {
		this.city = city != null ? city : null;
	}

	private String postalCode;

	private JsonValue[] parentOrGuardianName;

	public JsonValue[] getParentOrGuardianName() {
		if(parentOrGuardianName != null) 
			return Arrays.copyOf(parentOrGuardianName, parentOrGuardianName.length);
		return null;
	}

	public void setParentOrGuardianName(JsonValue[] parentOrGuardianName) {
		this.parentOrGuardianName = parentOrGuardianName != null ? parentOrGuardianName : null;
	}

	private String parentOrGuardianRIDOrUIN;

	private JsonValue[] proofOfAddress;

	public JsonValue[] getProofOfAddress() {
		if(proofOfAddress != null) 
			return Arrays.copyOf(proofOfAddress, proofOfAddress.length);
		return null;
	}

	public void setProofOfAddress(JsonValue[] proofOfAddress) {
		this.proofOfAddress = proofOfAddress != null ? proofOfAddress : null;
	}

	private JsonValue[] proofOfIdentity;

	public JsonValue[] getProofOfIdentity() {
		if(proofOfIdentity != null) 
			return Arrays.copyOf(proofOfIdentity, proofOfIdentity.length);
		return null;
	}

	public void setProofOfIdentity(JsonValue[] proofOfIdentity) {
		this.proofOfIdentity = proofOfIdentity != null ? proofOfIdentity : null;
	}

	private JsonValue[] proofOfRelationship;

	public JsonValue[] getProofOfRelationship() {
		if(proofOfRelationship != null) 
			return Arrays.copyOf(proofOfRelationship, proofOfRelationship.length);
		return null;
	}

	public void setProofOfRelationship(JsonValue[] proofOfRelationship) {
		this.proofOfRelationship = proofOfRelationship != null ? proofOfRelationship : null;
	}

	private JsonValue[] proofOfDateOfBirth;

	public JsonValue[] getProofOfDateOfBirth() {
		if(proofOfDateOfBirth != null) 
			return Arrays.copyOf(proofOfDateOfBirth, proofOfDateOfBirth.length);
		return null;
	}

	public void setProofOfDateOfBirth(JsonValue[] proofOfDateOfBirth) {
		this.proofOfDateOfBirth = proofOfDateOfBirth != null ? proofOfDateOfBirth : null;
	}

	private JsonValue[] individualBiometrics;

	public JsonValue[] getIndividualBiometrics() {
		if(individualBiometrics != null) 
			return Arrays.copyOf(individualBiometrics, individualBiometrics.length);
		return null;
	}

	public void setIndividualBiometrics(JsonValue[] individualBiometrics) {
		this.individualBiometrics = individualBiometrics != null ? individualBiometrics : null;
	}

	private JsonValue[] parentOrGuardianBiometrics;

	public JsonValue[] getParentOrGuardianBiometrics() {
		if(parentOrGuardianBiometrics != null) 
			return Arrays.copyOf(parentOrGuardianBiometrics, parentOrGuardianBiometrics.length);
		return null;
	}

	public void setParentOrGuardianBiometrics(JsonValue[] parentOrGuardianBiometrics) {
		this.parentOrGuardianBiometrics = parentOrGuardianBiometrics != null ? parentOrGuardianBiometrics : null;
	}

	private JsonValue[] localAdministrativeAuthority;

	public JsonValue[] getLocalAdministrativeAuthority() {
		if(localAdministrativeAuthority != null) 
			return Arrays.copyOf(localAdministrativeAuthority, localAdministrativeAuthority.length);
		return null;
	}

	public void setLocalAdministrativeAuthority(JsonValue[] localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority != null ? localAdministrativeAuthority : null;
	}

	private Double idSchemaVersion;
	private Integer cnieNumber;

}
