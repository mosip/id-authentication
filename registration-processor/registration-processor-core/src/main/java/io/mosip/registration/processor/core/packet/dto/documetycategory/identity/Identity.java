package io.mosip.registration.processor.core.packet.dto.documetycategory.identity;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class Identity {

	public DocumentCategoryValues fullName;

	private DocumentCategoryValues gender;

	private DocumentCategoryValues dob;

	private DocumentCategoryValues parentOrGuardianRID;

	private DocumentCategoryValues parentOrGuardianUIN;

	private DocumentCategoryValues proofOfAddress;

	private DocumentCategoryValues proofOfIdentity;

	private DocumentCategoryValues proofOfRelationship;

	private DocumentCategoryValues proofOfDateOfBirth;

	private DocumentCategoryValues individualBiometrics;

	private DocumentCategoryValues age;

	private DocumentCategoryValues addressLine1;

	private DocumentCategoryValues addressLine2;

	private DocumentCategoryValues addressLine3;

	private DocumentCategoryValues region;

	private DocumentCategoryValues province;

	private DocumentCategoryValues postalCode;

	private DocumentCategoryValues phone;

	private DocumentCategoryValues email;

	private DocumentCategoryValues localAdministrativeAuthority;

	private DocumentCategoryValues IDSchemaVersion;

	private DocumentCategoryValues CNIENumber;

	private DocumentCategoryValues city;

}
