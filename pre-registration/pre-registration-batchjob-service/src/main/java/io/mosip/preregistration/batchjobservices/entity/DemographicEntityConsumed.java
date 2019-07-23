/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.core.common.entity.DocumentEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity class defines the database table details for PreRegistration.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
@Entity
@Table(name = "applicant_demographic_consumed", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
public class DemographicEntityConsumed implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL, mappedBy = "demographicEntity")
	private List<DocumentEntity> documentEntity;

	@OneToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL, mappedBy = "demographicEntity")
	private RegistrationBookingEntity registrationBookingEntity;

	/** The pre registration id. */
	@Column(name = "prereg_id", nullable = false)
	@Id
	private String preRegistrationId;

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
	private LocalDateTime createDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The update date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updateDateTime;

	/**
	 * Encrypted Date Time
	 */
	@Column(name = "encrypted_dtimes")
	private LocalDateTime encryptedDateTime;

	@Column(name = "demog_detail_hash")
	private String demogDetailHash;
}
