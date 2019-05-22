package io.mosip.admin.uinmgmt.service;

import io.mosip.admin.uinmgmt.dto.UinGenerationStatusResponseDto;

public interface UinGenerationStatusService {
	
	public UinGenerationStatusResponseDto getPacketStatus(String rid);

}
