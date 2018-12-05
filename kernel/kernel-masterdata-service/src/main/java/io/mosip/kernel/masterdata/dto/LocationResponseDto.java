package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationResponseDto {

	private List<LocationDto> locations;
}
