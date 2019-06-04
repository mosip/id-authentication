package io.mosip.registration.processor.core.spi.filesystem.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;

/**
 * File System Manager
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface FileSystemManager {

	/**
	 * This method checks whether a file exists in DFS.
	 *
	 * @param id
	 *            The ID for which file needs to be checked
	 * @param fileName
	 *            File that needs to checked
	 * @return True if file is found, false otherwise
	 */
	public boolean checkFileExistence(String id, String fileName)
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException;

	/**
	 * This method fetches the packet corresponding to an ID and returns it.
	 *
	 * @param id
	 *            The id
	 * @param referenceId
	 *            reference id id to fetch the public key
	 * @param timestamp
	 *            get the key which is used for encryption at the particular time
	 * @return The packet in specified format
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 * @throws io.mosip.kernel.core.exception.IOException
	 */
	public InputStream getPacket(String id) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException;

	/**
	 * This method fetches a file corresponding to an ID and returns it.
	 *
	 * @param id
	 *            The id
	 * @param fileName
	 *            Required file name
	 * @return the required file
	 */
	public InputStream getFile(String id, String fileName) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException;

	/**
	 * This method to unzip data ,which extract zip content to provide destination
	 * folder path
	 * 
	 * @param input
	 *            data to unzip
	 * @param desDir
	 *            directory where unzip files to be extracted
	 * @throws IOException
	 *             if an error occured while unzipping
	 */
	public void unpackPacket(InputStream input, String desDir) throws IOException;

	/**
	 * This method stores the packet corresponding to an ID
	 * 
	 * @param id
	 *            The id
	 * @param key
	 *            Physical path of the packet which needs to be stored
	 * @param document
	 *            document to be stored
	 * @return True If the packet is stored successfully
	 */
	public boolean storeFile(String id, String key, InputStream document);

	/**
	 * This method stores the packet corresponding to an ID.
	 *
	 * @param id
	 *            The id
	 * @param file
	 *            Packet which needs to be stored
	 * @return True If the packet is stored successfully
	 */
	public boolean storePacket(String id, InputStream file);

	/**
	 * This method stores the packet corresponding to an enrolment ID.
	 *
	 * @param id
	 *            The id
	 * @param filePath
	 *            Physical path of the packet which needs to be stored
	 * @return True If the packet is stored successfully
	 */
	public boolean storePacket(String id, File filePath);
	
	/**
	 * Checks if is packet present.
	 *
	 * @param id the id
	 * @return true, if is packet present
	 */
	public boolean isPacketPresent(String id);
}
