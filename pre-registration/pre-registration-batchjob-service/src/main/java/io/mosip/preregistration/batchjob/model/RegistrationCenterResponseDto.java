package io.mosip.preregistration.batchjob.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterResponseDto {
	private List<RegistrationCenterDto> registrationCenters;
}
