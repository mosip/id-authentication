package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegExceptionalHolidayID implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 499662213516904291L;

	@Column(name="regcntr_id",nullable=false,length=10)
	private String registrationCenterId;
	
	@Column(name="hol_date",nullable=false)
	private LocalDate exceptionHolidayDate;
}
