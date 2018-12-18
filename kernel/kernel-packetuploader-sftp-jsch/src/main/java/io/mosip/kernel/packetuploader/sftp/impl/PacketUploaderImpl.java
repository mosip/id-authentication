package io.mosip.kernel.packetuploader.sftp.impl;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.kernel.core.packetuploader.exception.ConnectionException;
import io.mosip.kernel.core.packetuploader.exception.NoSessionException;
import io.mosip.kernel.core.packetuploader.exception.SFTPException;
import io.mosip.kernel.core.packetuploader.spi.PacketUploader;
import io.mosip.kernel.packetuploader.sftp.model.SFTPChannel;
import io.mosip.kernel.packetuploader.sftp.model.SFTPServer;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.sftp.util.PacketUploaderUtils;

/**
 * Packet uploader SFTP
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class PacketUploaderImpl implements PacketUploader<SFTPServer, SFTPChannel> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.packetuploader.sftp.PacketUploadera#createSFTPChannel(io.mosip.kernel.packetuploader.sftp.model.SFTPServer)
	 */
	public SFTPChannel createSFTPChannel(SFTPServer sftpServer)  {
		PacketUploaderUtils.checkConfiguration(sftpServer);
		PacketUploaderUtils.checkKey(sftpServer);
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
					PacketUploaderExceptionConstant.MOSIP_CONNECTION_EXCEPTION.getErrorMessage() + PacketUploaderConstant.EXCEPTION_BREAKER.getValue() + e.getMessage(), e);
		}
		return sftpChannel;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.packetuploader.sftp.PacketUploadera#upload(io.mosip.kernel.packetuploader.sftp.model.SFTPChannel, java.lang.String)
	 */
	public void upload(SFTPChannel sftpChannel, String source) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		PacketUploaderUtils.check(source);
		String target = sftpChannel.getSftpServer().getSftpRemoteDirectory();
		try {
			channelSftp.put(source, target);
		} catch (SftpException e) {
			throw new SFTPException(PacketUploaderExceptionConstant.MOSIP_SFTP_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_SFTP_EXCEPTION.getErrorMessage() + PacketUploaderConstant.EXCEPTION_BREAKER.getValue() + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.packetuploader.sftp.PacketUploadera#releaseConnection(io.mosip.kernel.packetuploader.sftp.model.SFTPChannel)
	 */
	public void releaseConnection(SFTPChannel sftpChannel) {
		ChannelSftp channelSftp = sftpChannel.getChannelSftp();
		Session session = null;
		try {
			session = channelSftp.getSession();
		} catch (JSchException e) {
			throw new NoSessionException(
					PacketUploaderExceptionConstant.MOSIP_NO_SESSION_FOUND_EXCEPTION.getErrorCode(),
					PacketUploaderExceptionConstant.MOSIP_NO_SESSION_FOUND_EXCEPTION.getErrorMessage() + PacketUploaderConstant.EXCEPTION_BREAKER.getValue() + e.getMessage(), e);
		}
		channelSftp.exit();
		if (session != null) {
			session.disconnect();
		}
	}

}
