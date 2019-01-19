package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.Data;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 23-10-2016
 */
@Data


public class RegistrationCenterHolidayDto {
	private RegistrationCenterDto registrationCenter;
	private List<HolidayDto> holidays;
}
