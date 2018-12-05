package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponseDto {
	private List<HolidayDto> holidays;
}
