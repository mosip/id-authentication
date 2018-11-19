package io.mosip.kernel.packetuploader.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.kernel.core.packetuploader.exception.ConnectionException;
import io.mosip.kernel.core.packetuploader.exception.NoSessionException;
import io.mosip.kernel.core.packetuploader.exception.SFTPException;
import io.mosip.kernel.packetuploader.sftp.model.SFTPChannel;
import io.mosip.kernel.packetuploader.sftp.model.SFTPServer;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.sftp.util.PacketUploaderUtils;

/**
 * Mosip packet uploader SFTP
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
	 * @param sftpServer
	 *            {@link SFTPServer} provided by user
	 * @return configured {@link SFTPChannel} instance
	 * @throws ConnectionException
	 *             to be thrown when there is a exception during connection with
	 *             server
	 */
	public static SFTPChannel createSFTPChannel(SFTPServer sftpServer) throws ConnectionException {
		PacketUploaderUtils.checkConfiguration(sftpServer);

		JSch jsch = new JSch();
		if (sftpServer.getPrivateKeyFileName() != null) {
			PacketUploaderUtils.addIdentity(jsch, sftpServer);
		}
		Session session = PacketUploaderUtils.configureSession(jsch, sftpServer);
		SFTPChannel sftpChannel = null;
		try {
			session.connect();
			Channel channel = session.openChannel(PacketUploaderConstant.STR_SFTP.getValue());
			sftpChannel = new SFTPChannel((ChannelSftp) channel, sftpServer);
			channel.connect();
		} catch (JSchException e) {
			throw new ConnectionException(PacketUploaderExceptionConstant.MOSIP_CONNECTION_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_CONNECTION_EXCEPTION.getErrorMessage(), e);
		}
		return sftpChannel;
	}

	/**
	 * Uploades file to server <i>(this method will not create destination folder it
	 * should be already present)</i>
	 * 
	 * @param sftpChannel
	 *            configured {@link SFTPChannel} instance
	 * @param source
	 *            path of packet to be uploaded
	 */
	public static void upload(SFTPChannel sftpChannel, String source) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		PacketUploaderUtils.check(source);
		String target = sftpChannel.getSftpServer().getSftpRemoteDirectory();
		try {
			channelSftp.put(source, target);
		} catch (SftpException e) {
			throw new SFTPException(PacketUploaderExceptionConstant.MOSIP_SFTP_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_SFTP_EXCEPTION.getErrorMessage(), e);
		}
	}

	/**
	 * This releases the obtained Connection to server
	 * 
	 * @param sftpChannel
	 *            configured {@link SFTPChannel} instance
	 */
	public static void releaseConnection(SFTPChannel sftpChannel) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		Session session = null;
		try {
			session = channelSftp.getSession();
		} catch (JSchException e) {
			throw new NoSessionException(
					PacketUploaderExceptionConstant.MOSIP_NO_SESSION_FOUND_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NO_SESSION_FOUND_EXCEPTION.getErrorMessage(), e);
		}
		channelSftp.exit();
		if (session != null) {
			session.disconnect();
		}
	}

}
