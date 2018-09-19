package org.mosip.kernel.packetuploader.gateway;

import org.mosip.kernel.packetuploader.channel.SftpChannel;
import org.mosip.kernel.packetuploader.constants.PacketUploaderConfiguration;
import org.mosip.kernel.packetuploader.constants.PacketUploaderConstants;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;
import org.mosip.kernel.packetuploader.exceptions.MosipConnectionException;
import org.mosip.kernel.packetuploader.exceptions.MosipIllegalConfigurationException;
import org.mosip.kernel.packetuploader.exceptions.MosipIllegalIdentityException;
import org.mosip.kernel.packetuploader.exceptions.MosipNoSessionException;
import org.mosip.kernel.packetuploader.exceptions.MosipSFTPException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class PacketUploader {

	private PacketUploader() {
	}

	public static SftpChannel createSFTPChannel(PacketUploaderConfiguration configuration)
			throws MosipConnectionException {
		JSch jsch = new JSch();
		if (configuration.getPrivateKeyFileName() != null) {
			addIdentity(jsch, configuration);
		}
		Session session = configureSession(jsch, configuration);
		SftpChannel sftpChannel = null;
		try {
			session.connect();
			Channel channel = session.openChannel(PacketUploaderConstants.STR_SFTP.getValue());
			sftpChannel = new SftpChannel((ChannelSftp) channel, configuration);
			channel.connect();
		} catch (JSchException e) {
			throw new MosipConnectionException(PacketUploaderExceptionConstants.MOSIP_CONNECTION_EXCEPTION);
		}
		return sftpChannel;
	}

	public static void upload(SftpChannel sftpChannel, String source) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		String target = sftpChannel.getConfiguration().getSftpRemoteDirectory();
		mkdir(channelSftp, target);
		lcd(channelSftp, source);
		try {
			channelSftp.put(source, target);
		} catch (SftpException e) {
			throw new MosipSFTPException(PacketUploaderExceptionConstants.MOSIP_SFTP_EXCEPTION);
		}
	}

	public static void releaseConnection(SftpChannel sftpChannel) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		Session session = null;
		try {
			session = channelSftp.getSession();
		} catch (JSchException e) {
			throw new MosipNoSessionException(PacketUploaderExceptionConstants.MOSIP_NO_SESSION_FOUND_EXCEPTION);
		}
		channelSftp.exit();
		if (session != null) {
			session.disconnect();
		}
	}

	private static void lcd(ChannelSftp sftpChannel, String path) {
		try {
			sftpChannel.lcd(path);
		} catch (SftpException e) {
			throw new MosipSFTPException(PacketUploaderExceptionConstants.MOSIP_SFTP_EXCEPTION);
		}
	}

	private static void mkdir(ChannelSftp sftpChannel, String target) {
		try {
			sftpChannel.mkdir(target);
		} catch (SftpException e) {
			throw new MosipSFTPException(PacketUploaderExceptionConstants.MOSIP_SFTP_EXCEPTION);
		}

	}

	private static Session configureSession(JSch jsch, PacketUploaderConfiguration configuration) {
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

	private static void addIdentity(JSch jsch, PacketUploaderConfiguration configuration) {
		try {
			if (configuration.getPrivateKeyPassphrase() != null) {
				jsch.addIdentity(configuration.getPrivateKeyFileName(), configuration.getPrivateKeyPassphrase());
			} else {
				jsch.addIdentity(configuration.getPrivateKeyFileName());
			}
		} catch (JSchException e) {
			throw new MosipIllegalIdentityException(PacketUploaderExceptionConstants.MOSIP_ILLEGAL_IDENTITY_EXCEPTION);
		}
	}
}
