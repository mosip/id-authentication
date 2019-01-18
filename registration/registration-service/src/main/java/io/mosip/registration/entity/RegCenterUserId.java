package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

/**
 * Composite key for RegCenterUser entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
public class RegCenterUserId implements Serializable {

	private static final long serialVersionUID = -7306845601917592413L;

	@Column(name = "regcntr_id")
	private String regcntrId;
	@Column(name = "usr_id")
	private String usrId;

}
