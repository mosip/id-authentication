package io.mosip.admin.packetstatusupdater.dto;

import java.util.List;

import lombok.Data;

@Data
public class PacketStatusUpdateResponseDto {

	/** The packet status update list. */
	private List<PacketStatusUpdateDto> packetStatusUpdateList;
}
