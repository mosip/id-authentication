package io.mosip.kernel.sftppacketuploader.channel;

import com.jcraft.jsch.ChannelSftp;

import io.mosip.kernel.sftppacketuploader.constant.PacketUploaderConfiguration;

/**
 * SftpChannel class for MOSIP contains {@link #channelSftp} and
 * {@link #configuration}
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SftpChannel {

	/**
	 * {@link ChannelSftp} parameter
	 */
	private ChannelSftp channelSftp;
	/**
	 * {@link PacketUploaderConfiguration} parameter
	 */
	private PacketUploaderConfiguration configuration;

	/**
	 * Constructor for this class
	 * 
	 * @param channelSftp
	 *            {@link #channelSftp} parameter
	 * @param configuration
	 *            {@link #configuration} parameter
	 */
	public SftpChannel(ChannelSftp channelSftp, PacketUploaderConfiguration configuration) {
		this.channelSftp = channelSftp;
		this.configuration = configuration;
	}

	/**
	 * Getter for {@link #configuration}
	 * 
	 * @return {@link #configuration}
	 */
	public PacketUploaderConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Getter for {@link #channelSftp}
	 * 
	 * @return {@link #channelSftp}
	 */
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

}
