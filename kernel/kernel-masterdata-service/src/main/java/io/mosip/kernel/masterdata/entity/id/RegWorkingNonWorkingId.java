package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegWorkingNonWorkingId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322512960633960954L;
	
	@Column(name="regcntr_id",nullable=false,length=10)
	private String registrationCenterId;
	
	@Column(name="day_code",nullable=false,length=3)
	private String dayCode;

}
