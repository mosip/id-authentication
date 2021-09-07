package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * The class authentication ananymous profile event.
 * 
 * @author Loganathan Sekar
 */

@Data
public class AnanymousAuthenticationProfile {
	
	/** The id. */
	private String partnerName;
	
	/** The timestamp. */
	private LocalDate date;
	
	/** The transaction id. */
	private String yearOfBirth;
	
	private String gender;
	
	private List<String> location;
	
	private List<String> preferredLanguages;
	
	private List<String> authFactors;
	
	private List<BiometricProfileInfo> biometricInfo;
	
	private String status;
	
	private List<String> errorCode;

	

}
