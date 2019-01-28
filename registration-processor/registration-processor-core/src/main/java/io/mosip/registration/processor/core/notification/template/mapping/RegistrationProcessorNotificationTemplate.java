package io.mosip.registration.processor.core.notification.template.mapping;

import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
public class RegistrationProcessorNotificationTemplate {
	
	private String firstName;
	private String phoneNumber;
	private String emailID;
    private String dateOfBirth;
	private int age;
	private String gender;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String region;
	private String province;
	private String city;
	private String postalCode;
	private String parentOrGuardianName;
	private String parentOrGuardianRIDOrUIN;
	private String proofOfAddress;
	private String proofOfIdentity;
	private String proofOfRelationship;
	private String proofOfDateOfBirth;
	private String individualBiometrics;
	private String parentOrGuardianBiometrics;
	private String localAdministrativeAuthority;
	private double idSchemaVersion;
	private int cnieNumber;
	
	
}
