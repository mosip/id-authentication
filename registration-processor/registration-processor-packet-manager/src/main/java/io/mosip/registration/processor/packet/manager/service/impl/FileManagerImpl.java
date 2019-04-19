package io.mosip.registration.processor.packet.manager.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
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

@RefreshScope
@Service
public class FileManagerImpl implements FileManager<DirectoryPathDto, InputStream> {

	private static final String EXTENSION = "registration.processor.packet.ext";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(FileManagerImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::put()::entry");
		
		String filepath=env.getProperty(workingDirectory.toString());
		File destinationDirectory=FileUtils.getFile(filepath, getFileName(fileName));				
		FileUtils.copyToFile(file, destinationDirectory);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::put()::exit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.id.issuance.file.system.connector.service.FileManager#
	 * checkIfFileExists(java.lang.Object, java.lang.String)
	 */
	@Override
	public Boolean checkIfFileExists(DirectoryPathDto destinationDirectory, String fileName) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::checkIfFileExists()::entry");
		File file = FileUtils.getFile(env.getProperty(destinationDirectory.toString()), getFileName(fileName));

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::checkIfFileExists()::exit");
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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::cleanUpFile()::entry");
		boolean fileExistsInDestination = false;
		boolean fileExistsInSource = false;
		try {

			fileExistsInDestination = (boolean) checkIfFileExists(destFolderLoc, fileName);
			if (fileExistsInDestination) {

				fileExistsInSource = (boolean) checkIfFileExists(srcFolderLoc, fileName);
				if (fileExistsInSource) {
					delete(srcFolderLoc, fileName);
				} else {
					throw new FileNotFoundInSourceException(
							PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());

				}
			} else {
				throw new FileNotFoundInDestinationException(
						PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getMessage());

			}
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new FilePathNotAccessibleException(
					PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::cleanUpFile()::exit");

	}

	/**
	 * Delete a file from directory.
	 *
	 * @param destinationDirectory
	 *            the destination directory
	 * @param fileName
	 *            the file name
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void delete(DirectoryPathDto destinationDirectory, String fileName) throws IOException {

		String filepath=env.getProperty(destinationDirectory.toString());
		File filePath=FileUtils.getFile(filepath, getFileName(fileName));				
		
		FileUtils.forceDelete(filePath);
	}

	/**
	 * Get the file name with extension.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file name
	 */
	private String getFileName(String fileName) {
		return fileName + env.getProperty(EXTENSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileManager#copy(
	 * java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void copy(String fileName, DirectoryPathDto sourceWorkingDirectory,
			DirectoryPathDto destinationWorkingDirectory) throws IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::copy()::entry");
		if (checkIfFileExists(sourceWorkingDirectory, fileName)) {
			
			String filepath=env.getProperty(sourceWorkingDirectory.toString());
			File srcFile=FileUtils.getFile(filepath, getFileName(fileName));
			
			String filepath1=env.getProperty(destinationWorkingDirectory.toString());
			File destFile=FileUtils.getFile(filepath1, getFileName(fileName));
			FileUtils.copyFile(srcFile, destFile);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::copy()::exit");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idissuance.packet.manager.service.FileManager#getCurrentDirectory()
	 */

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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::cleanUpFile()::entry");

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
					throw new FileNotFoundInSourceException(
							PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());

				}
			} else {
				throw new FileNotFoundInDestinationException(
						PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getMessage());

			}
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new FilePathNotAccessibleException(
					PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::cleanUpFile()::exit");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.filesystem.manager.FileManager#
	 * deletePacket(java.lang.Object, java.lang.String)
	 */
	@Override
	public void deletePacket(DirectoryPathDto workingDirectory, String fileName) throws IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::deletePacket()::entry");
		boolean isFilePresent = (boolean) checkIfFileExists(workingDirectory, fileName);
		if (isFilePresent) {
			delete(workingDirectory, fileName);
		} else {
			throw new FileNotFoundInSourceException(
					PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::deletePacket()::exit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.filesystem.manager.FileManager#
	 * deleteFolder(java.lang.Object, java.lang.String)
	 */
	@Override
	public void deleteFolder(DirectoryPathDto destinationDirectory, String fileName) throws IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::deleteFolder()::entry");
		
//		String filepath=env.getProperty(destinationDirectory.toString());
//		File srcFile=FileUtils.getFile(filepath, getFileName(fileName));
		File filePath = new File(env.getProperty(destinationDirectory.toString()) + File.separator + fileName);

		FileUtils.forceDelete(filePath);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::deleteFolder()::exit");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.filesystem.manager.FileManager#
	 * getFile(java.lang.Object, java.lang.String)
	 */
	@Override
	public File getFile(DirectoryPathDto workingDirectory, String fileName) throws IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::getFile()::entry");
		File file = FileUtils.getFile(env.getProperty(workingDirectory.toString()), getFileName(fileName));

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::getFile()::exit");
		return file;
	}

}
