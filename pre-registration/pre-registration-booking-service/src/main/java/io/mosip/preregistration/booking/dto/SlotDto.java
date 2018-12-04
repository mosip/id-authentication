package io.mosip.preregistration.booking.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotDto {

	private LocalTime fromTime;
	
	private LocalTime toTime;
	
	private int availability;
}
