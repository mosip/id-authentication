package io.mosip.registration.processor.core.spi.filesystem.manager;

import java.io.IOException;

/**
 * @author M1039303
 *
 * @param <T>
 *            Directory path
 * @param <U>
 *            Return type of operations
 * @param <V>
 *            Input file
 */
public interface FileManager<D, F> {
	
	public void copy(String fileName, D sourceWorkingDirectory, D destinationWorkingDirectory) throws IOException;
	/**
	 * stores file to the specific directory in VM
	 * 
	 * @param workingDirectory
	 *            working directory
	 * @param file
	 *            file to be stored
	 * @throws IOException
	 */
	public void put(String fileName, F file, D workingDirectory) throws IOException;


	/**
	 * @param workingDirectory
	 *            working directory
	 * @param fileName
	 *            name of the file to be checked
	 * @return boolean depending on if file exists or not
	 * 
	 */
	public Boolean checkIfFileExists(D workingDirectory, String fileName);

	/**
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
	 *                source directory
	 * @param destFolderLoc
	 *                destination directory
	 * @param fileName
	 *            name of the file to be cleaned up
	 * @param parentPath
	 *            name of the child folder to be cleaned up
	 * @return boolean depending on cleanup is successful or not
	 */
	public void cleanUpFile(D srcFolderLoc, D destFolderLoc, String fileName, String childPath);
	
	/**
	 * Get current directory location.
	 *
	 * @return FTP_ZONE path
	 */
	public String getCurrentDirectory();
}
