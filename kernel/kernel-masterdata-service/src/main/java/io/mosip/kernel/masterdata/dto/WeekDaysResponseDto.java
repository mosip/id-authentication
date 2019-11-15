package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.masterdata.dto.getresponse.WeekDaysDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kishan Rathore
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekDaysResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8647424243157540833L;
	
	private List<WeekDaysDto> weekdays;

}
