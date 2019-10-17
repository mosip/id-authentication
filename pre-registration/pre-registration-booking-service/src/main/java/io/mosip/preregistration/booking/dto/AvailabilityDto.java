package io.mosip.preregistration.booking.dto;

import java.util.List;

import io.mosip.preregistration.booking.serviceimpl.dto.DateTimeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AvailabilityDto {
	
	private String regCenterId;
	
	private List<DateTimeDto> centerDetails;
	

}
