package org.mosip.kernel.packetuploader.channel;

import org.mosip.kernel.packetuploader.constants.PacketUploaderConfiguration;

import com.jcraft.jsch.ChannelSftp;

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
	 * @param configuration
	 */
	public SftpChannel(ChannelSftp channelSftp, PacketUploaderConfiguration configuration) {
		this.channelSftp = channelSftp;
		this.configuration = configuration;
	}

	/**
	 * getter for {@link #configuration}
	 * 
	 * @return
	 */
	public PacketUploaderConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * getter for {@link #channelSftp}
	 * 
	 * @return
	 */
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

}
