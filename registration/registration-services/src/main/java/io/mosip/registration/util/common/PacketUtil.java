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

	/**
	 * This method takes the list of packets that are in file format as an input and
	 * returns the list of names of those packets by splitting the file names using
	 * the dot as delimiter.
	 *
	 * @param packets
	 *            - the list of packets in file format
	 * @return the list of file names of the packet
	 */
	public List<String> getPacketNames(List<File> packets) {
		List<String> packetNames = new ArrayList<>();
		for (File packet : packets) {
			String[] packetName = packet.getName().split("\\.");
			packetNames.add(packetName[0]);
		}

		return packetNames;
	}
}
