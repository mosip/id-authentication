package io.mosip.registration.processor.request.handler.service;

import java.io.IOException;

import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;

/**
 * The Interface PacketGeneratorService.
 * 
 * @author Sowmya
 */
public interface PacketGeneratorService {

	/**
	 * Creates the packet.
	 *
	 * @param request
	 *            the request
	 * @return the packer generator res dto
	 */
	public PacketGeneratorResDto createPacket(PacketGeneratorDto request) throws RegBaseCheckedException,IOException;

}
