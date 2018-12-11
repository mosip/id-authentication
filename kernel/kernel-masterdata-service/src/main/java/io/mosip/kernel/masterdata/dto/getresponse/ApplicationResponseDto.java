package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import lombok.Data;

@Data


public class ApplicationResponseDto {

	private List<ApplicationDto> applicationtypes;
	
}
