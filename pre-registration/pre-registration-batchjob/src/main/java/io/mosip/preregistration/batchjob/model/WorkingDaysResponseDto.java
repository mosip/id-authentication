package io.mosip.preregistration.batchjob.model;

import java.io.Serializable;
import java.util.List;

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
