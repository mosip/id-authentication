package io.mosip.admin.uinmgmt.service;

import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;

/**
 * This interface provides methods to do GET operations to UIN Detail.
 * 
 * @author Megha Tanga
 * 
 */
public interface UinDetailService {

	/**
	 * Method to get the Complete UIN details
	 * 
	 * @param uin
	 *            pass UIN as String
	 * @return UinResponseDto complete detail about the given uin
	 */
	public UinDetailResponseDto getUinDetails(String uin);

}
