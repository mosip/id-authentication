package io.mosip.registrationProcessor.perf.regPacket.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Identity {

	private List<FieldData> fullName;
	private String dateOfBirth;
	private Integer age;
	private List<FieldData> gender;
	private List<FieldData> residenceStatus;
	private List<FieldData> addressLine1;
	private List<FieldData> addressLine2;
	private List<FieldData> addressLine3;
	private List<FieldData> region;
	private List<FieldData> province;
	private List<FieldData> city;
	private String postalCode;
	private String phone;
	private String email;
	private List<FieldData> localAdministrativeAuthority;
	private DocumentData proofOfAddress;
	private DocumentData proofOfIdentity;
	private DocumentData proofOfRelationship;
	private BiometricData individualBiometrics;
	private Float IDSchemaVersion;
	private String CNIENumber;

}
