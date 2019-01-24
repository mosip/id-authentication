package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterUserID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4584637997735050514L;

	@Column(name="regcntr_id",nullable=false)
	private String regCenterId;
	
	@Column(name="usr_id",nullable=false)
	private String userId;
}
