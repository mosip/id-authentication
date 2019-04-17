package io.mosip.preregistration.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class defines the variables to accept the input parameter from
 * request.
 *
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DemographicRequestDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The pre-registration-Id. */
	private String preRegistrationId;

	/** The created by. */
	private String createdBy;

	/** The create date time. */
	private String createdDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private String updatedDateTime;

	/**
	 * language code
	 */
	private String statusCode;

	/**
	 * Demographic Json details
	 */
	private JSONObject demographicDetails;
	
	/**
	 * status code
	 */
	private String langCode;
}
