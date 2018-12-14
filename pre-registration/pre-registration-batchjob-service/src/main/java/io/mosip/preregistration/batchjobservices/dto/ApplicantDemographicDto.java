package io.mosip.preregistration.batchjobservices.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Applicant Demographic Dto class
 * 
 * @author M1043008
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplicantDemographicDto {
	
	private String preRegistrationId;

	private String groupId;
	
	private byte[] applicantDetailJson;
	
	private String statusCode;

	private String langCode;

	private String createdBy;
	
	private String cr_appuser_id;
	
	private Timestamp createDateTime;

	private String updatedBy;

	private Timestamp updateDateTime;

	private boolean isDeleted;

	private Timestamp deletedDateTime;
	
	


}
