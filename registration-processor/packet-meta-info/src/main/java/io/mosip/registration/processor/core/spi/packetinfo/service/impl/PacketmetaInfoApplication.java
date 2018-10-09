package io.mosip.registration.processor.core.spi.packetinfo.service.impl;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager;
/**
 * 
 * @author M1048399
 *
 */
public class PacketmetaInfoApplication {
	private static PacketInfoManager packetInfoManager;
	public static void main(String[] args) {
		try {
			packetInfoManager = new PacketInfoManagerImpl();
			PacketInfo packetInfo = (PacketInfo) JsonUtils.jsonFileToJavaObject(PacketInfo.class,"..\\packet-meta-info\\src\\main\\resources\\PacketMetaInfo.json");
			if(packetInfoManager.savePacketInfo(packetInfo)) {
				System.out.println("Saved");
			}else {
				System.out.println("Not Saved");
			}
		} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException e) {
			e.printStackTrace();
		}
	}
}
