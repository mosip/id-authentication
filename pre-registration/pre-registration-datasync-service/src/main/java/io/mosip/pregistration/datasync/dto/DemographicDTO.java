package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1046129 - Jagadishwari
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DemographicDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 198815710659551253L;

	/** The pre registration id. */
	private String preRegistrationId;

	/** The group id. */
	private String groupId;
	
	/** The JSON */
	private byte[] applicantDetailJson;
	
	/** The status_code */
	private String statusCode;

	/** The lang_code */
	private String langCode;

	/** The created by. */
	private String createdBy;
	
	/** The created appuser by. */
	private String cr_appuser_id;
	
	/** The create date time. */
	private Timestamp createDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private Timestamp updateDateTime;

	/** The is deleted. */
	private Boolean isDeleted;

	/** The deleted date time. */
	private Timestamp deletedDateTime;
	
		
	
}
