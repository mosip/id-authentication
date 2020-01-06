package io.mosip.registration.processor.core.spi.filesystem.manager;

import java.io.File;
import java.io.IOException;

import io.mosip.registration.processor.core.exception.JschConnectionException;
import io.mosip.registration.processor.core.exception.SftpFileOperationException;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;

// TODO: Auto-generated Javadoc
/**
 * The Interface FileManager.
 *
 * @author Mukul Puspam
 * @param <D>
 *            the generic type
 * @param <F>
 *            the generic type
 */
public interface FileManager<D, F> {

	/**
	 * Copy.
	 *
	 * @param fileName
	 *            the file name
	 * @param sourceWorkingDirectory
	 *            the source working directory
	 * @param destinationWorkingDirectory
	 *            the destination working directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void copy(String fileName, D sourceWorkingDirectory, D destinationWorkingDirectory) throws IOException;

	/**
	 * stores file to the specific directory in VM.
	 *
	 * @param fileName
	 *            the file name
	 * @param file
	 *            file to be stored
	 * @param workingDirectory
	 *            working directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
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

	/**
	 * Gets the file.
	 *
	 * @param workingDirectory
	 *            the working directory
	 * @param fileName
	 *            the file name
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File getFile(D workingDirectory, String fileName) throws IOException;

	/**
	 * Get the file from working directory
	 *
	 * @param workingDirectory
	 * @param fileName
	 * @param sftpConnectionDto
	 * @return
	 * @throws JschConnectionException
	 * @throws SftpFileOperationException
	 */
	public byte[] getFile(D workingDirectory, String fileName,SftpJschConnectionDto sftpConnectionDto)throws  JschConnectionException, SftpFileOperationException;

	/**
	 * Copy file from one directory to another
	 *
	 * @param fileName
	 * @param sourceWorkingDirectory
	 * @param destinationWorkingDirectory
	 * @param sftpConnectionDto
	 * @return
	 * @throws IOException
	 * @throws JschConnectionException
	 * @throws SftpFileOperationException
	 */
	public boolean copy(String fileName, D sourceWorkingDirectory, D destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto) throws IOException, JschConnectionException, SftpFileOperationException ;

	/**
	 * Cleanup a file from a working directory
	 *
	 * @param fileName
	 * @param sourceWorkingDirectory
	 * @param destinationWorkingDirectory
	 * @param sftpConnectionDto
	 * @return
	 * @throws IOException
	 * @throws JschConnectionException
	 * @throws SftpFileOperationException
	 */
	public boolean cleanUp(String fileName, D sourceWorkingDirectory, D destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto) throws IOException, JschConnectionException, SftpFileOperationException ;

	/**
	 * Disconnect the sftp connection used to get file from LANDING_ZONE.
	 */
	public void disconnectSftp();

}
