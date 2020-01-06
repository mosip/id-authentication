package io.mosip.registration.processor.request.handler.service.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class PacketUtil.
 * 
 * @author Sowmya
 */
public class PacketUtil {

	/**
	 * Gets the packet names.
	 *
	 * @param packets
	 *            the packets
	 * @return the packet names
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
