package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;

/**
 * Service class for PacketExport
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface PacketExportService {

	/**
	 * Get the Synched records from the table
	 * @return The list of Synched records
	 */
	List<PacketStatusDTO> getSynchedRecords();

	/**
	 * Update the exported packet status
	 * @param exportedPackets The list of exported packets
	 * @return Return the response based on the success or failure response
	 */
	ResponseDTO updateRegistrationStatus(List<PacketStatusDTO> exportedPackets);

}