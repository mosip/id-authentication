package io.mosip.registration.processor.packet.storage.entity;
	
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;


/**
 * The Class ApplicantDemographicInfoJsonPKEntity.
 */
@Embeddable

/**
 * Instantiates a new applicant demographic info json PK entity.
 */
@Data
public class ApplicantDemographicInfoJsonPKEntity implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

}
