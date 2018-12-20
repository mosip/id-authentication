package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import lombok.Data;

@Data
public class Identity {
	
	private DemographicDetails[] firstName;
	
	private DemographicDetails[] lastName;
	
	private DemographicDetails[] dateOfBirth;
	
	private DemographicDetails[] gender;
	
	private DemographicDetails[] addressLine1;
	
	private DemographicDetails[] addressLine2;
	
	private DemographicDetails[] addressLine3;
	
	private DemographicDetails[] region;
	
	private DemographicDetails[] city;
	
	private DemographicDetails[] province;
	
	private DemographicDetails[] postalCode;
	
	private DemographicDetails[] emailId;
	
	private DemographicDetails[] mobileNumber;
	
	private DemographicDetails[] languageCode;
	
	private DemographicDetails[] isChild;
	
	private DemographicDetails[] age;
	
	private DemographicDetails[] localAdministrativeAuthority;
	
	private DemographicDetails[] cneOrPINNumber;
	
	private DemographicDetails[] parentOrGuardianName;
	
	private DemographicDetails[] parentOrGuardianRIDOrUIN;

}
