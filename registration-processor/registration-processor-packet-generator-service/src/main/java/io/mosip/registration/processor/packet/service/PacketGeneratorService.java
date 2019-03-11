package io.mosip.registration.processor.packet.service;

import io.mosip.registration.processor.packet.service.dto.PackerGeneratorResDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorDto;

/**
 * The Interface PacketGeneratorService.
 */
public interface PacketGeneratorService {

	/**
	 * Creates the packet.
	 *
	 * @param request
	 *            the request
	 * @return the packer generator res dto
	 */
	public PackerGeneratorResDto createPacket(PacketGeneratorDto request);

}
