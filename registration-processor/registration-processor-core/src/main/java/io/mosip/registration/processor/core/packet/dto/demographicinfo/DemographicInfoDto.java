package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
	
/**
 * Instantiates a new demographic info dto.
 */
@Data
public class DemographicInfoDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	private String regId;
	
	/** The uin. */
	private String uin;
	
	/** The lang code. */
	private String langCode;

	/** The name. */
	private String name;
	
	/** The dob. */
	private Date dob;
	
	/** The gender code. */
	private String genderCode;
	
	/** The phonetic name. */
	private String phoneticName;

}
