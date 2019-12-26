package io.mosip.kernel.masterdata.dto.getresponse;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.masterdata.dto.ExceptionalHolidayDto;
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
