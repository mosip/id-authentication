package io.mosip.registration.dto.mastersync;

import java.util.List;

public class LocationResponseDto {

	private List<LocationDto> locations;

	/**
	 * @return the locations
	 */
	public List<LocationDto> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<LocationDto> locations) {
		this.locations = locations;
	}

}
