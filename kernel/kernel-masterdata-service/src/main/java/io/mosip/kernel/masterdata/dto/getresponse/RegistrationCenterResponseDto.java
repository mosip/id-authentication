package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 23-10-2016
 */
@Data


public class RegistrationCenterResponseDto {
	private List<RegistrationCenterDto> registrationCenters;
}
