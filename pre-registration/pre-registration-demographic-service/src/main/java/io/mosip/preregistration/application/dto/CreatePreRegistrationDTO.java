package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreatePreRegistrationDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The pre-registration-Id. */
	private String preRegistrationId;

	/** The created by. */
	private String createdBy;

	/** The create date time. */
	private Date createdDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private Date updatedDateTime;
	
	private String statusCode;
	
	private String langCode;
	/**
	 * 
	 */
	private JSONObject demographicDetails;

}
