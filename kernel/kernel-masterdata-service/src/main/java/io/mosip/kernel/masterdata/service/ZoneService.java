package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.getresponse.extn.ZoneExtnDto;

/**
 * Zone service
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface ZoneService {

	/**
	 * Method to fetch the user's zone hierarchy
	 * 
	 * @param langCode
	 *            input language code
	 * 
	 * @return {@link List} of {@link ZoneExtnDto}
	 */
	public List<ZoneExtnDto> getUserZoneHierarchy(String langCode);

	/**
	 * Method to fetch the user leaf zone in hierarchy
	 * 
	 * @param langCode
	 *            input language code
	 * @return {@link List} of {@link ZoneExtnDto}
	 */
	public List<ZoneExtnDto> getUserLeafZone(String langCode);
}
