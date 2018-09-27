package org.mosip.kernel.sftppacketuploader.gateway;

import org.mosip.kernel.sftppacketuploader.channel.SftpChannel;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderConfiguration;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderConstants;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderExceptionConstants;
import org.mosip.kernel.sftppacketuploader.exceptions.MosipConnectionException;
import org.mosip.kernel.sftppacketuploader.exceptions.MosipNoSessionException;
import org.mosip.kernel.sftppacketuploader.exceptions.MosipSFTPException;
import org.mosip.kernel.sftppacketuploader.utils.PacketUploaderUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Mosip packet uploader SFTP GateWay
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketUploader {

	/**
	 * Constructor for this class
	 */
	private PacketUploader() {
	}

	/**
	 * This creates and connects SFTP channel based on configutaions
	 * 
	 * @param configuration
	 *            {@link PacketUploaderConfiguration} provided by user
	 * @return configured {@link SftpChannel} instance
	 * @throws MosipConnectionException
	 *             to be thrown when there is a exception during connection with
	 *             server
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
	 * Uploades file to server <i>(this method will not create destination folder it
	 * should be already present)</i>
	 * 
	 * @param sftpChannel
	 *            configured {@link SftpChannel} instance
	 * @param source
	 *            path of packet to be uploaded
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
	 * This releases the obtained Connection to server
	 * 
	 * @param sftpChannel
	 *            configured {@link SftpChannel} instance
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
