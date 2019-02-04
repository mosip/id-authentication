package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import java.io.Serializable;
import java.util.Arrays;
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
	
	public Date getDob() {
		return this.dob!=null?new Date(this.dob.getTime()):null;
	}

	public void setDob(Date dob) {
		this.dob = dob!=null?new Date(dob.getTime()):null;
	}

	/** The gender code. */
	private String genderCode;
	

}
