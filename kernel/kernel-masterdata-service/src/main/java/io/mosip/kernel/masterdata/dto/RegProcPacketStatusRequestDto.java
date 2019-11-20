package io.mosip.kernel.masterdata.dto;

import java.util.List;

import io.mosip.kernel.core.packetstatusupdater.dto.PacketUpdateStatusRequestDto;
import lombok.Data;

@Data
public class RegProcPacketStatusRequestDto {

	/** The packet status update list. */
	private List<PacketUpdateStatusRequestDto> packetStatusUpdateList;
}
