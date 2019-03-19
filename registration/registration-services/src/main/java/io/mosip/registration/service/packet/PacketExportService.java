package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;

public interface PacketExportService {

	/**
	 * Get the Synched records from the table
	 * @return
	 */
	List<PacketStatusDTO> getSynchedRecords();

	/**
	 * Update the exported packet status
	 * @param exportedPackets
	 * @return
	 */
	ResponseDTO updateRegistrationStatus(List<PacketStatusDTO> exportedPackets);

}