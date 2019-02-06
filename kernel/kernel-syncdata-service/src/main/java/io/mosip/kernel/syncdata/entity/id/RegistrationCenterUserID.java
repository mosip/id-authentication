package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RegistrationCenterUserID implements Serializable {

	private static final long serialVersionUID = 123442772911982310L;

	@Column(name = "regcntr_id", nullable = false, length = 10)
	private String regCenterId;
	
	@Column(name = "usr_id", nullable = false, length = 36)
	private String userId;
	
}
