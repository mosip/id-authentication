package org.mosip.kernel.packetuploader.channel;

import org.mosip.kernel.packetuploader.constants.PacketUploaderConfiguration;

import com.jcraft.jsch.ChannelSftp;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SftpChannel {

	/**
	 * 
	 */
	private ChannelSftp channelSftp;
	/**
	 * 
	 */
	private PacketUploaderConfiguration configuration;

	/**
	 * @param channelSftp
	 * @param configuration
	 */
	public SftpChannel(ChannelSftp channelSftp, PacketUploaderConfiguration configuration) {
		this.channelSftp = channelSftp;
		this.configuration = configuration;
	}

	/**
	 * @return
	 */
	public PacketUploaderConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return
	 */
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

}
