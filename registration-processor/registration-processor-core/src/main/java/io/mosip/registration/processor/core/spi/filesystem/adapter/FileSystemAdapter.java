package io.mosip.registration.processor.core.spi.filesystem.adapter;
	
import java.io.File;
import java.io.IOException;

/**
 * MOSIP ADAPTER INTERFACE FOR CONNECTING TO DFS.
 *
 * @author Pranav Kumar
 * @param <T>            Acceptable and Return type of individual packets, files
 * @param <V>            Return type after a successful operation
 * @since 0.0.1
 */
public interface FileSystemAdapter<T, V> {

	/**
	 * This method stores the packet corresponding to an enrolment ID.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @param filePath            Physical path of the packet which needs to be stored
	 * @return True If the packet is stored successfully
	 */
	public V storePacket(String enrolmentId, File filePath);

	/**
	 * This method stores the packet corresponding to an enrolment ID.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @param file            Packet which needs to be stored
	 * @return True If the packet is stored successfully
	 */
	public V storePacket(String enrolmentId, T file);

	/**
	 * This method fetches the packet corresponding to an enrolment ID and returns
	 * it.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @return The packet in specified format
	 */
	public T getPacket(String enrolmentId);

	/**
	 * This method checks whether a file exists in DFS.
	 *
	 * @param enrolmentId            The enrolment ID for which file needs to be checked
	 * @param fileName            File that needs to checked
	 * @return True if file is found, false otherwise
	 */
	public Boolean checkFileExistence(String enrolmentId, String fileName);

	/**
	 * This method fetches a file corresponding to an enrolment ID and returns it.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @param fileName            Required file name
	 * @return the required file
	 */
	public T getFile(String enrolmentId, String fileName);

	/**
	 * This method unzips the packet corresponding to an enrolment ID and uploads
	 * individual files of that packet.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void unpackPacket(String enrolmentId) throws IOException;

	/**
	 * This method deletes the packet corresponding to an enrolment ID.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @return True if the packet is deleted successfully
	 */
	public V deletePacket(String enrolmentId);

	/**
	 * This method deletes a particular file related to an enrolment ID.
	 *
	 * @param enrolmentId            The enrolmentId
	 * @param fileName            The file which needs to be deleted
	 * @return True if the file is successfully deleted
	 */
	public V deleteFile(String enrolmentId, String fileName);

	
	/**
	 * Checks if is packet present.
	 *
	 * @param registrationId the registration id
	 * @return the boolean
	 */
	public Boolean isPacketPresent(String registrationId);

}
