package org.kernel.packetstatusupdater.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.packetstatusupdater.dto.PacketStatusUpdateDto;
import io.mosip.kernel.core.packetstatusupdater.spi.PacketStatusUpdateService;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

public class PacketStatusUpdateServiceImpl implements PacketStatusUpdateService {

	@Autowired
	private ZoneUtils zoneUtils;
	
	
	
	@Override
	public PacketStatusUpdateDto getStatus(String rid) {
		
		return null;
	}

}
