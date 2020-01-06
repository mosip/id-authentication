package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.RegCenterUser;
import lombok.Data;

/**
 * Composite key for for {@link RegCenterUser}
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
