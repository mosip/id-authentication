package io.mosip.registration.processor.core.spi.filesystem.manager;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Interface FileManager.
 *
 * @author Mukul Puspam
 *
 * @param <T>
 *            Directory path
 * @param <U>
 *            Return type of operations
 * @param <V>
 *            Input file
 */
public interface FileManager<D, F> {

	/**
	 * Copy.
	 *
	 * @param fileName the file name
	 * @param sourceWorkingDirectory the source working directory
	 * @param destinationWorkingDirectory the destination working directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void copy(String fileName, D sourceWorkingDirectory, D destinationWorkingDirectory) throws IOException;

	/**
	 * stores file to the specific directory in VM.
	 *
	 * @param fileName the file name
	 * @param file            file to be stored
	 * @param workingDirectory            working directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void put(String fileName, F file, D workingDirectory) throws IOException;

	/**
	 * Check if file exists.
	 *
	 * @param workingDirectory
	 *            working directory
	 * @param fileName
	 *            name of the file to be checked
	 * @return boolean depending on if file exists or not
	 */
	public Boolean checkIfFileExists(D workingDirectory, String fileName);

	/**
	 * Clean up file.
	 *
	 * @param srcFolderLoc
	 *            source directory
	 * @param destFolderLoc
	 *            destination directory
	 * @param fileName
	 *            name of the file to be cleaned up
	 * @return boolean depending on cleanup is successful or not
	 */
	public void cleanUpFile(D srcFolderLoc, D destFolderLoc, String fileName);

	/**
	 * Check if file exists in source and destination and cleanup.
	 *
	 * @param srcFolderLoc
	 *            source directory
	 * @param destFolderLoc
	 *            destination directory
	 * @param fileName
	 *            name of the file to be cleaned up
	 * @param childPath
	 *            the child path
	 * @return boolean depending on cleanup is successful or not
	 */
	public void cleanUpFile(D srcFolderLoc, D destFolderLoc, String fileName, String childPath);

	/**
	 * Delete packet.
	 *
	 * @param workingDirectory
	 *            the working directory
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void deletePacket(D workingDirectory, String fileName) throws IOException;

	/**
	 * Delete folder.
	 *
	 * @param destinationDirectory
	 *            the destination directory
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void deleteFolder(D destinationDirectory, String fileName) throws IOException;
}
