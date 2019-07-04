package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;

/**
 * Service class for Exporting the Registration Packets
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
public interface PacketExportService {

	/**
	 * Gets the Synched and exported {@link Registration} records from the table.
	 * 
	 * <p>
	 * The client status of the {@link Registration} record has to be either
	 * EXPORTED OR SYNCED
	 * </p>
	 * 
	 * @return The list of Synched and exported records
	 */
	List<PacketStatusDTO> getSynchedRecords();

	/**
	 * Updates the exported packet status in the {@link Registration} table
	 * 
	 * <p>
	 * The following columns in the {@link Registration} table will be updated based
	 * on the packet uploaded to server.
	 * </p>
	 * <ul>
	 * <li>Client Status Code</li>
	 * <li>File Upload Status</li>
	 * <li>Client Status Comment</li>
	 * <li>Upload Count</li>
	 * <li>Server Status Code</li>
	 * </ul>
	 * 
	 * <p>
	 * Returns the status of the virus scan as {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If virus scan has completed successfully, {@link SuccessResponseDTO} will be
	 * set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @param exportedPackets
	 *            The list of exported packets
	 * @return Return the response based on the success or failure response
	 */
	ResponseDTO updateRegistrationStatus(List<PacketStatusDTO> exportedPackets);

}