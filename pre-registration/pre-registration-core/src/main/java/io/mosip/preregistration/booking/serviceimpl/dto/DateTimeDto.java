package io.mosip.preregistration.booking.serviceimpl.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DateTimeDto {

	private String date;

	private boolean isHoliday;

	private List<SlotDto> timeSlots;
}
