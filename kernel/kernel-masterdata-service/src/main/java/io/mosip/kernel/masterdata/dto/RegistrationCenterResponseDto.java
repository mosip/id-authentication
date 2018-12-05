package io.mosip.kernel.synchandler.dto;

import java.util.List;

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
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterResponseDto {
	private List<RegistrationCenterDto> registrationCenters;
}
