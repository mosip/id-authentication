package io.mosip.preregistration.batchjobservices.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Entity
@Table(name = "applicant_demographic")
@Getter
@Setter
@NoArgsConstructor
public class Applicant_demographic {
	
	/** The pre registration id. */
	@Column(name = "prereg_id", nullable = false)
	@Id
	private String preRegistrationId;

	/** The group id. */
	@Column(name = "group_id", nullable = false)
	private String groupId;
	
	/** The JSON */
	@Column(name = "demog_detail")
	private byte[] applicantDetailJson;
	

	/** The status_code */
	@Column(name = "status_code", nullable = false)
	private String statusCode;

	/** The lang_code */
	@Column(name = "lang_code", nullable = false)
	private String langCode;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;
	
	/** The created appuser by. */
	@Column(name = "cr_appuser_id")
	private String cr_appuser_id;
	
	/** The create date time. */
	@Column(name = "cr_dtimes")
	@CreationTimestamp
	private Timestamp createDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The update date time. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private Timestamp updateDateTime;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private Timestamp deletedDateTime;

}
