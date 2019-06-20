package io.mosip.registration.service.packet;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Service interface for Packet Upload to the server
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
public interface PacketUploadService {

	/**
	 * Gets the list of {@link Registration} data with the following statues:
	 * <ol>
	 * <li>SYNCED</li>
	 * <li>EXPORTED</li>
	 * <li>RESEND</li>
	 * <li>E</li>
	 * </ol>
	 * 
	 * @return the list of {@link Registration} entries with the above statuses
	 */
	List<Registration> getSynchedPackets();

	/**
	 * Push the {@link Registration} packet to the server using Packet Receiver
	 * service. The packet has to be syched before pushing to server.
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If the packet had been pushed successfully, {@link SuccessResponseDTO} will
	 * be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 *
	 * @param packet
	 *            the packet to be pushed to server
	 * @return the status of the packet push
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	ResponseDTO pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException;

	/**
	 * Updates the client status of the uploaded packet to PUSHED in
	 * {@link Registration} table
	 *
	 * @param packetUploadStatus
	 *            the list of packets which are uploaded
	 * @return <code>true</code> if status of all uploaded packets are updated
	 */
	 Boolean updateStatus(List<PacketStatusDTO> packetUploadStatus);
	 
	/**
	 * Uploads the required registration packet to the server after creation of
	 * registration packet.
	 * 
	 * <p>
	 * The registration packet will be sync with the server and then packet will be
	 * uploaded
	 * </p>
	 * <p>
	 * The client and server statuses will be updated after packet is uploaded
	 * </p>
	 * <p>
	 * The above process will be done only when EOD process is turned OFF
	 * </p>
	 *
	 * @param rid
	 *            the registration id of the Registration Packet to be uploaded to
	 *            the server
	 */
 	void uploadPacket(String rid);

	/**
	 * Uploads the registration packets after approval/rejection during EOD process
	 * when EOD process is turned ON
	 * 
	 * <p>
	 * The registration packet will be sync with the server and then packet will be
	 * uploaded
	 * </p>
	 * <p>
	 * The client and server statuses will be updated after packet is uploaded
	 * </p>
	 * <p>
	 * The above process will be done only when EOD process is turned ON
	 * </p>
	 *
	 * @param regIds
	 *            the RIDs' of the registration packets to be uploaded to the server
	 */
	void uploadEODPackets(List<String> regIds);

	/**
	 * Uploads all the registration packets which are already sync with the server
	 * during Machine Re-Mapping process
	 * 
	 * <p>
	 * The client and server statuses will be updated after packet is uploaded
	 * </p>
	 */
	void uploadAllSyncedPackets();
}
