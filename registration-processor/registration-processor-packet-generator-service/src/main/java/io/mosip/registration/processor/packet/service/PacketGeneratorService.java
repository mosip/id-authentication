package io.mosip.registration.processor.packet.service;

import io.mosip.registration.processor.packet.service.dto.PackerGeneratorResDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorDto;

public interface PacketGeneratorService {

	public PackerGeneratorResDto createPacket(PacketGeneratorDto request);

}
