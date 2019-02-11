package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

/**
 * Composite key for RegCenterUser entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class RegCenterUserId implements Serializable {

	private static final long serialVersionUID = -7306845601917592413L;

	@Column(name = "regcntr_id")
	private String regcntrId;
	@Column(name = "usr_id")
	private String usrId;

}
