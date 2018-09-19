package org.mosip.kernel.packetuploader.channel;

import org.mosip.kernel.packetuploader.constants.PacketUploaderConfiguration;

import com.jcraft.jsch.ChannelSftp;

public class SftpChannel {

	private ChannelSftp channelSftp;
	private PacketUploaderConfiguration configuration;

	public SftpChannel() {
	}
	
	public SftpChannel(ChannelSftp channelSftp,PacketUploaderConfiguration configuration) {
		this.channelSftp = channelSftp;
		this.configuration = configuration;
	}
	
	public PacketUploaderConfiguration getConfiguration() {
		return configuration;
	}

    public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

	

}
