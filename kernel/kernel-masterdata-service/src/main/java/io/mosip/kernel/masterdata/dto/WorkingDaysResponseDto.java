package io.mosip.kernel.masterdata.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.masterdata.dto.getresponse.WorkingDaysDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDaysResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5966823885108013755L;
	
	private List<WorkingDaysDto> workingdays;
}
