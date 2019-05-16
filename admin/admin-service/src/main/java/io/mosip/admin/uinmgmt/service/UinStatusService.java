package io.mosip.admin.uinmgmt.service;

import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.admin.uinmgmt.dto.UinResponseWrapperDto;
/**
 * This interface provides methods to do GET operations to UIN Status.
 * 
 * @author Megha Tanga
 * 
 */
public interface UinStatusService {
	/**
	 * This method to konw the Status of the given UIN
	 * 
	 * @param uin
	 *         give UIN as input String
	 * 
	 * @return UinResponseWrapperDto
	 *         return the status of the given UIN
	 *
	 */
	public UinResponseWrapperDto getUinStatus(String uin);
	/**
	 * This method to get the UIn Details
	 * 
	 * @param uin
	 *         give UIN as input String
	 * 
	 * @return UinResponseWrapperDto
	 *         return the status of the given UIN
	 *
	 */
	public UinDetailResponseDto getUinDetails(String uin, String langCode);

}
