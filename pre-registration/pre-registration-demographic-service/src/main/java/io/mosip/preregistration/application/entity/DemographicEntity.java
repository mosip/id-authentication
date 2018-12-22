package io.mosip.preregistration.application.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registration entity
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
@Component
@Entity
@Table(name = "applicant_demographic", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
@NamedQuery(name = "DemographicEntity.findByCreatedBy", query = "SELECT e FROM DemographicEntity e  WHERE e.createdBy=:userId")
@NamedQuery(name = "DemographicEntity.noOfGroupIds", query = "SELECT DISTINCT groupId  FROM DemographicEntity where crAppuserId=:userId")
@NamedQuery(name = "DemographicEntity.findBypreRegistrationId", query = "SELECT r FROM DemographicEntity r  WHERE r.preRegistrationId=:preRegId")
public class DemographicEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

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
	private String crAppuserId;

	/** The create date time. */
	@Column(name = "cr_dtimes")
	private Timestamp createDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The update date time. */
	@Column(name = "upd_dtimes")
	private Timestamp updateDateTime;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;

}
