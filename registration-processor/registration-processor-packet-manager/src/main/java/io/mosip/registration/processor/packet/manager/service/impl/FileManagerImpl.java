package io.mosip.registration.processor.packet.manager.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.JschConnectionException;
import io.mosip.registration.processor.core.exception.SftpFileOperationException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
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

	/** The extention. */
	@Value("${registration.processor.packet.ext}")
	private String extension;

	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;


	@Value("${registration.processor.vm.ppk}")
	private String regProcPPK;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(FileManagerImpl.class);

	/** The path. */

	/** The env. */
	@Autowired
	private Environment env;

	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;

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

		String filepath = env.getProperty(workingDirectory.toString());
		File destinationDirectory = FileUtils.getFile(filepath, getFileName(fileName));
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

		String filepath = env.getProperty(destinationDirectory.toString());
		File filePath = FileUtils.getFile(filepath, getFileName(fileName));

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
		return fileName + getExtension();
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

			String filepath = env.getProperty(sourceWorkingDirectory.toString());
			File srcFile = FileUtils.getFile(filepath, getFileName(fileName));

			String filepath1 = env.getProperty(destinationWorkingDirectory.toString());
			File destFile = FileUtils.getFile(filepath1, getFileName(fileName));
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

		 String filepath=env.getProperty(destinationDirectory.toString());
		 File srcFile=FileUtils.getFile(filepath, fileName);
		//File filePath = new File(env.getProperty(destinationDirectory.toString()) + File.separator + fileName);

		FileUtils.forceDelete(srcFile);
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

	@Override
	public byte[] getFile(DirectoryPathDto workingDirectory, String fileName, SftpJschConnectionDto sftpConnectionDto)
			throws JschConnectionException, SftpFileOperationException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), fileName,
				"FileManagerImpl::getFile(DirectoryPathDto workingDirectory, String fileName,SftpJschConnectionDto sftpConnectionDto)::entry");

		byte[] bytedata = null;
		try {
			channelSftp = getSftpConnection(sftpConnectionDto);
			try (InputStream is = channelSftp
					.get(env.getProperty(workingDirectory.toString()) + "/" + getFileName(fileName))) {
				bytedata = IOUtils.toByteArray(is);
			}

		} catch (SftpException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					fileName, e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new SftpFileOperationException(PlatformErrorMessages.RPR_PKM_SFTP_FILE_OPERATION_FAILED.getMessage());

		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					fileName, e.getMessage() + ExceptionUtils.getStackTrace(e));

		}
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), fileName,
				"FileManagerImpl::getFile(DirectoryPathDto workingDirectory, String fileName,SftpJschConnectionDto sftpConnectionDto)::exit");

		return bytedata;

	}

	public ChannelSftp getSftpConnection(SftpJschConnectionDto sftpConnectionDto) throws JschConnectionException {
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::getSftpConnection()::entry");

		if (channelSftp != null && channelSftp.isConnected()) {
			return channelSftp;
		}

		try {

			JSch jsch = new JSch();
			jsch.addIdentity(getPPKPath());
			session = jsch.getSession(sftpConnectionDto.getUser(), sftpConnectionDto.getHost(),
					sftpConnectionDto.getPort());
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel(sftpConnectionDto.getProtocal());
			channel.connect();
			channelSftp = (ChannelSftp) channel;

		} catch (JSchException |IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new JschConnectionException(PlatformErrorMessages.RPR_PKM_JSCH_NOT_CONNECTED.getMessage());
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"FileManagerImpl::getSftpConnection()::exit");

		return channelSftp;

	}

	@Override
	public boolean copy(String fileName, DirectoryPathDto sourceWorkingDirectory,
			DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)
					throws IOException, JschConnectionException, SftpFileOperationException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), fileName,
				"FileManagerImpl::copy(String fileName, DirectoryPathDto sourceWorkingDirectory,DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)::entry");

		boolean status = false;
		byte[] bytedata = null;
		String sourceFilePath = env.getProperty(sourceWorkingDirectory.toString()) + "/" + getFileName(fileName);
		String destinationFilePath = env.getProperty(destinationWorkingDirectory.toString()) + "/"
				+ getFileName(fileName);
		try {

			channelSftp = getSftpConnection(sftpConnectionDto);

			try (InputStream is = channelSftp.get(sourceFilePath)) {
				bytedata = IOUtils.toByteArray(is);
			}

			channelSftp.put(new ByteArrayInputStream(bytedata), destinationFilePath);

			if (channelSftp.get(destinationFilePath) != null) {
				status = true;
			}

			
		} catch (SftpException e) {

			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				status = false;
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));
				return status;
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));
				throw new SftpFileOperationException(
						PlatformErrorMessages.RPR_PKM_SFTP_FILE_OPERATION_FAILED.getMessage());

			}

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), fileName,
				"FileManagerImpl::copy(String fileName, DirectoryPathDto sourceWorkingDirectory,DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)::exit");

		return status;
	}

	@Override
	public boolean cleanUp(String fileName, DirectoryPathDto sourceWorkingDirectory,
			DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)
					throws IOException, JschConnectionException, SftpFileOperationException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),fileName,
				"FileManagerImpl::cleanUpFile(String fileName, DirectoryPathDto sourceWorkingDirectory,DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)::entry");

		boolean status = false;
		String sourceFilePath = env.getProperty(sourceWorkingDirectory.toString()) + "/" + getFileName(fileName);
		String destinationFilePath = env.getProperty(destinationWorkingDirectory.toString()) + "/"
				+ getFileName(fileName);
		try {

			channelSftp = getSftpConnection(sftpConnectionDto);
			if (channelSftp.get(destinationFilePath) != null) {

				if (channelSftp.get(sourceFilePath) != null) {
					channelSftp.rm(sourceFilePath);
					status = true;
				}

			}
	
		} catch (SftpException e) {

			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				status = false;
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));
				return status;
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new SftpFileOperationException(
						PlatformErrorMessages.RPR_PKM_SFTP_FILE_OPERATION_FAILED.getMessage());

			}

		}
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),fileName,
				"FileManagerImpl::cleanUpFile(String fileName, DirectoryPathDto sourceWorkingDirectory,DirectoryPathDto destinationWorkingDirectory, SftpJschConnectionDto sftpConnectionDto)::exit");

		return status;
	}

    @Override
    public void disconnectSftp() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (session != null) {
			session.disconnect();
		}
    }

    public String getExtension() {
		return extension;
	}

	public String getPPKPath() throws  IOException {
		RestTemplate restTemplate = new RestTemplate();
		String data= restTemplate.getForObject(configServerFileStorageURL + regProcPPK, String.class);
		File file = FileUtils.getFile(regProcPPK);
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(data.getBytes());
		} finally {
			out.close();
		}
		return file.getPath();

	}
}
