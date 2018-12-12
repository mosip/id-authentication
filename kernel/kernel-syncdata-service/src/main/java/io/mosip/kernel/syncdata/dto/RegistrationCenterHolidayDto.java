package io.mosip.kernel.syncdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterHolidayDto {
	private RegistrationCenterDto registrationCenter;
	private List<HolidayDto> holidays;
}
