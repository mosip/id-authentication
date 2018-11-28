package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class DemographicDedupeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String regId;
	private String preRegId;
	private String langCode;
	
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private Date dob;
	private String genderCode;
	private String addrLine1;
	private String addrLine2;
	private String addrLine3;
	private String addrLine4;
	private String addrLine5;
	private String addrLine6;
	private String zipCode;

}
