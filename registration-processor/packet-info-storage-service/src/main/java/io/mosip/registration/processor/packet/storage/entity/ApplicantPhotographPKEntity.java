package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the applicant_photograph database table.
 * 
 * @author Horteppa M1048399
 */
@Embeddable
public class ApplicantPhotographPKEntity implements Serializable {

	/** The Constant serialVersionUID. */
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

	/**
	 * Instantiates a new applicant photograph PK entity.
	 */
	public ApplicantPhotographPKEntity() {
		super();
	}

	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return this.regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

}