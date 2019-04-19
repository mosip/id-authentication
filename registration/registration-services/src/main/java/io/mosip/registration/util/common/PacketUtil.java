package io.mosip.registration.util.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * class for Packet Util
 * 
 * @author saravanakumar gnanaguru
 *
 */
public class PacketUtil {

	public List<String> getPacketNames(List<File> packets) {
		List<String> packetNames = new ArrayList<>();
		for (File packet : packets) {
			String[] packetName = packet.getName().split("\\.");
			packetNames.add(packetName[0]);
		}

		return packetNames;
	}
}
