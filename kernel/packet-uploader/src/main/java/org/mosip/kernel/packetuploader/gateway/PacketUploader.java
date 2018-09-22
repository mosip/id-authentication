package org.mosip.kernel.packetuploader.gateway;

import org.mosip.kernel.packetuploader.channel.SftpChannel;
import org.mosip.kernel.packetuploader.constants.PacketUploaderConfiguration;
import org.mosip.kernel.packetuploader.constants.PacketUploaderConstants;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;
import org.mosip.kernel.packetuploader.exceptions.MosipConnectionException;
import org.mosip.kernel.packetuploader.exceptions.MosipNoSessionException;
import org.mosip.kernel.packetuploader.exceptions.MosipSFTPException;
import org.mosip.kernel.packetuploader.utils.PacketUploaderUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploader {

	/**
	 * 
	 */
	private PacketUploader() {
	}

	/**
	 * @param configuration
	 * @return
	 * @throws MosipConnectionException
	 */
	public static SftpChannel createSFTPChannel(PacketUploaderConfiguration configuration)
			throws MosipConnectionException {
		PacketUploaderUtils.checkConfiguration(configuration);

		JSch jsch = new JSch();
		if (configuration.getPrivateKeyFileName() != null) {
			PacketUploaderUtils.addIdentity(jsch, configuration);
		}
		Session session = PacketUploaderUtils.configureSession(jsch, configuration);
		SftpChannel sftpChannel = null;
		try {
			session.connect();
			Channel channel = session.openChannel(PacketUploaderConstants.STR_SFTP.getValue());
			sftpChannel = new SftpChannel((ChannelSftp) channel, configuration);
			channel.connect();
		} catch (JSchException e) {
			throw new MosipConnectionException(PacketUploaderExceptionConstants.MOSIP_CONNECTION_EXCEPTION, e);
		}
		return sftpChannel;
	}

	/**
	 * @param sftpChannel
	 * @param source
	 */
	public static void upload(SftpChannel sftpChannel, String source) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		PacketUploaderUtils.check(source);
		String target = sftpChannel.getConfiguration().getSftpRemoteDirectory();
		try {
			channelSftp.put(source, target);
		} catch (SftpException e) {
			throw new MosipSFTPException(PacketUploaderExceptionConstants.MOSIP_SFTP_EXCEPTION, e);
		}
	}

	/**
	 * @param sftpChannel
	 */
	public static void releaseConnection(SftpChannel sftpChannel) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		Session session = null;
		try {
			session = channelSftp.getSession();
		} catch (JSchException e) {
			throw new MosipNoSessionException(PacketUploaderExceptionConstants.MOSIP_NO_SESSION_FOUND_EXCEPTION, e);
		}
		channelSftp.exit();
		if (session != null) {
			session.disconnect();
		}
	}

}
