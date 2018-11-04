package io.mosip.kernel.packetuploader.sftp.model;

import com.jcraft.jsch.ChannelSftp;

import lombok.Data;

/**
 * SftpChannel class for MOSIP contains {@link #channelSftp} and
 * {@link #sftpServer}
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Data
public class SFTPChannel {

	/**
	 * {@link ChannelSftp} parameter
	 */
	private ChannelSftp channelSftp;
	/**
	 * {@link SFTPServer} parameter
	 */
	private SFTPServer sftpServer;

	/**
	 * Constructor for this class
	 * 
	 * @param channelSftp
	 *            {@link #channelSftp} parameter
	 * @param configuration
	 *            {@link #sftpServer} parameter
	 */
	public SFTPChannel(ChannelSftp channelSftp, SFTPServer configuration) {
		this.channelSftp = channelSftp;
		this.sftpServer = configuration;
	}


}
