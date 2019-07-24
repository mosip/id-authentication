package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegistrationCenterUserID implements Serializable{
	
	/**
	 * Generated SerialVersionUID
	 */
	private static final long serialVersionUID = -403460847776525788L;

	@Column(name = "regcntr_id", unique = true, nullable = false, length = 10)
	private String regCenterId;

	@Column(name = "usr_id", unique = true, nullable = false, length = 10)
	private String userId;

}
