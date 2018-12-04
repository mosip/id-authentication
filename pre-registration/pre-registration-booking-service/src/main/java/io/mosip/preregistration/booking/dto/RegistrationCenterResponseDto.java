package io.mosip.preregistration.booking.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterResponseDto {
	private List<RegistrationCenterDto> registrationCenters;
}
