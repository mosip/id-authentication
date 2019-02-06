package io.mosip.kernel.core.fsadapter.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * File adapter interface for connecting to DFS
 *
 * @author Pranav Kumar
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
public interface FileSystemAdapter {

	/**
	 * This method checks whether a file exists in DFS.
	 *
	 * @param id
	 *            The ID for which file needs to be checked
	 * @param fileName
	 *            File that needs to checked
	 * @return True if file is found, false otherwise
	 */
	public boolean checkFileExistence(String id, String fileName);

	/**
	 * This method copy document from one folder to another
	 * 
	 * @param sourceFolderName
	 *            The source folder
	 * @param sourceFileName
	 *            The source file
	 * @param destinationFolderName
	 *            The destination folder
	 * @param destinationFileName
	 *            The destination file
	 * @return True if document copy is successful
	 */
	public boolean copyFile(String sourceFolderName, String sourceFileName, String destinationFolderName,
			String destinationFileName);

	/**
	 * This method deletes a particular file related to an ID.
	 *
	 * @param id
	 *            The id
	 * @param fileName
	 *            The file which needs to be deleted
	 * @return True if the file is successfully deleted
	 */
	public boolean deleteFile(String id, String fileName);

	/**
	 * This method deletes the packet corresponding to an ID.
	 *
	 * @param id
	 *            The id
	 * @return True if the packet is deleted successfully
	 */
	public boolean deletePacket(String id);

	/**
	 * This method fetches a file corresponding to an ID and returns it.
	 *
	 * @param id
	 *            The id
	 * @param fileName
	 *            Required file name
	 * @return the required file
	 */
	public InputStream getFile(String id, String fileName);

	/**
	 * This method fetches the packet corresponding to an ID and returns it.
	 *
	 * @param id
	 *            The id
	 * @return The packet in specified format
	 */
	public InputStream getPacket(String id);

	/**
	 * Checks if is packet present.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the boolean
	 */
	public boolean isPacketPresent(String id);

	/**
	 * This method stores the packet corresponding to an ID
	 * 
	 * @param id
	 *            The id
	 * @param key
	 *            Physical path of the packet which needs to be stored
	 * @param file
	 *            file to be stored
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
	 * This method unzips the packet corresponding to an enrolment ID and uploads
	 * individual files of that packet.
	 *
	 * @param id
	 *            The id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void unpackPacket(String id);

}