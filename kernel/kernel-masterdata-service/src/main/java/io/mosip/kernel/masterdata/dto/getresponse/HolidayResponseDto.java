package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import lombok.Data;

/**
 * @author Sidhant Agarwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
public class HolidayResponseDto {
	private List<HolidayDto> holidays;
}
