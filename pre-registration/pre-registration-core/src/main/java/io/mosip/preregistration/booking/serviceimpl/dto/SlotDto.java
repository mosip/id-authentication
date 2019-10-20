package io.mosip.preregistration.booking.serviceimpl.dto;

import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SlotDto {

	private LocalTime fromTime;
	
	private LocalTime toTime;
	
	private int availability;
}
