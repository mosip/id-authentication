package io.mosip.registration.processor.core.spi.packetinfo.service.impl;

import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager;

/**
 * 
 * @author M1048399
 *
 */
public class PacketInfoManagerImpl implements PacketInfoManager<PacketInfo,BiometericData,Document> {

	@Override
	public boolean savePacketInfo(PacketInfo packetInfo) {
		return false;
	}

	@Override
	public boolean saveBioMetricInfo(BiometericData bioMetricData) {
		return false;
	}

	@Override
	public boolean saveDemograficData(Document demograficData) {
		return false;
	}

	

}
