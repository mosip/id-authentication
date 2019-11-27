/**
 * 
 */
package io.mosip.registration.processor.request.handler.service;

import io.mosip.registration.processor.request.handler.service.dto.LostRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.LostResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;

/**
 * The Interface LostPacketService.
 *
 * @author M1022006
 */
public interface LostPacketService {

	/**
	 * Gets the id value.
	 *
	 * @param lostPacketRequestDto
	 *            the lost packet request dto
	 * @return the id value
	 */
	public LostResponseDto getIdValue(LostRequestDto lostRequestDto) throws RegBaseCheckedException;

}
