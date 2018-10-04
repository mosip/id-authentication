package io.mosip.kernel.sftppacketuploader.utils;

import java.io.File;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.mosip.kernel.sftppacketuploader.constant.PacketUploaderConfiguration;
import io.mosip.kernel.sftppacketuploader.constant.PacketUploaderConstants;
import io.mosip.kernel.sftppacketuploader.constant.PacketUploaderExceptionConstants;
import io.mosip.kernel.sftppacketuploader.exception.MosipEmptyPathException;
import io.mosip.kernel.sftppacketuploader.exception.MosipIllegalConfigurationException;
import io.mosip.kernel.sftppacketuploader.exception.MosipIllegalIdentityException;
import io.mosip.kernel.sftppacketuploader.exception.MosipNullConfigurationException;
import io.mosip.kernel.sftppacketuploader.exception.MosipNullPathException;
import io.mosip.kernel.sftppacketuploader.exception.MosipPacketSizeException;

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
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 * @return configured {@link Session}
	 */
	public static Session configureSession(JSch jsch, PacketUploaderConfiguration configuration) {
		Session session = null;
		try {
			session = jsch.getSession(configuration.getUser(), configuration.getHost(), configuration.getPort());
		} catch (JSchException e) {
			throw new MosipIllegalConfigurationException(
					PacketUploaderExceptionConstants.MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION);
		}
		session.setConfig(PacketUploaderConstants.STR_STRICT_HOST_KEY_CHECKING.getKey(),
				PacketUploaderConstants.STR_STRICT_HOST_KEY_CHECKING.getValue());
		session.setConfig(PacketUploaderConstants.AUTHENTICATIONS.getKey(),
				PacketUploaderConstants.AUTHENTICATIONS.getValue());
		if (configuration.getPrivateKeyFileName() == null) {
			session.setPassword(configuration.getPassword());
		}
		return session;
	}

	/**
	 * This adds private key as identity
	 * 
	 * @param jsch
	 *            {@link JSch} instance
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void addIdentity(JSch jsch, PacketUploaderConfiguration configuration) {
		try {
			if (configuration.getPrivateKeyPassphrase() != null) {
				jsch.addIdentity(configuration.getPrivateKeyFileName(), configuration.getPrivateKeyPassphrase());
			} else {
				jsch.addIdentity(configuration.getPrivateKeyFileName());
			}
		} catch (JSchException e) {
			throw new MosipIllegalIdentityException(PacketUploaderExceptionConstants.MOSIP_ILLEGAL_IDENTITY_EXCEPTION,
					e);
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
			throw new MosipNullPathException(PacketUploaderExceptionConstants.MOSIP_NULL_PATH_EXCEPTION);
		} else if (packetPath.isEmpty()) {
			throw new MosipEmptyPathException(PacketUploaderExceptionConstants.MOSIP_EMPTY_PATH_EXCEPTION);
		} else if (new File(packetPath).length() > Long.parseLong(PacketUploaderConstants.PACKET_SIZE_MAX.getValue())
				|| new File(packetPath).length() == Long
						.parseLong(PacketUploaderConstants.PACKET_SIZE_MIN.getValue())) {
			throw new MosipPacketSizeException(PacketUploaderExceptionConstants.MOSIP_PACKET_SIZE_EXCEPTION);
		}

	}

	/**
	 * Validation method for configurations
	 * 
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void checkConfiguration(PacketUploaderConfiguration configuration) {
		if (configuration == null) {
			throw new MosipNullConfigurationException(
					PacketUploaderExceptionConstants.MOSIP_NULL_CONFIGURATION_EXCEPTION);
		} else if (configuration.getHost() == null) {
			throw new MosipNullConfigurationException(PacketUploaderExceptionConstants.MOSIP_NULL_HOST_EXCEPTION);
		} else if (configuration.getHost().isEmpty()) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_EMPTY_HOST_EXCEPTION);
		} else if (configuration.getPort() < Integer.parseInt(PacketUploaderConstants.PORT_MIN.getValue())
				|| configuration.getPort() > Integer.parseInt(PacketUploaderConstants.PORT_MAX.getValue())) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_INVALID_PORT_EXCEPTION);
		} else if (configuration.getUser() == null) {
			throw new MosipNullConfigurationException(PacketUploaderExceptionConstants.MOSIP_NULL_USER_EXCEPTION);
		} else if (configuration.getUser().isEmpty()) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_NULL_USER_EXCEPTION);
		} else if (configuration.getSftpRemoteDirectory() == null) {
			throw new MosipNullConfigurationException(
					PacketUploaderExceptionConstants.MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION);
		} else if (configuration.getSftpRemoteDirectory().isEmpty()) {
			throw new MosipIllegalConfigurationException(
					PacketUploaderExceptionConstants.MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION);
		}
		checkKey(configuration);
	}

	/**
	 * Validation method for Keys
	 * 
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 */
	public static void checkKey(PacketUploaderConfiguration configuration) {
		if (configuration.getPassword() == null && configuration.getPrivateKeyFileName() == null) {
			throw new MosipNullConfigurationException(PacketUploaderExceptionConstants.MOSIP_INVALID_KEY_EXCEPTION);
		} else if (configuration.getPassword() == null && configuration.getPrivateKeyFileName().isEmpty()) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_INVALID_KEY_EXCEPTION);
		} else if (configuration.getPrivateKeyFileName() == null && configuration.getPassword().isEmpty()) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_INVALID_KEY_EXCEPTION);
		} else if (configuration.getPassword() != null && configuration.getPrivateKeyFileName() != null
				&& configuration.getPassword().isEmpty() && configuration.getPrivateKeyFileName().isEmpty()) {
			throw new MosipIllegalConfigurationException(PacketUploaderExceptionConstants.MOSIP_INVALID_KEY_EXCEPTION);
		}
	}
}
