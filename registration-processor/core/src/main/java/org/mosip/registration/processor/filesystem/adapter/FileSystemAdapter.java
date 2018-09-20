package org.mosip.registration.processor.filesystem.adapter;

import java.io.File;
import java.io.IOException;

/**
 * MOSIP ADAPTER INTERFACE FOR CONNECTING TO DFS
 *
 * @param <T>
 *            Acceptable and Return type of individual packets, files
 * @param <U>
 *            FileNames that are acceptable
 * @param <V>
 *            Return type after a successful operation
 * 
 * @author Pranav Kumar
 * 
 * @since 0.0.1
 */
public interface FileSystemAdapter<T, U, V> {

	/**
	 * This method stores the packet corresponding to an enrolment ID
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @param filePath
	 *            Physical path of the packet which needs to be stored
	 * @return True If the packet is stored successfully
	 */
	public V storePacket(String enrolmentId, File filePath);

	/**
	 * This method fetches the packet corresponding to an enrolment ID and returns
	 * it
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @return The packet in specified format
	 */
	public T getPacket(String enrolmentId);

	/**
	 * This method fetches a file corresponding to an enrolment ID and returns it
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @param fileName
	 *            Required file name
	 * @return the required file
	 */
	public T getFile(String enrolmentId, U fileName);

	/**
	 * This method unzips the packet corresponding to an enrolment ID and uploads
	 * individual files of that packet
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @throws IOException
	 */
	public void unpackPacket(String enrolmentId) throws IOException;

	/**
	 * This method deletes the packet corresponding to an enrolment ID
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @return True if the packet is deleted successfully
	 */
	public V deletePacket(String enrolmentId);

	/**
	 * This method deletes a particular file related to an enrolment ID
	 * 
	 * @param enrolmentId
	 *            The enrolmentId
	 * @param fileName
	 *            The file which needs to be deleted
	 * @return True if the file is successfully deleted
	 */
	public V deleteFile(String enrolmentId, U fileName);

}
