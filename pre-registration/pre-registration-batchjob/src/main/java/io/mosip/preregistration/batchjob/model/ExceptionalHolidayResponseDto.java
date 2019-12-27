package io.mosip.preregistration.batchjob.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author Kishan Rathore
 *
 */
@Data
public class ExceptionalHolidayResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008692838467689608L;
	
	private List<ExceptionalHolidayDto> exceptionalHolidayList;

}
