package io.mosip.registration.processor.packet.manager.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.manager.decryptor.Decryptor;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.exception.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.packet.manager.utils.ZipUtils;

/**
 * File System Manager implementation.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Component
public class FileSystemManagerImpl implements PacketManager {

	@Autowired
	private FileSystemAdapter fileSystemAdapter;

	@Autowired
	private Decryptor decryptor;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(FileSystemManagerImpl.class);

	private static final String PACKET_NOTAVAILABLE_ERROR_DESC = "the requested file {} in the destination is not found";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager
	 * #checkFileExistence(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkFileExistence(String id, String fileName)
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::checkFileExistence()::entry");
		InputStream decryptedData = getFile(id);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::checkFileExistence()::extractZip");
		return ZipUtils.unzipAndCheckIsFileExist(decryptedData, fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager
	 * #getPacket(java.lang.String)
	 */
	@Override
	public InputStream getPacket(String id) throws IOException, ApisResourceAccessException,
			PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::getPacket()::entry");
		return getFile(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager
	 * #getFile(java.lang.String, java.lang.String)
	 */
	@Override
	public InputStream getFile(String id, String fileName)
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::getFile()::entry");
		InputStream decryptedData = getFile(id);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::getFile()::extractZip");
		return ZipUtils.unzipAndGetFile(decryptedData, fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager
	 * #storePacket(java.lang.String, java.io.InputStream)
	 */
	@Override
	public boolean storePacket(String id, InputStream file) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::storePacket({},data)::entry" + id);
		return fileSystemAdapter.storePacket(id, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager
	 * #storePacket(String, File)
	 */
	@Override
	public boolean storePacket(String id, File filePath) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::storePacket({},{})::entry" + id + " " + filePath);
		return fileSystemAdapter.storePacket(id, filePath);
	}

	/**
	 * Method to get file from fileSystem and decrypt it.
	 * 
	 * @param id
	 *            registration id
	 * @return the decrypted packet if everything is successfully
	 * @throws PacketDecryptionFailureException
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 */
	private InputStream getFile(String id)
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::fileSystemAdapter.getPacket()");
		InputStream data = fileSystemAdapter.getPacket(id);
		if (data == null) {
			throw new FileNotFoundInDestinationException(PACKET_NOTAVAILABLE_ERROR_DESC + id);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::getFile(regid)::decryptor");
		InputStream decryptedData = decryptor.decrypt(data, id);
		if (decryptedData == null) {
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
							.getErrorMessage());
		}
		return decryptedData;
	}

	@Override
	public boolean isPacketPresent(String id) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"HdfsFileSystemManagerImpl::isPacketPresent({},{})::entry" + id );
		return fileSystemAdapter.isPacketPresent(id);
	}

}
