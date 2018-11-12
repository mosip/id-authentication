package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite key for RegistrationCenter entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class RegistrationCenterId implements Serializable {

	private static final long serialVersionUID = 4371755177119053570L;

	@Column(name = "id", length = 28, nullable = false, updatable = false)
	private String centerId;

	@AttributeOverride(name = "isActive", column = @Column(name = "IS_ACTIVE", insertable = false, updatable = false))

	/**
	 * @return the centerId
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * @param centerId
	 *            the centerId to set
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
}
