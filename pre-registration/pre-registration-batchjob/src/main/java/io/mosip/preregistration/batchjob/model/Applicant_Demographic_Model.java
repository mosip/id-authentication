package io.mosip.preregistration.batchjob.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Applicant_Demographic_Model {
	
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
