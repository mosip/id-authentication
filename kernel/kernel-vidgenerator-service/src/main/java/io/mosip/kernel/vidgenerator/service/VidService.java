package io.mosip.kernel.vidgenerator.service;

import java.time.LocalDateTime;

import io.mosip.kernel.vidgenerator.dto.VidFetchResponseDto;
import io.mosip.kernel.vidgenerator.entity.VidEntity;

public interface VidService {
	
	VidFetchResponseDto fetchVid(LocalDateTime expiry);
	
	long fetchVidCount(String status);

	void expireAndRenew();

	boolean saveVID(VidEntity vid);

}
