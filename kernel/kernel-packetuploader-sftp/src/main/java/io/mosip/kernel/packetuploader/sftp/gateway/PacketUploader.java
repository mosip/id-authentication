package io.mosip.kernel.packetuploader.sftp.gateway;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.kernel.packetuploader.sftp.channel.SftpChannel;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConfiguration;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.sftp.exception.MosipConnectionException;
import io.mosip.kernel.packetuploader.sftp.exception.MosipNoSessionException;
import io.mosip.kernel.packetuploader.sftp.exception.MosipSFTPException;
import io.mosip.kernel.packetuploader.sftp.util.PacketUploaderUtils;

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
			Channel channel = session.openChannel(PacketUploaderConstant.STR_SFTP.getValue());
			sftpChannel = new SftpChannel((ChannelSftp) channel, configuration);
			channel.connect();
		} catch (JSchException e) {
			throw new MosipConnectionException(PacketUploaderExceptionConstant.MOSIP_CONNECTION_EXCEPTION, e);
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
			throw new MosipSFTPException(PacketUploaderExceptionConstant.MOSIP_SFTP_EXCEPTION, e);
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
			throw new MosipNoSessionException(PacketUploaderExceptionConstant.MOSIP_NO_SESSION_FOUND_EXCEPTION, e);
		}
		channelSftp.exit();
		if (session != null) {
			session.disconnect();
		}
	}

}
