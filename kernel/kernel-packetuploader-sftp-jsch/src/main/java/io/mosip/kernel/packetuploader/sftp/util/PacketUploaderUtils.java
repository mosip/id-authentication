package io.mosip.kernel.packetuploader.sftp.util;

import java.io.File;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.mosip.kernel.core.packetuploader.exception.EmptyPathException;
import io.mosip.kernel.core.packetuploader.exception.IllegalConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.IllegalIdentityException;
import io.mosip.kernel.core.packetuploader.exception.NullConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.NullPathException;
import io.mosip.kernel.core.packetuploader.exception.PacketSizeException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.sftp.model.SFTPServer;

/**
 * Util Class for Packet Uploader
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploaderUtils {
	/**
	 * Constructor for this class
	 */
	private PacketUploaderUtils() {
	}

	/**
	 * This configures session with given configuration
	 * 
	 * @param jsch
	 *            {@link JSch} instance
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 * @return configured {@link Session}
	 */
	public static Session configureSession(JSch jsch, SFTPServer sftpServer) {
		Session session = null;
		try {
			session = jsch.getSession(sftpServer.getUser(), sftpServer.getHost(), sftpServer.getPort());
		} catch (JSchException e) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION.getErrorMessage(), e);
		}
		session.setConfig(PacketUploaderConstant.STR_STRICT_HOST_KEY_CHECKING.getKey(),
				PacketUploaderConstant.STR_STRICT_HOST_KEY_CHECKING.getValue());
		session.setConfig(PacketUploaderConstant.AUTHENTICATIONS.getKey(),
				PacketUploaderConstant.AUTHENTICATIONS.getValue());
		if (sftpServer.getPrivateKeyFileName() == null) {
			session.setPassword(sftpServer.getPassword());
		}
		return session;
	}

	/**
	 * This adds private key as identity
	 * 
	 * @param jsch
	 *            {@link JSch} instance
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 */
	public static void addIdentity(JSch jsch, SFTPServer sftpServer) {
		try {
			if (sftpServer.getPrivateKeyPassphrase() != null) {
				jsch.addIdentity(sftpServer.getPrivateKeyFileName(), sftpServer.getPrivateKeyPassphrase());
			} else {
				jsch.addIdentity(sftpServer.getPrivateKeyFileName());
			}
		} catch (JSchException e) {
			throw new IllegalIdentityException(
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_IDENTITY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_ILLEGAL_IDENTITY_EXCEPTION.getErrorMessage()+ PacketUploaderConstant.EXCEPTION_BREAKER.getValue() + e.getMessage(), e);
		}
	}

	/**
	 * Validation method for packetPath
	 * 
	 * @param packetPath
	 *            path of packet to upload
	 */
	public static void check(String packetPath) {
		if (packetPath == null) {
			throw new NullPathException(PacketUploaderExceptionConstant.MOSIP_NULL_PATH_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_PATH_EXCEPTION.getErrorMessage());
		} else if (packetPath.trim().isEmpty()) {
			throw new EmptyPathException(PacketUploaderExceptionConstant.MOSIP_EMPTY_PATH_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_PATH_EXCEPTION.getErrorMessage());
		} else if (new File(packetPath).length() > Long.parseLong(PacketUploaderConstant.PACKET_SIZE_MAX.getValue())
				|| new File(packetPath).length() == Long.parseLong(PacketUploaderConstant.PACKET_SIZE_MIN.getValue())) {
			throw new PacketSizeException(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage());
		}

	}

	/**
	 * Validation method for configurations
	 * 
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 */
	public static void checkConfiguration(SFTPServer sftpServer) {
		if (sftpServer == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_CONFIGURATION_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_CONFIGURATION_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getHost() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_HOST_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_HOST_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getHost().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_EMPTY_HOST_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_HOST_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getPort() < Integer.parseInt(PacketUploaderConstant.PORT_MIN.getValue())
				|| sftpServer.getPort() > Integer.parseInt(PacketUploaderConstant.PORT_MAX.getValue())) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_PORT_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_PORT_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getUser() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getUser().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_USER_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getSftpRemoteDirectory() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getSftpRemoteDirectory().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION.getErrorMessage());
		}
		
	}

	/**
	 * Validation method for Keys
	 * 
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 */
	public static void checkKey(SFTPServer sftpServer) {
		if (sftpServer.getPassword() == null && sftpServer.getPrivateKeyFileName() == null) {
			throw new NullConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getPassword() == null && sftpServer.getPrivateKeyFileName().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		} else if (sftpServer.getPrivateKeyFileName() == null && sftpServer.getPassword().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		} else {
			keyIsEmpty(sftpServer);
		}
	}

	private static void keyIsEmpty(SFTPServer sftpServer) {
		if (sftpServer.getPassword() != null && sftpServer.getPrivateKeyFileName() != null
				&& sftpServer.getPassword().isEmpty() && sftpServer.getPrivateKeyFileName().trim().isEmpty()) {
			throw new IllegalConfigurationException(
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		}
	}
}
