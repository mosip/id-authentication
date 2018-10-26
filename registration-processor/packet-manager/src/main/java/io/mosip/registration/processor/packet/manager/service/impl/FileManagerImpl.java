package io.mosip.registration.processor.packet.manager.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;

/**
 * The implementation Class for FileManager.
 *
 * @author M1039303
 */
@Service
public class FileManagerImpl implements FileManager<DirectoryPathDto, InputStream> {

	private final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Value("${registration.processor.FTP_ZONE}")
	private String path;

	@Autowired
	private Environment env;

	private static final String FILE_PATH_NOT_ACCESSIBLE = "The Folder Path is not Accessible.";
	private static final String FILE_NOT_FOUND_IN_DESTINATION = "The File is not present in Destination Folder.";
	private static final String FILE_NOT_FOUND_IN_SOURCE = "The File is not present in Source Folder.";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.file.system.connector.service.FileManager#get(java.lang
	 * .Object, java.lang.Object)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.file.system.connector.service.FileManager#put(java.lang
	 * .Object, java.lang.Object)
	 */
	@Override
	public void put(String fileName, InputStream file, DirectoryPathDto workingDirectory) throws IOException {
		File destinationDirectory = new File(
				env.getProperty(workingDirectory.toString()) + File.separator + getFileName(fileName));
		FileUtils.copyToFile(file, destinationDirectory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.id.issuance.file.system.connector.service.FileManager#
	 * checkIfFileExists(java.lang.Object, java.lang.String)
	 */
	@Override
	public Boolean checkIfFileExists(DirectoryPathDto destinationDirectory, String fileName) {
		File file = FileUtils.getFile(env.getProperty(destinationDirectory.toString()), getFileName(fileName));
		return file.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.id.issuance.file.system.connector.service.FileManager#cleanUpFile(
	 * java.lang.Object, java.lang.Object, java.lang.String)
	 */
	@Override
	public void cleanUpFile(DirectoryPathDto srcFolderLoc, DirectoryPathDto destFolderLoc, String fileName) {

		boolean fileExistsInDestination = false;
		boolean fileExistsInSource = false;
		try {

			fileExistsInDestination = (boolean) checkIfFileExists(destFolderLoc, fileName);
			if (fileExistsInDestination) {

				fileExistsInSource = (boolean) checkIfFileExists(srcFolderLoc, fileName);
				if (fileExistsInSource) {
					delete(srcFolderLoc, fileName);
				} else {
					throw new FileNotFoundInSourceException(FILE_NOT_FOUND_IN_SOURCE);

				}
			} else {
				throw new FileNotFoundInDestinationException(FILE_NOT_FOUND_IN_DESTINATION);

			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new FilePathNotAccessibleException(FILE_PATH_NOT_ACCESSIBLE);

		}

	}

	/**
	 * Delete a file from directory.
	 *
	 * @param workingDirectory
	 *            the working directory
	 * @param fileName
	 *            the file name
	 * @return the object
	 * @throws JSchException
	 *             the j sch exception
	 * @throws SftpException
	 *             the sftp exception
	 * @throws IOException
	 */
	private void delete(DirectoryPathDto destinationDirectory, String fileName) throws IOException {

		File filePath = new File(
				env.getProperty(destinationDirectory.toString()) + File.separator + getFileName(fileName));

		FileUtils.forceDelete(filePath);
	}

	/**
	 * Get the file name with extension.
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileName(String fileName) {
		return fileName + extention;
	}

	@Override
	public void copy(String fileName, DirectoryPathDto sourceWorkingDirectory,
			DirectoryPathDto destinationWorkingDirectory) throws IOException {
		if (checkIfFileExists(sourceWorkingDirectory, fileName)) {
			File srcFile = new File(
					env.getProperty(sourceWorkingDirectory.toString()) + File.separator + getFileName(fileName));
			File destFile = new File(
					env.getProperty(destinationWorkingDirectory.toString()) + File.separator + getFileName(fileName));
			FileUtils.copyFile(srcFile, destFile);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idissuance.packet.manager.service.FileManager#getCurrentDirectory()
	 */
	@Override
	public String getCurrentDirectory() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idissuance.packet.manager.service.FileManager#cleanUpFile(java.lang.
	 * Object, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void cleanUpFile(DirectoryPathDto srcFolderLoc, DirectoryPathDto destFolderLoc, String fileName,
			String childFolderName) {
		boolean fileExistsInDestination = false;
		boolean fileExistsInSource = false;
		try {

			fileExistsInDestination = (boolean) checkIfFileExists(destFolderLoc, fileName);
			if (fileExistsInDestination) {

				fileExistsInSource = (boolean) checkIfFileExists(srcFolderLoc,
						childFolderName + File.separator + fileName);
				if (fileExistsInSource) {
					delete(srcFolderLoc, childFolderName + File.separator + fileName);
				} else {
					throw new FileNotFoundInSourceException(FILE_NOT_FOUND_IN_SOURCE);

				}
			} else {
				throw new FileNotFoundInDestinationException(FILE_NOT_FOUND_IN_DESTINATION);

			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new FilePathNotAccessibleException(FILE_PATH_NOT_ACCESSIBLE);

		}

	}

}
