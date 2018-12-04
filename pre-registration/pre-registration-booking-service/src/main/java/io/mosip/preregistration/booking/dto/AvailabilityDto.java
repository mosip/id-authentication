package io.mosip.preregistration.booking.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDto {
	
	private String regCenterId;
	
	private List<DateTimeDto> centerDetails;
	

}
