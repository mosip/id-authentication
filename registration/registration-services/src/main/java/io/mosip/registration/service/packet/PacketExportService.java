package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;

public interface PacketExportService {

	/**
	 * Get the Synched records from the table
	 * @return
	 */
	List<Registration> getSynchedRecords();

	/**
	 * Update the exported packet status
	 * @param exportedPackets
	 * @return
	 */
	ResponseDTO updateRegistrationStatus(List<Registration> exportedPackets);

}