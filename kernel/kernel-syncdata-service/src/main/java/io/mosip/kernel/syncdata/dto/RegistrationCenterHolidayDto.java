package io.mosip.kernel.syncdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterHolidayDto extends BaseDto{
	private RegistrationCenterDto registrationCenter;
	private List<HolidayDto> holidays;
}
